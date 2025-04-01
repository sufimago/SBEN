package com.sufi.module.service.availability;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sufi.module.service.Alojamiento;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailabilityResponse {
    private Alojamiento alojamiento;

    @JsonProperty("precio_por_dia")
    private Double precioPorDia;

    public AvailabilityResponse(Alojamiento alojamiento, Double precioPorDia) {
        this.alojamiento = alojamiento;
        this.precioPorDia = precioPorDia;
    }
}