package com.duoc.recetasfrontend.model;

import lombok.Data;

@Data
public class CommentValue {
    private Long id; // Identificador único del comentario
    private String comentario; // Texto del comentario
    private Long valoracion; // Calificación numérica (1-5)
    private Long recipeId; // ID de la receta asociada
}
