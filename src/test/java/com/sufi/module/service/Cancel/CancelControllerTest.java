package com.sufi.module.service.Cancel;

import com.sufi.commons.IProcessorClient;
import com.sufi.commons.service.ReservaService;
import com.sufi.module.service.cancel.CancelController;
import com.sufi.module.service.cancel.CancelRequest;
import com.sufi.module.service.cancel.CancelResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CancelControllerTest {

    @Mock
    private IProcessorClient processorClient;

    @InjectMocks
    private CancelController controller;
    @Mock
    private ReservaService reservaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testObtenerCancelacionExito() {
        CancelRequest request = new CancelRequest(12345, "cliente@test.com");

        CancelResponse mockResponse = new CancelResponse("Cancelación exitosa");

        when(processorClient.cancel(any(CancelRequest.class))).thenReturn(Mono.just(mockResponse));
        when(reservaService.deleteReserva(any(CancelRequest.class))).thenReturn("Reserva eliminada");

        StepVerifier.create(controller.obtenerCancelacion(request))
                .expectNextMatches(response -> "Cancelación exitosa".equals(response.getMensaje()))
                .verifyComplete();

        verify(processorClient, times(1)).cancel(request);
        verify(reservaService, times(1)).deleteReserva(request);
    }
}
