package com.sufi.module.dto.avail;

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
