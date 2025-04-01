package com.sufi.commons.client;

import com.sufi.commons.IProcessorClient;
import com.sufi.module.service.Alojamiento;
import com.sufi.module.service.availability.AvailabilityRequest;
import com.sufi.module.service.availability.AvailabilityResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProcessorClient implements IProcessorClient {

    private final WebClient webClient;

    public ProcessorClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<String> getAlojamientos() {
        return webClient.get()
                .uri("/listings")
                .retrieve()
                .bodyToMono(String.class);
    }

    @Override
    public Mono<List<AvailabilityResponse>> getDisponibilidad(AvailabilityRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        Map<Integer, Integer> listingIdToHotCodeMap = createHotCodeToListingIdMap(request);

        List<Mono<List<AvailabilityResponse>>> responses = request.getListingId().stream()
                .map(id -> webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/check-availability")
                                .queryParam("fecha_entrada", request.getFechaEntrada().format(formatter))
                                .queryParam("fecha_salida", request.getFechaSalida().format(formatter))
                                .queryParam("listing_id", id)
                                .build())
                        .retrieve()
                        .bodyToFlux(AvailabilityResponse.class)
                        .collectList()
                        .map(responsesList -> responsesList.stream()
                                .map(response -> {
                                    // Obtener el nuevo ID del mapa
                                    Integer newListingId = listingIdToHotCodeMap.get(id);
                                    //keyoption
                                    String keyOption = createKeyOptionForQuote(response.getAlojamiento(), request);

                                    return new AvailabilityResponse(
                                            updateAlojamientoListingId(response.getAlojamiento(), newListingId),
                                            response.getPrecioPorDia(),
                                            keyOption
                                    );
                                })
                                .toList()))
                .toList();

        return Flux.merge(responses).flatMap(Flux::fromIterable).collectList();
    }

    private Integer getHotCodeForListingId(int listingId) {
        return listingId == 9006 ? 1111111 : 2222222;
    }

    private Alojamiento updateAlojamientoListingId(Alojamiento alojamiento, int hotCodigo) {
        return new Alojamiento(
                alojamiento.getNombre(),
                alojamiento.getImagen_id(),
                alojamiento.getDireccion(),
                hotCodigo,
                alojamiento.getPais(),
                alojamiento.getCiudad(),
                alojamiento.isDisponible()
        );
    }

    private Map<Integer, Integer> createHotCodeToListingIdMap(AvailabilityRequest request) {
        Map<Integer, Integer> listingIdToHotCodeMap = new HashMap<>();
        for (Integer listingId : request.getListingId()) {
            listingIdToHotCodeMap.put(listingId, getHotCodeForListingId(listingId));
        }
        return listingIdToHotCodeMap;
    }

    private String createKeyOptionForQuote(Alojamiento alojamiento, AvailabilityRequest request) {
        return String.format("%d|%s|%s",
                alojamiento.getListing(),
                request.getFechaEntrada().format(DateTimeFormatter.ISO_LOCAL_DATE),
                request.getFechaSalida().format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
}