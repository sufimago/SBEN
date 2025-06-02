package com.sufi.module.service.cancel;

import com.sufi.commons.IProcessorClient;
import com.sufi.commons.service.ReservaService;
import com.sufi.module.service.confirm.ConfirmRequest;
import com.sufi.module.service.confirm.ConfirmResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class CancelController {
    @Autowired
    private IProcessorClient processorClient;

    @Autowired
    private ReservaService reservaService;

    @PostMapping("/cancel")
    public Mono<CancelResponse> obtenerCancelacion(@RequestBody CancelRequest request) {
        Instant startTime = Instant.now();
        return processorClient.cancel(request)
                .doOnSubscribe(subscription -> System.out.println("Iniciando la petición de confirm..."))
                .doOnSuccess(response -> {
                    Duration duration = Duration.between(startTime, Instant.now());
                    System.out.println("Petición completada en: " + duration.toMillis() + " ms");
                    String mensajeCancelacion = reservaService.deleteReserva(request);
                });
    }
}
