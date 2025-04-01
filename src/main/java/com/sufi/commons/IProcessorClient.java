package com.sufi.commons;

import com.sufi.module.service.availability.AvailabilityRequest;
import com.sufi.module.service.availability.AvailabilityResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IProcessorClient {
    Mono<String> getAlojamientos();

    Mono<List<AvailabilityResponse>> getDisponibilidad(AvailabilityRequest request);
}
