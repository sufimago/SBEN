package com.sufi.commons.service;

import com.sufi.commons.mongo.ProviderOptionsRepository;
import com.sufi.module.dto.ProviderOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ProviderOptionsService {
    @Autowired
    private ProviderOptionsRepository providerOptionRepository;

    public Flux<ProviderOptions> obtenerPorIdO(String idO) {
        // Llama al repositorio con el campo idO
        return providerOptionRepository.findByIdO(idO);
    }

}
