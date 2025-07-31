package com.sufi.commons.mongo;

import com.sufi.module.dto.ProviderOptions;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface IProviderOptionsRepository extends ReactiveMongoRepository<ProviderOptions, String> {
    Flux<ProviderOptions> findByIdO(String idO);
}
