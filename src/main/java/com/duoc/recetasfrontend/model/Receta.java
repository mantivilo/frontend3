package com.duoc.recetasfrontend.model;

import lombok.Data;

@Data
public class Receta {
    private Long id; // Si es necesario para otras operaciones
    private String nombre;
    private String tipoCocina;
    private String paisOrigen;
    private String dificultad;
    private String ingredientes;
    private String instrucciones;
    private int tiempoCoccion;
    private String fotografiaUrl;
    private String urlVideo; // Si también debe estar presente
}


