package com.sufi.module.service.availability;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sufi.module.service.Alojamiento;
import com.sufi.module.service.DtoAvail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AvailabilityResponse {
    private Alojamiento alojamiento;

    @JsonProperty("precio_por_dia")
    private Double precioPorDia;

    private String keyOption;

    private List<DtoAvail> politicas_cancelacion;

    public AvailabilityResponse(Alojamiento alojamiento, Double precioPorDia, String keyOption, List<DtoAvail> cancelPolicies) {
        this.alojamiento = alojamiento;
        this.precioPorDia = precioPorDia;
        this.keyOption = keyOption;
        this.politicas_cancelacion = cancelPolicies;
    }
}