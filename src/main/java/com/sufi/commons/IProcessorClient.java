package com.sufi.commons;

import com.sufi.module.service.availability.AvailabilityRequest;
import com.sufi.module.service.availability.AvailabilityResponse;
import com.sufi.module.service.cancel.CancelRequest;
import com.sufi.module.service.cancel.CancelResponse;
import com.sufi.module.service.confirm.ConfirmRequest;
import com.sufi.module.service.confirm.ConfirmResponse;
import com.sufi.module.service.quote.QuoteRequest;
import com.sufi.module.service.quote.QuoteResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IProcessorClient {
    Mono<String> getAlojamientos();

    Mono<List<AvailabilityResponse>> getDisponibilidad(AvailabilityRequest request);

    Mono<QuoteResponse> quote(QuoteRequest request);

    Mono<ConfirmResponse> confirm(ConfirmRequest request);

    Mono<List<AvailabilityResponse>> obtenerDisponibilidadPorCiudad(String ciudad, AvailabilityRequest request);

    Mono<List<AvailabilityResponse>> getDisponibilidadCache(AvailabilityRequest request);

    Mono<List<AvailabilityResponse>> obtenerDisponibilidadPorCiudadCache(String ciudad, AvailabilityRequest request);

    Mono<CancelResponse> cancel(CancelRequest request);
}
