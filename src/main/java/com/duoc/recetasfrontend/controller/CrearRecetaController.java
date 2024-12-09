package com.duoc.recetasfrontend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import com.duoc.recetasfrontend.model.Receta;
import com.duoc.recetasfrontend.model.TokenStore;

@Controller
public class CrearRecetaController {

    private static final Logger logger = LoggerFactory.getLogger(CrearRecetaController.class);

    @Value("${backend.url}")
    private String backendUrl;

    private final TokenStore tokenStore;

    public CrearRecetaController(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @GetMapping("/crear-receta")
    public String mostrarFormulario(Model model) {
        logger.info("Mostrando el formulario para crear receta.");
        model.addAttribute("receta", new Receta());
        return "crearReceta"; // Nombre del archivo HTML
    }

    @PostMapping("/crear-receta")
    public String guardarReceta(@ModelAttribute Receta receta, Model model) {
        logger.info("Recibida la receta desde el formulario: {}", receta.toString());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Obtener el token desde TokenStore
        String token = tokenStore.getToken();
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", "Bearer " + token);
        } else {
            logger.error("Token JWT no encontrado en TokenStore. Redirigiendo al login.");
            model.addAttribute("error", "No estás autenticado. Por favor, inicia sesión.");
            return "redirect:/login";
        }

        HttpEntity<Receta> entity = new HttpEntity<>(receta, headers);

        try {
            String url = backendUrl + "/private/publicar";
            logger.info("Enviando datos al backend en la URL: {}", url);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Receta creada exitosamente. Código de respuesta: {}", response.getStatusCode());
                model.addAttribute("mensaje", "Receta creada exitosamente.");
                return "redirect:/home";
            } else {
                logger.error("Error al crear receta. Código de respuesta: {}", response.getStatusCode());
                model.addAttribute("error", "No se pudo crear la receta. Código: " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error al guardar la receta: ", e);
            model.addAttribute("error", "Error al guardar la receta: " + e.getMessage());
        }

        return "crearReceta";
    }
}
