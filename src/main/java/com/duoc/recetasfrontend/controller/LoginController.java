
package com.duoc.recetasfrontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;

@Controller
public class LoginController {

    @Value("${backend.url}") // URL del backend (definida en application.properties o YAML)
    private String backendUrl;

    private String jwtToken; // Variable para almacenar el token JWT

    @GetMapping("/login")
    public String login() {
        return "login"; // Devuelve el formulario de login
    }

    @PostMapping("/login")
    public String authenticateUser(
            @RequestParam String username,
            @RequestParam String password,
            Model model) {

        RestTemplate restTemplate = new RestTemplate();
        String loginUrl = backendUrl + "/login"; // Endpoint del backend para login

        // Crear el cuerpo de la solicitud (JSON)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody = String.format("{\"user\":\"%s\", \"encryptedPass\":\"%s\"}", username, password);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            // Realizar la solicitud al backend
            ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                jwtToken = response.getBody(); // Guardar el token JWT

                // Redirigir al home o a otra página
                return "redirect:/home";
            } else {
                model.addAttribute("error", "Credenciales inválidas");
                return "login"; // Volver a la página de login
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error en la autenticación: " + e.getMessage());
            return "login"; // Volver a la página de login
        }
    }

    public String getJwtToken() {
        return jwtToken;
    }




    @GetMapping("/test-session")
@ResponseBody
public String testSession(HttpSession session) {
    String token = (String) session.getAttribute("jwtToken");
    return token != null ? "Token en sesión: " + token : "No hay token en la sesión";
}

}

