package com.sufi.module.service.quote;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/quote")
public class QuoteController {

    @GetMapping("/get")
    public Mono<QuoteResponse> getQuote(@RequestParam String keyOption) {
        return Mono.fromCallable(() -> {
            String[] parts = keyOption.split("\\|");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Formato de keyOption inválido");
            }

            int hotCodigo = Integer.parseInt(parts[0]);
            LocalDate fechaEntrada = LocalDate.parse(parts[1]);
            LocalDate fechaSalida = LocalDate.parse(parts[2]);

            long noches = ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
            double precioTotal = noches * 100;

            return new QuoteResponse(
                    hotCodigo,
                    "Hotel " + hotCodigo,
                    precioTotal,
                    "EUR"
            );
        });
    }
}