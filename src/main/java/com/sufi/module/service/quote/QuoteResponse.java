package com.sufi.module.service.quote;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sufi.module.dto.Alojamiento;
import com.sufi.module.dto.CancelPolicies;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuoteResponse {
    private Alojamiento alojamiento;
    private String imagen;
    @JsonProperty("precio_por_dia")
    private Double precioPorDia;
    private List<CancelPolicies> politicas_cancelacion;

    // Constructor
    public QuoteResponse(Alojamiento alojamiento, Double precioPorDia, String moneda, List<CancelPolicies> politicas_cancelacion, String imagen) {
        this.alojamiento = alojamiento;
        this.precioPorDia = precioPorDia;
        this.politicas_cancelacion = politicas_cancelacion;
        moneda = "EUR";
        this.imagen = imagen;
    }
}
