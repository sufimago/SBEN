package com.sufi.module.service.confirm;

import com.sufi.module.dto.Reserva;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmResponse {
    private String mensaje;
    private Reserva reserva;

    public ConfirmResponse(String message, Reserva reserva) {
        this.mensaje = message;
        this.reserva = reserva;
    }
}
