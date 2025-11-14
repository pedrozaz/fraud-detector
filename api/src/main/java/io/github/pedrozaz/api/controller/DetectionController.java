package io.github.pedrozaz.api.controller;

import io.github.pedrozaz.api.dto.FraudCheckResponse;
import io.github.pedrozaz.api.dto.TransactionRequest;
import io.github.pedrozaz.api.model.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/detect")
@Slf4j
public class DetectionController {

    @PostMapping
    public ResponseEntity<FraudCheckResponse> checkTransaction(@RequestBody TransactionRequest request) {
        log.info("Received transaction check request for user: {}", request.userId());

        // for tests
        FraudCheckResponse response = new FraudCheckResponse(
                UUID.randomUUID().toString(),
                StatusEnum.APPROVED,
                false,
                .05
        );

        return ResponseEntity.ok(response);
    }
}
