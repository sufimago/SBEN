package com.sufi.module.service.availability;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AvailabilityRequest {
    private LocalDateTime fechaEntrada;
    private LocalDateTime fechaSalida;
    private List<Integer> listingId;
    private Integer occupancy;

    // Constructor, getters y setters
    public AvailabilityRequest(LocalDateTime fechaEntrada, LocalDateTime fechaSalida, List<Integer> listingId, Integer occupancy) {
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.listingId = listingId;
        this.occupancy = occupancy;
    }

    public AvailabilityRequest() {
        // Constructor vacío
    }
}