package com.sufi.module.service.confirm;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ConfirmRequest {
    private Integer listing_id;
    private Integer cliente_id;
    private LocalDateTime fecha_entrada;
    private LocalDateTime fecha_salida;
    private Integer num_personas;

    public ConfirmRequest(Integer listing_id, Integer client_id, LocalDateTime fechaEntrada, LocalDateTime fechaSalida, Integer num_personas) {
        this.listing_id = listing_id;
        this.cliente_id = client_id;
        this.fecha_entrada = fechaEntrada;
        this.fecha_salida = fechaSalida;
        this.num_personas = num_personas;
    }
}
