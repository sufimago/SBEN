package com.sufi.module.dto;

import jakarta.persistence.Id;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Document(collection = "Provider-Options")
public class ProviderOptions {
    @Id
    private ObjectId id;

    @Field("idO")
    private String idO;

    @Field("p")
    private double p;

    // Constructor vacío
    public ProviderOptions() {
    }
}