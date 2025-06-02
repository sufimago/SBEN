package com.sufi.module.service.cancel;

import lombok.Data;

@Data
public class CancelResponse {
    private String mensaje;

    public CancelResponse() {
    }

    public CancelResponse(String mensaje) {
        this.mensaje = mensaje;
    }
}
