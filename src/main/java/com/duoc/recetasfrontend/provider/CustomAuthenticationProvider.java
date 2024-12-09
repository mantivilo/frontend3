package com.duoc.recetasfrontend.provider;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.duoc.recetasfrontend.model.TokenStore;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final TokenStore tokenStore;
    private final RestTemplate restTemplate;

    // Constructor con inyección de dependencias
    public CustomAuthenticationProvider(TokenStore tokenStore, RestTemplate restTemplate) {
        this.tokenStore = tokenStore;
        this.restTemplate = restTemplate;
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        System.out.println("Custom Authentication Provider Sent: ");
        System.out.println("Authentication: " + authentication);
        
        final String name = authentication.getName();
        final String password = authentication.getCredentials().toString();

        System.out.println("Custom Authentication Provider: " + name);

        // Crear el cuerpo de la solicitud
        final MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("user", name);
        requestBody.add("encryptedPass", password);

        System.out.println("Request Body: " + requestBody);

        // Realizar la solicitud con RestTemplate
        final var responseEntity = restTemplate.postForEntity("http://localhost:8080/login", requestBody, String.class);

        System.out.println("Response Entity: " + responseEntity);

        // Guardar el token en el TokenStore
        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
            tokenStore.setToken(responseEntity.getBody());
            System.out.println("Token Store: " + tokenStore.getToken());
        } else {
            throw new BadCredentialsException("Invalid username or password");
        }

        // Configurar las autoridades
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(name, password, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}