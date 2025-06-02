package com.sufi.module.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataBaseDto {
    private int hotCodigo;
    private String ciudad;
    private Integer imagenId;
    private boolean disponible;
    private int occupants;
    private String direccion;
    private int listing;
    private String nombre;
    private String pais;

    // Constructor
    public DataBaseDto(int hotCodigo, String ciudad, Integer imagenId, boolean disponible,
                       int occupants, String direccion, int listing, String nombre, String pais) {
        this.hotCodigo = hotCodigo;
        this.ciudad = ciudad;
        this.imagenId = imagenId;
        this.disponible = disponible;
        this.occupants = occupants;
        this.direccion = direccion;
        this.listing = listing;
        this.nombre = nombre;
        this.pais = pais;
    }

    public DataBaseDto() {
        // Constructor vacío
    }
}
