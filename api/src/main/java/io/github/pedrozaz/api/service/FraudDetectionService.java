package io.github.pedrozaz.api.service;

import io.github.pedrozaz.api.dto.FraudCheckResponse;
import io.github.pedrozaz.api.dto.MLRequest;
import io.github.pedrozaz.api.dto.MLResponse;
import io.github.pedrozaz.api.dto.TransactionRequest;
import io.github.pedrozaz.api.model.StatusEnum;
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

    public FraudDetectionService(RestTemplate restTemplate,
                                 @Value("${ml.service.url}") String mlServiceUrl) {
        this.restTemplate = restTemplate;
        this.mlServiceUrl = mlServiceUrl;
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
        try {
            log.info("Sending request to ML service at {}: {}", mlServiceUrl, mlRequest);
            mlResponse = restTemplate.postForObject(mlServiceUrl + "/predict", mlRequest, MLResponse.class);

            if (mlResponse == null) {
                log.error("ML response is null response");
                return createFallbackResponse(false, .0);
            }
            log.info("Received ML Response: {}", mlResponse);
        } catch (RestClientException e) {
            log.error("Error calling ML service: {}", e.getMessage());
            return createFallbackResponse(true, .99);
        }

        StatusEnum status = getStatus(mlResponse);

        return new FraudCheckResponse(
                UUID.randomUUID().toString(),
                status,
                mlResponse.isFraud(),
                mlResponse.fraudScore()
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

    private FraudCheckResponse createFallbackResponse(boolean isFraud, double score) {
        StatusEnum status = isFraud ? StatusEnum.REJECTED : StatusEnum.APPROVED;
        return new FraudCheckResponse(
                UUID.randomUUID().toString(),
                status,
                isFraud,
                score
        );
    }
}
