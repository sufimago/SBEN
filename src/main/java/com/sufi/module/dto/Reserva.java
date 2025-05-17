package com.sufi.module.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Reserva {
    private String alojamiento_id;
    private LocalDate fecha_entrada;
    private LocalDate fecha_salida;
    private Integer localizador;
    private double precio_total;
    private double precio_por_dia;
    @JsonProperty("información alojamiento")
    private Alojamiento informacionAlojamiento;
    @JsonProperty("información cliente")
    private Cliente informacionCliente;

    public Reserva(String alojamiento_id, LocalDate fecha_entrada, LocalDate fecha_salida, Integer localizador, double precio_total, double precio_por_dia, Alojamiento informacionAlojamiento, Cliente informacionCliente) {
        this.alojamiento_id = alojamiento_id;
        this.fecha_entrada = fecha_entrada;
        this.fecha_salida = fecha_salida;
        this.localizador = localizador;
        this.precio_total = precio_total;
        this.precio_por_dia = precio_por_dia;
        this.informacionAlojamiento = informacionAlojamiento;
        this.informacionCliente = informacionCliente;
    }
}
