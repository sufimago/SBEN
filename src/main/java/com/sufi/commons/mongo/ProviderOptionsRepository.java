package com.sufi.commons.mongo;

import com.sufi.module.dto.ProviderOptions;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProviderOptionsRepository extends ReactiveMongoRepository<ProviderOptions, String> {
    // Aquí puedes agregar métodos personalizados si es necesario
    // Por ejemplo, para buscar por listingId:

    Flux<ProviderOptions> findByIdO(String idO);
}
