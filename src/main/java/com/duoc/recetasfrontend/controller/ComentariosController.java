package com.duoc.recetasfrontend.controller;

import com.duoc.recetasfrontend.model.CommentValue;
import com.duoc.recetasfrontend.model.TokenStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/recetas")
public class ComentariosController {

    private static final Logger logger = LoggerFactory.getLogger(ComentariosController.class);

    @Value("${backend.url}")
    private String backendUrl;

    private final TokenStore tokenStore;

    public ComentariosController(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @PostMapping("/{id}/guardarComentarioValoracion")
    public String guardarComentarioValoracion(
            @PathVariable Long id,
            @ModelAttribute CommentValue commentValue,
            Model model,
            RedirectAttributes redirectAttributes) {

        logger.info("Recibiendo comentario y valoración: {}", commentValue);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Obtener el token desde TokenStore
        String token = tokenStore.getToken();
        if (token == null || token.isEmpty()) {
            logger.error("Token JWT no encontrado en TokenStore. Redirigiendo al login.");
            redirectAttributes.addFlashAttribute("error", "No estás autenticado. Por favor, inicia sesión.");
            return "redirect:/login";
        }
        headers.set("Authorization", "Bearer " + token);

        // Validación adicional en el frontend
        if (commentValue.getComentario() == null || commentValue.getComentario().isEmpty()) {
            logger.warn("Comentario vacío recibido.");
            redirectAttributes.addFlashAttribute("error", "El comentario no puede estar vacío.");
            return "redirect:/recetas/" + id + "/detalle";
        }
        if (commentValue.getValoracion() == null || commentValue.getValoracion() < 1 || commentValue.getValoracion() > 5) {
            logger.warn("Valoración inválida recibida: {}", commentValue.getValoracion());
            redirectAttributes.addFlashAttribute("error", "La valoración debe ser un número entre 1 y 5.");
            return "redirect:/recetas/" + id + "/detalle";
        }

        // Asociar el ID de la receta al comentario
        commentValue.setRecipeId(id);
        HttpEntity<CommentValue> entity = new HttpEntity<>(commentValue, headers);

        try {
            String url = backendUrl + "/private/recetas/" + id + "/guardarComentarioValoracion";
            logger.info("Enviando datos al backend en la URL: {}", url);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Comentario y valoración guardados exitosamente. Código de respuesta: {}", response.getStatusCode());
                redirectAttributes.addFlashAttribute("mensaje", "¡Gracias por tu comentario y valoración!");
                return "redirect:/recetas/" + id + "/detalle"; // Redirige de vuelta a los detalles de la receta
            } else {
                logger.error("Error al guardar comentario y valoración. Código de respuesta: {}", response.getStatusCode());
                redirectAttributes.addFlashAttribute("error", "No se pudo guardar el comentario. Código: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            logger.error("Error al guardar el comentario y la valoración: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al guardar el comentario. Intenta nuevamente más tarde.");
        }

        return "redirect:/recetas/" + id + "/detalle"; // Redirige de vuelta a la misma vista en caso de error
    }
}
