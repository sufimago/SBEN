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

    @JsonProperty("precio_por_dia")
    private Double precioPorDia;

    @JsonProperty("precio_total")
    private Double precioTotal;

    private String keyOption;

    private List<CancelPolicies> politicas_cancelacion;

    public AvailabilityResponse(Alojamiento alojamiento, Double precioPorDia, String keyOption, List<CancelPolicies> cancelPolicies, String imagen, Double precioTotal) {
        this.alojamiento = alojamiento;
        this.precioPorDia = precioPorDia;
        this.keyOption = keyOption;
        this.politicas_cancelacion = cancelPolicies;
        this.imagen = imagen;
        this.precioTotal = precioTotal;
    }
}