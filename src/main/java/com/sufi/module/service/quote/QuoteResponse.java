package com.sufi.module.service.quote;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuoteResponse {
    private int hotCodigo;
    private String nombreHotel;
    private double precioTotal;
    private String moneda;

    // Constructor
    public QuoteResponse(int hotCodigo, String nombreHotel, double precioTotal, String moneda) {
        this.hotCodigo = hotCodigo;
        this.nombreHotel = nombreHotel;
        this.precioTotal = precioTotal;
        this.moneda = moneda;
    }
}
