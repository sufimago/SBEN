package com.sufi.module.service.confirm;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ConfirmRequest {
    private Integer listing_id;
    private LocalDateTime fecha_entrada;
    private LocalDateTime fecha_salida;
    private String nombre_cliente;
    private String email_cliente;
    private Integer num_personas;
    private Double precio_total_cotizado;

    public ConfirmRequest(Integer listing_id, LocalDateTime fechaEntrada, LocalDateTime fechaSalida, Integer num_personas, String nombre_cliente, String email_cliente, Double precio_total_cotizado) {
        this.listing_id = listing_id;
        this.fecha_entrada = fechaEntrada;
        this.fecha_salida = fechaSalida;
        this.num_personas = num_personas;
        this.nombre_cliente = nombre_cliente;
        this.email_cliente = email_cliente;
        this.precio_total_cotizado = precio_total_cotizado;
    }
}
