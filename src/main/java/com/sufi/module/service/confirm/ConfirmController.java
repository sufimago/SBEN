package com.sufi.module.service.confirm;

import com.sufi.commons.IProcessorClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ConfirmController {
    @Autowired
    private IProcessorClient processorClient;

    @PostMapping("/confirm")
    public Mono<ConfirmResponse> obtenerConfirmacion(@RequestBody ConfirmRequest request) {
        Instant startTime = Instant.now();
        return processorClient.confirm(request)
                .doOnSubscribe(subscription -> System.out.println("Iniciando la petición de confirm..."))
                .doOnSuccess(response -> {
                    Duration duration = Duration.between(startTime, Instant.now());
                    System.out.println("Petición completada en: " + duration.toMillis() + " ms");
                });
    }
}
