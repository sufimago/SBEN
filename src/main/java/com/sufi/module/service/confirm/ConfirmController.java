package com.sufi.module.service.confirm;

import com.sufi.commons.IProcessorClient;
import com.sufi.commons.service.ReservaService;
import com.sufi.module.dto.Reserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

import static com.sufi.HotelDatabase.getDatabaseUrl;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ConfirmController {
    @Autowired
    private IProcessorClient processorClient;

    @Autowired
    private ReservaService reservaService;

    @PostMapping("/confirm")
    public Mono<ConfirmResponse> obtenerConfirmacion(@RequestBody ConfirmRequest request) {
        Instant startTime = Instant.now();
        return processorClient.confirm(request)
                .doOnSubscribe(subscription -> System.out.println("Iniciando la petición de confirm..."))
                .doOnSuccess(response -> {
                    Duration duration = Duration.between(startTime, Instant.now());
                    System.out.println("Petición completada en: " + duration.toMillis() + " ms");
                    String mensajeReserva = reservaService.insertReserva(request, response);
                    System.out.println("Mensaje de reserva: " + mensajeReserva);
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode().value() == 422) {
                        System.err.println("Error 422 del proveedor: " + ex.getResponseBodyAsString());
                        return Mono.error(new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Datos inválidos enviados al proveedor"));
                    }
                    return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al comunicarse con el proveedor"));
                });
    }
}
