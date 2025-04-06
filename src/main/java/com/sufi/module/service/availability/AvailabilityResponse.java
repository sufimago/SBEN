package com.sufi.module.service.availability;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sufi.module.dto.Alojamiento;
import com.sufi.module.dto.CancelPolicies;
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

    private List<CancelPolicies> politicas_cancelacion;

    public AvailabilityResponse(Alojamiento alojamiento, Double precioPorDia, String keyOption, List<CancelPolicies> cancelPolicies) {
        this.alojamiento = alojamiento;
        this.precioPorDia = precioPorDia;
        this.keyOption = keyOption;
        this.politicas_cancelacion = cancelPolicies;
    }
}