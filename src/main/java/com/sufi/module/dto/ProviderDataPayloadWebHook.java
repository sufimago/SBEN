package com.sufi.module.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderDataPayloadWebHook {
    private String fecha_entrada;
    private String fecha_salida;
    private Integer listing_id;
    private Double precio_base;
}
