package com.sufi.module.service.confirm;

import com.sufi.commons.IProcessorClient;
import com.sufi.commons.service.ReservaService;
import com.sufi.module.dto.Cliente;
import com.sufi.module.dto.Reserva;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ConfirmControllerTest {

    @Mock
    private IProcessorClient processorClient;

    @Mock
    private ReservaService reservaService;

    @InjectMocks
    private ConfirmController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testObtenerConfirmacionExito() {
        LocalDateTime fechaEntrada = LocalDateTime.parse("2023-10-01T12:00:00");
        LocalDateTime fechaSalida = LocalDateTime.parse("2023-10-05T12:00:00");

        LocalDate fechaEntradaLocal = fechaEntrada.toLocalDate();
        LocalDate fechaSalidaLocal = fechaSalida.toLocalDate();

        ConfirmRequest request = new ConfirmRequest(100,
                fechaEntrada, fechaSalida, 2, "Carlos",
                "carlos@carlos.com", 150.0);

        ConfirmResponse mockResponse = new ConfirmResponse("Reserva confirmada", new Reserva(
                "100",
                fechaEntradaLocal, fechaSalidaLocal, 123456, 150, 150, null,
                new Cliente("carlos", "carlos@carlos.com"))
        );

        when(processorClient.confirm(any(ConfirmRequest.class))).thenReturn(Mono.just(mockResponse));
        when(reservaService.insertReserva(any(ConfirmRequest.class), any(ConfirmResponse.class)))
                .thenReturn("Reserva guardada exitosamente");

        StepVerifier.create(controller.obtenerConfirmacion(request))
                .expectNextMatches(response ->
                        "Reserva confirmada".equals(response.getMensaje()) &&
                                response.getReserva() != null
                )
                .verifyComplete();

        verify(processorClient, times(1)).confirm(request);
        verify(reservaService, times(1)).insertReserva(request, mockResponse);
    }

    @Test
    void testObtenerConfirmacionError422() {
        LocalDateTime fechaEntrada = LocalDateTime.parse("2023-10-01T12:00:00");
        LocalDateTime fechaSalida = LocalDateTime.parse("2023-10-05T12:00:00");

        ConfirmRequest request = new ConfirmRequest(100,
                fechaEntrada, fechaSalida, 2, "Carlos",
                "carlos@carlos.com", 150.0);

        WebClientResponseException ex = WebClientResponseException.create(
                422, "Unprocessable Entity", null, "Datos inválidos".getBytes(), null);

        when(processorClient.confirm(any(ConfirmRequest.class))).thenReturn(Mono.error(ex));

        StepVerifier.create(controller.obtenerConfirmacion(request))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(ResponseStatusException.class, error);
                    ResponseStatusException rse = (ResponseStatusException) error;
                    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, rse.getStatusCode());
                    assertEquals("Datos inválidos enviados al proveedor", rse.getReason());
                })
                .verify();

        verify(processorClient, times(1)).confirm(request);
        verifyNoInteractions(reservaService);
    }

    @Test
    void testObtenerConfirmacionErrorGeneral() {
        LocalDateTime fechaEntrada = LocalDateTime.parse("2023-10-01T12:00:00");
        LocalDateTime fechaSalida = LocalDateTime.parse("2023-10-05T12:00:00");

        ConfirmRequest request = new ConfirmRequest(100,
                fechaEntrada, fechaSalida, 2, "Carlos",
                "carlos@carlos.com", 150.0);


        WebClientResponseException ex = WebClientResponseException.create(
                500, "Internal Server Error", null, "Error servidor".getBytes(), null);

        when(processorClient.confirm(any(ConfirmRequest.class))).thenReturn(Mono.error(ex));

        StepVerifier.create(controller.obtenerConfirmacion(request))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(ResponseStatusException.class, error);
                    ResponseStatusException rse = (ResponseStatusException) error;
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, rse.getStatusCode());
                    assertEquals("Error al comunicarse con el proveedor", rse.getReason());
                })
                .verify();

        verify(processorClient, times(1)).confirm(request);
        verifyNoInteractions(reservaService);
    }
}
