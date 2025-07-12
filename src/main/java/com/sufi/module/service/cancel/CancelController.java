package com.sufi.module.service.cancel;

import com.sufi.commons.IProcessorClient;
import com.sufi.commons.service.ReservaService;
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
public class CancelController {
    @Autowired
    private IProcessorClient processorClient;

    @Autowired
    private ReservaService reservaService;

    @PostMapping("/cancel")
    public Mono<CancelResponse> obtenerCancelacion(@RequestBody CancelRequest request) {
        Instant startTime = Instant.now();
        return processorClient.cancel(request)
                .doOnSubscribe(subscription -> System.out.println("Iniciando la petición de Cancel..."))
                .doOnSuccess(response -> {
                    Duration duration = Duration.between(startTime, Instant.now());
                    System.out.println("Petición completada en: " + duration.toMillis() + " ms");
                    String mensajeCancelacion = reservaService.deleteReserva(request);
                });
    }
}
