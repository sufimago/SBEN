package com.sufi.module.service.availability;

import com.sufi.commons.IProcessorClient;
import com.sufi.commons.service.ProviderOptionsService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AvailabilityController {

    boolean isCache = true;
    @Autowired
    private IProcessorClient processorClient;

    @Autowired
    private MeterRegistry meterRegistry;
    @Autowired
    private ProviderOptionsService providerOptionsService;
    private boolean cache;

    @GetMapping("/obtenerDisponibilidadPorCiudad")
    public Mono<List<AvailabilityResponse>> obtenerDisponibilidadPorCiudad(
            @RequestParam String ciudad,
            @RequestParam String fechaEntrada,
            @RequestParam String fechaSalida,
            @RequestParam Integer occupancy
    ) {
        Instant startTime = Instant.now();
        AvailabilityRequest request = construirDisponibilidadRequest(fechaEntrada, fechaSalida, occupancy);

        return processorClient.obtenerDisponibilidadPorCiudad(ciudad, request)
                .doOnSubscribe(subscription -> System.out.println("Iniciando la petición para ciudad: " + ciudad))
                .doOnSuccess(response -> {
                    Duration duration = Duration.between(startTime, Instant.now());
                    System.out.println("Petición completada en: " + duration.toMillis() + " ms");
                });
    }


    public AvailabilityRequest construirDisponibilidadRequest(String fechaEntrada, String fechaSalida, Integer occupancy) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDate = LocalDate.parse(fechaEntrada, formatter).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(fechaSalida, formatter).atStartOfDay();

        return new AvailabilityRequest(startDate, endDate, new ArrayList<>(), occupancy);
    }

    @GetMapping("/obtenerAlojamientos")
    public Mono<String> obtenerAlojamientos() {
        return processorClient.getAlojamientos();
    }

    @GetMapping("/obtenerDisponibilidad")
    public Mono<List<AvailabilityResponse>> obtenerDisponibilidad(
            @RequestParam String ciudad,
            @RequestParam String fechaEntrada,
            @RequestParam String fechaSalida,
            @RequestParam Integer occupancy
    ) {
        cache = true;
        AvailabilityRequest request = construirDisponibilidadRequest(fechaEntrada, fechaSalida, occupancy);
        Timer.Sample sample = Timer.start(meterRegistry);

        meterRegistry.counter("availability_cache_usage",
                        "ciudad", ciudad,
                        "cache", String.valueOf(cache))
                .increment();

        Mono<List<AvailabilityResponse>> resultado = cache
                ? processorClient.obtenerDisponibilidadPorCiudadCache(ciudad, request)
                : processorClient.obtenerDisponibilidadPorCiudad(ciudad, request);

        return resultado
                .doOnSuccess(response -> sample.stop(Timer.builder("availability_request_duration_seconds")
                        .description("Duración de la solicitud de disponibilidad")
                        .tag("ciudad", ciudad)
                        .tag("cache", String.valueOf(cache))
                        .register(meterRegistry)))
                .doOnError(error -> {
                    // Registrar errores
                    meterRegistry.counter("availability_errors",
                                    "ciudad", ciudad,
                                    "error", error.getClass().getSimpleName())
                            .increment();
                });
    }
}
