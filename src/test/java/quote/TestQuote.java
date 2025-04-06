package quote;

import com.sufi.commons.IProcessorClient;
import com.sufi.module.dto.Alojamiento;
import com.sufi.module.dto.CancelPolicies;
import com.sufi.module.service.quote.QuoteController;
import com.sufi.module.service.quote.QuoteRequest;
import com.sufi.module.service.quote.QuoteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TestQuote {

    @Mock
    private IProcessorClient processorClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testQuote() {
        when(processorClient.quote(any(QuoteRequest.class)))
                .thenReturn(Mono.just(dataQuoteResponse()));

        QuoteController quoteController = new QuoteController();
        quoteController.obtenerQuote("9001%7C2025-02-01%7C2025-02-10%7C1");

        assertNotNull(quoteController);
    }

    public QuoteRequest dataQuoteRequest() {
        return new QuoteRequest("9001%7C2025-02-01%7C2025-02-10%7C1");
    }

    public QuoteResponse dataQuoteResponse() {
        CancelPolicies cancelPolicies = new CancelPolicies();
        cancelPolicies.setDias_antes(1);
        cancelPolicies.setPenalizacion(0.5);

        return new QuoteResponse(
                createAlojamiento(),
                150.0,
                "EUR",
                List.of(cancelPolicies)
        );
    }

    public Alojamiento createAlojamiento() {
        return new Alojamiento(
                "Bella casa",
                "Casa de campo",
                "Calle 123",
                9000,
                "Casa",
                "Bogotá",
                true,
                2
        );
    }

}
