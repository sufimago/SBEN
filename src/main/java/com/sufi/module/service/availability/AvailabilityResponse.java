package com.sufi.module.service.availability;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sufi.module.dto.Alojamiento;
import com.sufi.module.dto.CancelPolicies;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AvailabilityResponse {
    private Alojamiento alojamiento;
    private String imagen;
    private int listingId;

    @JsonProperty("precio_por_dia")
    private Double precioPorDia;

    private String keyOption;

    private List<CancelPolicies> politicas_cancelacion;

    public AvailabilityResponse(Alojamiento alojamiento, Double precioPorDia, String keyOption, List<CancelPolicies> cancelPolicies, String imagen) {
        this.alojamiento = alojamiento;
        this.precioPorDia = precioPorDia;
        this.keyOption = keyOption;
        this.politicas_cancelacion = cancelPolicies;
        this.imagen = imagen;
    }

    public AvailabilityResponse(int listingId, Double precioPorDia, String keyOption) {
        this.listingId = listingId;
        this.precioPorDia = precioPorDia;
        this.keyOption = keyOption;
    }
}