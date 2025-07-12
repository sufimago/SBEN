package com.sufi.module.service.availability;

import com.sufi.commons.IProcessorClient;
import com.sufi.commons.service.AvailabilityService;
import com.sufi.commons.service.ProviderOptionsService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AvailabilityController {

    @Autowired
    private IProcessorClient processorClient;

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private ProviderOptionsService providerOptionsService;

    @Autowired
    private AvailabilityService availabilityService;

    private static final Logger logger = Logger.getLogger(AvailabilityController.class.getName());

    // Contador de requests en progreso
    private final AtomicInteger requestsInProgress = new AtomicInteger(0);

    public AvailabilityController(IProcessorClient processorClient,
                                  MeterRegistry meterRegistry,
                                  ProviderOptionsService providerOptionsService,
                                  AvailabilityService availabilityService) {
        this.processorClient = processorClient;
        this.meterRegistry = meterRegistry;
        this.providerOptionsService = providerOptionsService;
        this.availabilityService = availabilityService;
    }

    @GetMapping("/obtenerDisponibilidadPorCiudad")
    public Mono<List<AvailabilityResponse>> obtenerDisponibilidadPorCiudad(
            @RequestParam String ciudad,
            @RequestParam String fechaEntrada,
            @RequestParam String fechaSalida,
            @RequestParam Integer occupancy) {

        return processRequest(ciudad, fechaEntrada, fechaSalida, occupancy, false);
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
            @RequestParam Integer occupancy,
            @RequestParam boolean cache) {

        return processRequest(ciudad, fechaEntrada, fechaSalida, occupancy, cache);
    }

    private Mono<List<AvailabilityResponse>> processRequest(
            String ciudad,
            String fechaEntrada,
            String fechaSalida,
            Integer occupancy,
            boolean useCache) {

        Instant startTime = Instant.now();
        AvailabilityRequest request = availabilityService.construirDisponibilidadRequest(fechaEntrada, fechaSalida, occupancy);

        // Métricas de inicio
        requestsInProgress.incrementAndGet();
        meterRegistry.gauge("requests_in_progress", Tags.of("ciudad", ciudad, "cache", String.valueOf(useCache)),
                requestsInProgress);

        meterRegistry.counter("availability_requests_total",
                        "ciudad", ciudad,
                        "cache", String.valueOf(useCache))
                .increment();

        Timer.Sample sample = Timer.start(meterRegistry);

        Mono<List<AvailabilityResponse>> resultado = useCache
                ? processorClient.obtenerDisponibilidadPorCiudadCache(ciudad, request)
                : processorClient.obtenerDisponibilidadPorCiudad(ciudad, request);

        return resultado
                .doOnSuccess(response -> {
                    // Registro de éxito y tiempo de respuesta
                    long durationMs = Duration.between(startTime, Instant.now()).toMillis();

                    sample.stop(Timer.builder("availability_request_duration_seconds")
                            .description("Duración de la solicitud de disponibilidad")
                            .tag("ciudad", ciudad)
                            .tag("cache", String.valueOf(useCache))
                            .publishPercentiles(0.5, 0.95, 0.99)
                            .serviceLevelObjectives(Duration.ofMillis(100), Duration.ofMillis(500), Duration.ofSeconds(1))
                            .register(meterRegistry));

                    // Tamaño de la respuesta
                    meterRegistry.summary("availability_response_size",
                                    "ciudad", ciudad,
                                    "cache", String.valueOf(useCache))
                            .record(response.size());

                    // Contador de éxitos
                    meterRegistry.counter("availability_success_total",
                                    "ciudad", ciudad,
                                    "cache", String.valueOf(useCache))
                            .increment();

                    logger.info(String.format("Petición completada en %d ms para %s (cache: %b)",
                            durationMs, ciudad, useCache));
                })
                .doOnError(error -> {
                    // Registro mejorado de errores
                    String errorType = error.getClass().getSimpleName();
                    String errorMessage = error.getMessage() != null ? error.getMessage() : "null";
                    int statusCode = 0; // Default para errores no HTTP

                    if (error instanceof WebClientResponseException) {
                        statusCode = ((WebClientResponseException) error).getStatusCode().value();
                    }

                    // Registro del error general
                    meterRegistry.counter("availability_errors_total",
                                    "ciudad", ciudad,
                                    "cache", String.valueOf(useCache),
                                    "error_type", errorType,
                                    "error_message", errorMessage)
                            .increment();

                    // Registro específico para errores HTTP
                    if (statusCode > 0) {
                        meterRegistry.counter("availability_http_errors",
                                        "ciudad", ciudad,
                                        "cache", String.valueOf(useCache),
                                        "status_code", String.valueOf(statusCode),
                                        "error_type", errorType)
                                .increment();
                    }

                    logger.severe(String.format("Error al obtener disponibilidad para %s (cache: %b): %s - Código: %d",
                            ciudad, useCache, errorMessage, statusCode));
                })
                .doFinally(signalType -> {
                    // Actualización de métricas al finalizar
                    requestsInProgress.decrementAndGet();
                    meterRegistry.gauge("requests_in_progress", Tags.of("ciudad", ciudad, "cache", String.valueOf(useCache)),
                            requestsInProgress);

                    // Tiempo total de procesamiento
                    meterRegistry.timer("availability_total_processing_time",
                                    "ciudad", ciudad,
                                    "cache", String.valueOf(useCache))
                            .record(Duration.between(startTime, Instant.now()));
                });
    }
}