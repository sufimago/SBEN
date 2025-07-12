package com.sufi.module.service.quote;

import com.sufi.commons.IProcessorClient;
import com.sufi.module.dto.Alojamiento;
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
public class QuoteControllerTest {

    @Mock
    private IProcessorClient processorClient;

    @InjectMocks
    private QuoteController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testObtenerQuote() {
        String keyOption = "loQueSea";

        QuoteResponse mockResponse = new QuoteResponse(
                new Alojamiento("Bogotá", null, "Calle falsa 123", 1, "Hotel Test", "Colombia", true, 2),
                100.0,
                "EUR",
                null,
                "imagen.jpg"
        );

        when(processorClient.quote(any(QuoteRequest.class))).thenReturn(Mono.just(mockResponse));

        StepVerifier.create(controller.obtenerQuote(keyOption))
                .expectNextMatches(response ->
                        response.getPrecioPorDia() == 100.0 &&
                                response.getAlojamiento().getCiudad().equals("Bogotá") &&
                                "imagen.jpg".equals(response.getImagen())
                )
                .verifyComplete();

        // Verificamos que se llamó al processorClient con el request correcto
        verify(processorClient, times(1)).quote(argThat(req -> req.getKeyOption().equals(keyOption)));
    }

}
