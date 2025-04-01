package com.sufi.module.dto.Avail;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatabaseAvailMapping {
    private int hotCodigo;
    public DatabaseAvailMapping(int hotCodigo) {
        this.hotCodigo = hotCodigo;
    }
}
