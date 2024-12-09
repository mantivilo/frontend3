package com.duoc.recetasfrontend.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import com.duoc.recetasfrontend.model.Receta;

import com.duoc.recetasfrontend.model.TokenStore;

import java.util.List;

@Controller
public class DetalleController {

    String url = "http://localhost:8080";

    private final TokenStore tokenStore;

    public DetalleController(TokenStore tokenStore) {
        super();
        this.tokenStore = tokenStore;
    }

    @GetMapping("/recetas/{id}/detalle")
    public String getRecetaDetalle(@PathVariable Long id, Model model) {

        final var restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "Bearer " + this.tokenStore.getToken()); // Adding "Bearer"
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        // Build the URL for the detail recipe ID.
        String detalleUrl = url + "/private/recetas/" + id + "/detalle";
        String comentariosUrl = url + "/private/receta/" + id + "/comentariosValoracion";

        try {
            // GET call to backend for recipe details
            ResponseEntity<Receta> response = restTemplate.exchange(detalleUrl, HttpMethod.GET, entity, Receta.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Pass the detail recipe to the view
                model.addAttribute("detalles", response.getBody());
            } else {
                model.addAttribute("error", "No se pudo obtener los detalles de la receta.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error al obtener los detalles de la receta: " + e.getMessage());
        }

        try {
            // GET call to backend for comments and ratings
            ResponseEntity<List> comentariosResponse = restTemplate.exchange(comentariosUrl, HttpMethod.GET, entity, List.class);

            if (comentariosResponse.getStatusCode().is2xxSuccessful() && comentariosResponse.getBody() != null) {
                // Pass the comments and ratings to the view
                model.addAttribute("comentarios", comentariosResponse.getBody());
            } else {
                model.addAttribute("errorComentarios", "No se pudieron obtener los comentarios de la receta.");
            }
        } catch (Exception e) {
            model.addAttribute("errorComentarios", "Error al obtener los comentarios: " + e.getMessage());
        }

        // **Agregamos el atributo esperado por Thymeleaf**
        model.addAttribute("commentValue", ""); // Valor inicial para el campo de comentarios.

        return "detailrecipe";
    }
}
