package com.sufi.module.service.availability;

import com.sufi.commons.IProcessorClient;
import com.sufi.commons.service.AvailabilityService;
import com.sufi.commons.service.ProviderOptionsService;
import com.sufi.module.dto.Alojamiento;
import com.sufi.module.dto.ProviderDataWebHook;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityControllerTest {
    @InjectMocks
    private AvailabilityController controller;

    @Mock
    private IProcessorClient processorClient;

    @Mock
    private AvailabilityService availabilityService;

    @Mock
    private ProviderOptionsService providerOptionsService;

    @Mock
    private MeterRegistry meterRegistry;

    private AvailabilityRequest mockRequest;
    private List<AvailabilityResponse> mockResponse;


    @BeforeEach
    public void setup() {
        mockRequest = new AvailabilityRequest();
        mockResponse = List.of(new AvailabilityResponse(), new AvailabilityResponse());

        meterRegistry = new SimpleMeterRegistry();

        controller = new AvailabilityController(
                processorClient,
                meterRegistry,
                providerOptionsService,
                availabilityService
        );
    }

    @Test
    void testObtenerDisponibilidadConCache() {
        String ciudad = "Bogotá";
        String fechaEntrada = "2026-12-11";
        String fechaSalida = "2026-12-12";
        int occupancy = 2;
        boolean useCache = true;

        AvailabilityResponse mockResponse1 = createMockResponse(ciudad, 1001, "calle 123", "Hotel Bogotá", "imagen1.jpg", 150.0);
        AvailabilityResponse mockResponse2 = createMockResponse(ciudad, 1002, "Avenida 456", "Apartamento Centro", "imagen2.jpg", 120.0);

        AvailabilityRequest mockRequest = new AvailabilityRequest(
                LocalDate.parse(fechaEntrada).atStartOfDay(),
                LocalDate.parse(fechaSalida).atStartOfDay(),
                new ArrayList<>(),
                occupancy
        );

        when(availabilityService.construirDisponibilidadRequest(
                eq(fechaEntrada),
                eq(fechaSalida),
                eq(occupancy)))
                .thenReturn(mockRequest);

        try (MockedStatic<DriverManager> mockedDriverManager = Mockito.mockStatic(DriverManager.class)) {
            Connection mockConnection = Mockito.mock(Connection.class);
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString())).thenReturn(mockConnection);

            when(processorClient.obtenerDisponibilidadPorCiudadCache(eq(ciudad), any(AvailabilityRequest.class)))
                    .thenReturn(Mono.just(List.of(mockResponse1, mockResponse2)));

            StepVerifier.create(controller.obtenerDisponibilidad(ciudad, fechaEntrada, fechaSalida, occupancy, useCache))
                    // Then
                    .expectNextMatches(responseList -> responseList.size() == 2 &&
                            responseList.stream().anyMatch(r -> r.getAlojamiento().getListing() == 1001) &&
                            responseList.stream().anyMatch(r -> r.getAlojamiento().getListing() == 1002))
                    .verifyComplete();

            assertEquals(1.0, meterRegistry.get("availability_requests_total").counter().count());
            verify(availabilityService, times(1)).construirDisponibilidadRequest(fechaEntrada, fechaSalida, occupancy);
            verify(processorClient, times(1)).obtenerDisponibilidadPorCiudadCache(eq(ciudad), any(AvailabilityRequest.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testObtenerDisponibilidadSinCache() {
        String ciudad = "Bogotá";
        String fechaEntrada = "2026-07-11";
        String fechaSalida = "2026-07-12";
        int occupancy = 2;
        boolean useCache = false;

        AvailabilityResponse mockResponse1 = createMockResponse(ciudad, 1001, "calle 123", "Hotel Bogotá", "imagen1.jpg", 150.0);
        AvailabilityResponse mockResponse2 = createMockResponse(ciudad, 1002, "Avenida 456", "Apartamento Centro", "imagen2.jpg", 120.0);

        AvailabilityRequest mockRequest = new AvailabilityRequest(
                LocalDate.parse(fechaEntrada).atStartOfDay(),
                LocalDate.parse(fechaSalida).atStartOfDay(),
                new ArrayList<>(),
                occupancy
        );

        when(availabilityService.construirDisponibilidadRequest(
                eq(fechaEntrada),
                eq(fechaSalida),
                eq(occupancy)))
                .thenReturn(mockRequest);

        try (MockedStatic<DriverManager> mockedDriverManager = Mockito.mockStatic(DriverManager.class)) {
            Connection mockConnection = Mockito.mock(Connection.class);
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString())).thenReturn(mockConnection);

            when(processorClient.obtenerDisponibilidadPorCiudad(eq(ciudad), any(AvailabilityRequest.class)))
                    .thenReturn(Mono.just(List.of(mockResponse1, mockResponse2)));

            StepVerifier.create(controller.obtenerDisponibilidad(ciudad, fechaEntrada, fechaSalida, occupancy, useCache))
                    .expectNextMatches(responseList -> responseList.size() == 2 &&
                            responseList.stream().anyMatch(r -> r.getAlojamiento().getListing() == 1001) &&
                            responseList.stream().anyMatch(r -> r.getAlojamiento().getListing() == 1002))
                    .verifyComplete();

            assertEquals(1.0, meterRegistry.get("availability_requests_total").counter().count());
            verify(availabilityService, times(1)).construirDisponibilidadRequest(fechaEntrada, fechaSalida, occupancy);
            verify(processorClient, times(1)).obtenerDisponibilidadPorCiudad(eq(ciudad), any(AvailabilityRequest.class));
        } catch (Exception e) {
            e.printStackTrace();
            fail("El test lanzó una excepción inesperada: " + e.getMessage());
        }
    }



    @Test
    public void testObtenerDisponibilidadSinCacheGeneral() {
        String ciudad = "Bogotá";
        String fechaEntrada = "2026-07-11"; // fecha futura para no chocar con la validación
        String fechaSalida = "2026-07-12";
        int occupancy = 2;
        boolean useCache = false;

        AvailabilityRequest mockRequest = new AvailabilityRequest(
                LocalDate.parse(fechaEntrada).atStartOfDay(),
                LocalDate.parse(fechaSalida).atStartOfDay(),
                new ArrayList<>(),
                occupancy
        );

        // Respuestas simuladas
        AvailabilityResponse mockResponse1 = createMockResponse(ciudad, 1001, "calle 123", "Hotel Bogotá", "imagen1.jpg", 150.0);
        AvailabilityResponse mockResponse2 = createMockResponse(ciudad, 1002, "Avenida 456", "Apartamento Centro", "imagen2.jpg", 120.0);
        List<AvailabilityResponse> mockResponse = List.of(mockResponse1, mockResponse2);

        // Stubbing
        when(availabilityService.construirDisponibilidadRequest(
                eq(fechaEntrada),
                eq(fechaSalida),
                eq(occupancy)))
                .thenReturn(mockRequest);

        when(processorClient.obtenerDisponibilidadPorCiudad(eq(ciudad), eq(mockRequest)))
                .thenReturn(Mono.just(mockResponse));

        // Verificación con StepVerifier
        StepVerifier.create(controller.obtenerDisponibilidad(ciudad, fechaEntrada, fechaSalida, occupancy, useCache))
                .expectNext(mockResponse)
                .verifyComplete();

        // Métricas
        assertEquals(1.0, meterRegistry.get("availability_requests_total").counter().count());

        // Verificación de interacciones
        verify(availabilityService, times(1)).construirDisponibilidadRequest(fechaEntrada, fechaSalida, occupancy);
        verify(processorClient, times(1)).obtenerDisponibilidadPorCiudad(eq(ciudad), eq(mockRequest));
    }

    @Test
    void testObtenerDisponibilidadConCacheGeneral() {
        String ciudad = "Bogotá";
        String fechaEntrada = "2026-07-11";
        String fechaSalida = "2026-07-12";
        int occupancy = 2;
        boolean useCache = true;

        AvailabilityRequest mockRequest = new AvailabilityRequest(
                LocalDate.parse(fechaEntrada).atStartOfDay(),
                LocalDate.parse(fechaSalida).atStartOfDay(),
                new ArrayList<>(),
                occupancy
        );

        AvailabilityResponse mockResponse1 = createMockResponse(ciudad, 1001, "calle 123", "Hotel Bogotá", "imagen1.jpg", 150.0);
        AvailabilityResponse mockResponse2 = createMockResponse(ciudad, 1002, "Avenida 456", "Apartamento Centro", "imagen2.jpg", 120.0);
        List<AvailabilityResponse> mockResponse = List.of(mockResponse1, mockResponse2);

        when(availabilityService.construirDisponibilidadRequest(eq(fechaEntrada), eq(fechaSalida), eq(occupancy)))
                .thenReturn(mockRequest);

        try (MockedStatic<DriverManager> mockedDriverManager = Mockito.mockStatic(DriverManager.class)) {
            Connection mockConnection = Mockito.mock(Connection.class);
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString())).thenReturn(mockConnection);

            when(processorClient.obtenerDisponibilidadPorCiudad(eq(ciudad), any(AvailabilityRequest.class)))
                    .thenReturn(Mono.just(mockResponse));

            when(processorClient.obtenerDisponibilidadPorCiudadCache(eq(ciudad), any(AvailabilityRequest.class)))
                    .thenReturn(Mono.just(mockResponse));

            StepVerifier.create(controller.obtenerDisponibilidad(ciudad, fechaEntrada, fechaSalida, occupancy, useCache))
                    .expectNextMatches(responseList ->
                            responseList.size() == 2 &&
                                    responseList.stream().anyMatch(r -> r.getAlojamiento().getListing() == 1001) &&
                                    responseList.stream().anyMatch(r -> r.getAlojamiento().getListing() == 1002))
                    .verifyComplete();

            assertEquals(1.0, meterRegistry.get("availability_requests_total").counter().count(), 0.0001);
            verify(availabilityService, times(1)).construirDisponibilidadRequest(fechaEntrada, fechaSalida, occupancy);
            verify(processorClient, times(1)).obtenerDisponibilidadPorCiudadCache(eq(ciudad), any(AvailabilityRequest.class));
        }
    }

    @Test
    public void testObtenerDisponibilidad_SinCache_Error() {
        String ciudad = "Medellín";
        String fechaEntrada = "2026-07-15"; // fecha futura
        String fechaSalida = "2026-07-20";
        int occupancy = 2;
        boolean useCache = false;

        // AvailabilityRequest real
        AvailabilityRequest mockRequest = new AvailabilityRequest(
                LocalDate.parse(fechaEntrada).atStartOfDay(),
                LocalDate.parse(fechaSalida).atStartOfDay(),
                new ArrayList<>(),
                occupancy
        );

        // Stubbing
        when(availabilityService.construirDisponibilidadRequest(
                eq(fechaEntrada), eq(fechaSalida), eq(occupancy)))
                .thenReturn(mockRequest);

        when(processorClient.obtenerDisponibilidadPorCiudad(eq(ciudad), eq(mockRequest)))
                .thenReturn(Mono.error(new RuntimeException("Fallo de red")));

        // Verificación con StepVerifier
        StepVerifier.create(controller.obtenerDisponibilidad(ciudad, fechaEntrada, fechaSalida, occupancy, useCache))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Fallo de red"))
                .verify();

        // Verificaciones adicionales
        verify(availabilityService, times(1)).construirDisponibilidadRequest(fechaEntrada, fechaSalida, occupancy);
        verify(processorClient, times(1)).obtenerDisponibilidadPorCiudad(eq(ciudad), eq(mockRequest));

        assertEquals(1.0, meterRegistry.get("availability_requests_total").counter().count(), 0.0001);
        assertEquals(1.0, meterRegistry.get("availability_errors_total").counter().count(), 0.0001);
        assertTrue(Objects.requireNonNull(meterRegistry.find("availability_total_processing_time").timer()).count() >= 1);
    }
    
    private AvailabilityResponse createMockResponse(String ciudad, int listing, String street, String name, String image, double price) {
        return new AvailabilityResponse(
                new Alojamiento(ciudad, null, street, listing, name, "Colombia", true, 2),
                price,
                "keyOption" + listing,
                null,
                image,
                price * 2
        );
    }
}