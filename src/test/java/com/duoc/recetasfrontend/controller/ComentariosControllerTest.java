package com.duoc.recetasfrontend.controller;

import com.duoc.recetasfrontend.model.CommentValue;
import com.duoc.recetasfrontend.model.TokenStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException; // Importar correctamente
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ComentariosControllerTest {

    @Mock
    private TokenStore tokenStore;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ComentariosController comentariosController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        comentariosController = new ComentariosController(tokenStore);

        // Usar ReflectionTestUtils para configurar el valor de backendUrl
        ReflectionTestUtils.setField(comentariosController, "backendUrl", "http://localhost:8080");
    }

    @Test
    void testGuardarComentarioValoracionBackendError() {
        when(tokenStore.getToken()).thenReturn("validToken");

        // Simular error del backend
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new RestClientException("Backend error")); // RestClientException está ahora correctamente importado

        CommentValue commentValue = new CommentValue();
        commentValue.setComentario("Buen comentario");
        commentValue.setValoracion(5L);

        String result = comentariosController.guardarComentarioValoracion(
                1L,
                commentValue,
                mock(Model.class),
                redirectAttributes
        );

        assertEquals("redirect:/recetas/1/detalle", result);
        verify(redirectAttributes).addFlashAttribute("error", "Error al guardar el comentario. Intenta nuevamente más tarde.");
    }
}