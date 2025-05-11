package com.sufi.commons.client;

import com.sufi.commons.IProcessorClient;
import com.sufi.commons.service.ProviderOptionsService;
import com.sufi.module.dto.Alojamiento;
import com.sufi.module.dto.DataBaseDto;
import com.sufi.module.service.availability.AvailabilityRequest;
import com.sufi.module.service.availability.AvailabilityResponse;
import com.sufi.module.service.confirm.ConfirmRequest;
import com.sufi.module.service.confirm.ConfirmResponse;
import com.sufi.module.service.quote.QuoteRequest;
import com.sufi.module.service.quote.QuoteResponse;
import com.sufi.module.util.KeyOptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class ProcessorClient implements IProcessorClient {

    private final WebClient webClient;

    @Autowired
    private ProviderOptionsService providerOptionsService;

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

        List<Mono<List<AvailabilityResponse>>> responses = request.getListingId().stream()
                .map(id -> webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/check-availability")
                                .queryParam("fecha_entrada", request.getFechaEntrada().format(formatter))
                                .queryParam("fecha_salida", request.getFechaSalida().format(formatter))
                                .queryParam("listing_id", id)
                                .queryParam("occupants", request.getOccupancy())
                                .build())
                        .retrieve()
                        .bodyToFlux(AvailabilityResponse.class)
                        .collectList()
                        .map(responsesList -> responsesList.stream()
                                .map(response -> {
                                    //keyoption
                                    String keyOption = createKeyOptionForQuote(response.getAlojamiento().getListing(), request);

                                    return new AvailabilityResponse(
                                            response.getAlojamiento(),
                                            response.getPrecioPorDia(),
                                            keyOption,
                                            response.getPoliticas_cancelacion(),
                                            response.getImagen()
                                    );
                                })
                                .toList()))
                .toList();

        return Flux.merge(responses).flatMap(Flux::fromIterable).collectList();
    }

    @Override
    public Mono<QuoteResponse> quote(QuoteRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        KeyOptionUtil.ParsedKeyOption parsed = KeyOptionUtil.parse(request.getKeyOption());

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/quote")
                        .queryParam("fecha_entrada", parsed.fechaEntrada().format(formatter))
                        .queryParam("fecha_salida", parsed.fechaSalida().format(formatter))
                        .queryParam("listing_id", parsed.listingId())
                        .queryParam("num_personas", parsed.occupancy())
                        .build())
                .retrieve()
                .bodyToMono(QuoteResponse.class)
                .map(resp -> new QuoteResponse(
                        resp.getAlojamiento(),
                        resp.getPrecioPorDia(),
                        "EUR",
                        resp.getPoliticas_cancelacion(),
                        resp.getImagen()));
    }

    @Override
    public Mono<ConfirmResponse> confirm(ConfirmRequest request) {
        return webClient.post()
                .uri("/confirm")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ConfirmResponse.class)
                .map(resp -> new ConfirmResponse(
                        resp.getMensaje(),
                        resp.getReserva()
                ));
    }


    private String createKeyOptionForQuote(int listingId, AvailabilityRequest request) {
        return String.format("%d|%s|%s|%s",
                listingId,
                request.getFechaEntrada().format(DateTimeFormatter.ISO_LOCAL_DATE),
                request.getFechaSalida().format(DateTimeFormatter.ISO_LOCAL_DATE),
                request.getOccupancy());
    }

    @Override
    public Mono<List<AvailabilityResponse>> obtenerDisponibilidadPorCiudad(String ciudad, AvailabilityRequest request) {
        List<DataBaseDto> alojamientos = getAlojamientosPorCiudad(ciudad);

        List<Integer> listingIds = new ArrayList<>();
        for (DataBaseDto alojamiento : alojamientos) {
            listingIds.add(alojamiento.getListing());
        }

        request.setListingId(listingIds);

        return getDisponibilidad(request);
    }

    @Override
    public Mono<List<AvailabilityResponse>> obtenerDisponibilidadPorCiudadCache(String ciudad, AvailabilityRequest request) {
        List<DataBaseDto> alojamientos = getAlojamientosPorCiudad(ciudad);

        List<Integer> listingIds = new ArrayList<>();
        for (DataBaseDto alojamiento : alojamientos) {
            listingIds.add(alojamiento.getListing());
        }

        request.setListingId(listingIds);

        return getDisponibilidadCache(request);
    }

    private List<DataBaseDto> getAlojamientosPorCiudad(String ciudad) {
        List<DataBaseDto> alojamientos = new ArrayList<>();
        String query = "SELECT * FROM alojamientos WHERE ciudad = ?";
        String url = getDatabaseUrl();

        if (url == null) {
            System.err.println("No se pudo obtener la URL de la base de datos");
            return alojamientos;
        }

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, ciudad);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DataBaseDto alojamiento = new DataBaseDto(
                            rs.getInt("hotCodigo"),
                            rs.getString("ciudad"),
                            rs.getInt("imagen_id"),
                            rs.getBoolean("disponible"),
                            rs.getInt("occupants"),
                            rs.getString("direccion"),
                            rs.getInt("listing"),
                            rs.getString("nombre"),
                            rs.getString("pais")
                    );
                    alojamientos.add(alojamiento);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alojamientos;
    }

    @Override
    public Mono<List<AvailabilityResponse>> getDisponibilidadCache(AvailabilityRequest request) {
        return Flux.fromIterable(request.getListingId())
                .flatMap(id -> {
                    String options = getRequestStr(request, id);
                    Alojamiento alojamiento = new Alojamiento();
                    alojamiento.setListing(id);

                    return providerOptionsService.obtenerPorIdO(options)
                            .collectList()
                            .map(responsesList -> responsesList.stream()
                                    .map(response -> {
                                        String keyOption = createKeyOptionForQuote(id, request);
                                        return new AvailabilityResponse(
                                                alojamiento,
                                                response.getP(),
                                                keyOption,
                                                null,
                                                null
                                        );
                                    })
                                    .toList());
                })
                .flatMapIterable(list -> list)
                .collectList();
    }

    private String getRequestStr(AvailabilityRequest request, int listingId) {
        // Tomamos la fecha de entrada y la salida
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(request.getFechaEntrada().format(formatter), formatter);
        LocalDate endDate = LocalDate.parse(request.getFechaSalida().format(formatter), formatter);

        // Calculamos la duración
        int duracion = (int) (endDate.toEpochDay() - startDate.toEpochDay());

        // Generamos el string en el formato: listingId_fechaEntrada_duracion_ocupantes
        return listingId + "_" + startDate + "_" + duracion + "_" + request.getOccupancy();
    }

    private String getDatabaseUrl() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("No se encontró el archivo application.properties");
                return null;
            }
            props.load(input);
            return props.getProperty("database.sqlite.url");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}