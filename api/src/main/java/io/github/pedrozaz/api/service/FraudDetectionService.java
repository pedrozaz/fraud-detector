package io.github.pedrozaz.api.service;

import io.github.pedrozaz.api.dto.FraudCheckResponse;
import io.github.pedrozaz.api.dto.MLRequest;
import io.github.pedrozaz.api.dto.MLResponse;
import io.github.pedrozaz.api.dto.TransactionRequest;
import io.github.pedrozaz.api.model.StatusEnum;
import io.github.pedrozaz.api.model.Transaction;
import io.github.pedrozaz.api.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@Slf4j
public class FraudDetectionService {

    private final RestTemplate restTemplate;
    private final String mlServiceUrl;
    private final TransactionRepository transactionRepository;

    public FraudDetectionService(RestTemplate restTemplate,
                                 @Value("${ml.service.url}") String mlServiceUrl,
                                 TransactionRepository transactionRepository) {
        this.restTemplate = restTemplate;
        this.mlServiceUrl = mlServiceUrl;
        this.transactionRepository = transactionRepository;
    }

    public FraudCheckResponse checkTransaction(TransactionRequest request) {
        int txHour = request.timestamp().getHour();
        int deviceScore = 75;

        MLRequest mlRequest = new MLRequest(
                request.amount().doubleValue(),
                txHour,
                deviceScore
        );

        MLResponse mlResponse;
        StatusEnum status;

        try {
            log.info("Sending request to ML service at {}: {}", mlServiceUrl, mlRequest);
            mlResponse = restTemplate.postForObject(mlServiceUrl + "/predict", mlRequest, MLResponse.class);

            if (mlResponse == null) {
                log.error("ML response is null response");
                return saveTransaction(request, StatusEnum.REVIEW, false, .0);
            }
            log.info("Received ML Response: {}", mlResponse);
            status = getStatus(mlResponse);

        } catch (RestClientException e) {
            log.error("Error calling ML service: {}", e.getMessage());
            return saveTransaction(request, StatusEnum.REVIEW, false, .0);
        }

        return saveTransaction(
                request,
                status,
                mlResponse.isFraud(),
                mlResponse.fraudScore()
        );
    }

    private FraudCheckResponse saveTransaction(TransactionRequest request, StatusEnum status, boolean isFraud, double score) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setUserId(request.userId());
        transaction.setAmount(request.amount());
        transaction.setCurrency(request.currency());
        transaction.setTimestamp(request.timestamp());
        transaction.setCardId(request.cardId());
        transaction.setMerchantId(request.merchantId());

        transaction.setFraud(isFraud);
        transaction.setFraudScore(score);
        transaction.setStatus(status);

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Saved transaction with ID: {}", savedTransaction.getTransactionId());

        return new FraudCheckResponse(
                savedTransaction.getTransactionId(),
                savedTransaction.getStatus(),
                savedTransaction.isFraud(),
                savedTransaction.getFraudScore()
        );
    }

    private StatusEnum getStatus(MLResponse mlResponse) {
        if (mlResponse.isFraud()) {
            return StatusEnum.REJECTED;
        }
        if (mlResponse.fraudScore() > .3) {
            return StatusEnum.REVIEW;
        }
        return StatusEnum.APPROVED;
    }
}
