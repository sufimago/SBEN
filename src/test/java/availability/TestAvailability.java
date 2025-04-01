package availability;

import com.sufi.commons.IProcessorClient;
import com.sufi.module.service.Alojamiento;
import com.sufi.module.service.availability.AvailabilityRequest;
import com.sufi.module.service.availability.AvailabilityResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestAvailability {

    @Mock
    private IProcessorClient processorClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAvailability() {
        AvailabilityRequest request = new AvailabilityRequest(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                List.of(9000)
        );

        AvailabilityResponse fakeResponse = new AvailabilityResponse(
                createAlojamiento(),
                150.0,
                "keyOption"
        );

        when(processorClient.getDisponibilidad(request))
                .thenReturn(Mono.just(List.of(fakeResponse)));

        List<AvailabilityResponse> response = processorClient.getDisponibilidad(request).block();

        // assert
        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(fakeResponse, response.getFirst());
        assert(fakeResponse.getPrecioPorDia().equals(150.0));
    }

    public Alojamiento createAlojamiento() {
        return new Alojamiento(
                "Bella casa",
                "Casa de campo",
                "Calle 123",
                9000,
                "Casa",
                "Bogotá",
                true
        );
    }
}