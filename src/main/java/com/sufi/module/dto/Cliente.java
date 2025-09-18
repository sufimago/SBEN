package com.sufi.module.dto;

import lombok.Data;

@Data
public class Cliente {
    private String nombre;
    private String email;

    public Cliente(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }
    //test ci cd
}
