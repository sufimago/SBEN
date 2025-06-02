package com.sufi.module.service.cancel;

import lombok.Data;

@Data
public class CancelRequest {
    private Integer localizador;
    private String email_cliente;

    public CancelRequest(Integer localizado, String email_cliente) {
        this.localizador = localizado;
        this.email_cliente = email_cliente;
    }
}
