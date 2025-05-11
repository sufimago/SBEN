package com.sufi.module.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Alojamiento {
    private String ciudad;
    private String imagen_id;
    private String direccion;
    private int listing;
    @JsonProperty("nombre")
    private String nombre;
    private String pais;
    private boolean disponible;
    private Integer occupants;

    // Constructor, getters y setters
    public Alojamiento(String ciudad, String imagen_id, String direccion, int listing, String nombre, String pais, boolean disponible, Integer occupants) {
        this.ciudad = ciudad;
        this.imagen_id = imagen_id;
        this.direccion = direccion;
        this.listing = listing;
        this.nombre = nombre;
        this.pais = pais;
        this.disponible = disponible;
        this.occupants = occupants;
    }

    public Alojamiento() {
        // Constructor vacío
    }
}