package com.sufi.module.service.availability;

import com.sufi.commons.IProcessorClient;
import com.sufi.commons.service.ProviderOptionsService;
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

//    @GetMapping("/provider-options")
//    public List<ProviderOptions> obtenerProviderOptions(
//            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
//            @RequestParam("duracion") int duracion,
//            @RequestParam("ocupantes") int ocupantes) {
//
//        List<ProviderOptions> results = providerOptionsService.obtenerPorListingId(9000, fecha, duracion, ocupantes);
//
//        if (results.isEmpty()) {
//            System.out.println("No se encontraron resultados.");
//            return new ArrayList<>();
//        } else {
//            System.out.println("Se encontraron " + results.size() + " resultados.");
//            return results;
//        }
//    }


    @GetMapping("/disponibilidad")
    public Mono<List<AvailabilityResponse>> obtenerDisponibilidad(
            @RequestParam String ciudad,
            @RequestParam String fechaEntrada,
            @RequestParam String fechaSalida,
            @RequestParam Integer occupancy,
            @RequestParam(defaultValue = "false") boolean useCache
    ) {
        Instant startTime = Instant.now();
        AvailabilityRequest request = construirDisponibilidadRequest(fechaEntrada, fechaSalida, occupancy);

        Mono<List<AvailabilityResponse>> resultado = useCache
                ? processorClient.obtenerDisponibilidadPorCiudadCache(ciudad, request)
                : processorClient.obtenerDisponibilidadPorCiudad(ciudad, request);

        return resultado
                .doOnSubscribe(subscription -> System.out.println("Iniciando la petición para ciudad: " + ciudad))
                .doOnSuccess(response -> {
                    Duration duration = Duration.between(startTime, Instant.now());
                    System.out.println("Petición completada en: " + duration.toMillis() + " ms");
                });
    }
}
