package com.sufi.module.service.quote;

import com.sufi.commons.IProcessorClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/quote")
@CrossOrigin(origins = "http://localhost:3000")
public class QuoteController {

    @Autowired
    private IProcessorClient processorClient;

    @GetMapping("/get")
    public Mono<QuoteResponse> obtenerQuote(@RequestParam String keyOption) {
        Instant startTime = Instant.now();
        QuoteRequest request = new QuoteRequest(keyOption);

        return processorClient.quote(request)
                .doOnSubscribe(subscription -> System.out.println("Iniciando la petición de quote..."))
                .doOnSuccess(response -> {
                    Duration duration = Duration.between(startTime, Instant.now());
                    System.out.println("Petición completada en: " + duration.toMillis() + " ms");
                });
    }
}