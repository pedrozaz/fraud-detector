package io.github.pedrozaz.api.controller;

import io.github.pedrozaz.api.dto.FraudCheckResponse;
import io.github.pedrozaz.api.dto.TransactionRequest;
import io.github.pedrozaz.api.service.FraudDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/detect")
@RequiredArgsConstructor
@Slf4j
public class DetectionController {

    private final FraudDetectionService fraudService;

    @PostMapping
    public ResponseEntity<FraudCheckResponse> checkTransaction(@RequestBody TransactionRequest request) {
        log.info("Received transaction check request for user: {}", request.userId());

        FraudCheckResponse response = fraudService.checkTransaction(request);

        return ResponseEntity.ok(response);
    }
}
