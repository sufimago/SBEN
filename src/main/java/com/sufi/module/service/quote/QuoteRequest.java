package com.sufi.module.service.quote;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class QuoteRequest {
    private int hotCodigo;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private double precioDia;

    public QuoteRequest(int hotCodigo, LocalDate fechaEntrada, LocalDate fechaSalida, double precioDia) {
        this.hotCodigo = hotCodigo;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.precioDia = precioDia;
    }

    public static QuoteRequest fromKeyoption(String keyoption) {
        String[] parts = keyoption.split("\\|");
        return new QuoteRequest(
                Integer.parseInt(parts[0]),
                LocalDate.parse(parts[1]),
                LocalDate.parse(parts[2]),
                Double.parseDouble(parts[3])
        );
    }
}
