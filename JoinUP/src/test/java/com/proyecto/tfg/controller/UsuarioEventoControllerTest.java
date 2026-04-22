package com.proyecto.tfg.controller;

import com.proyecto.tfg.service.IUsuarioEventoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioEventoController.class)
public class UsuarioEventoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUsuarioEventoService eventRegistrationService;

    private int userId;
    private int eventId;

    @BeforeEach
    void setUp() {
        userId = 1;
        eventId = 100;
    }

    // ====================================
    // TEST: JOIN EVENT SUCCESS
    // ====================================
    @Test
    void testJoinEvent_Success() throws Exception {
        // Simulamos que no lanza excepción
        when(eventRegistrationService.unirseAEvento(userId, eventId)).thenReturn(null);

        mockMvc.perform(post("/api/inscripciones/unirse")
                        .param("idUsuario", String.valueOf(userId))
                        .param("idEvento", String.valueOf(eventId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully joined the event"));
    }

    // ====================================
    // TEST: JOIN EVENT FAIL (BUSINESS RULE)
    // ====================================
    @Test
    void testJoinEvent_BusinessRuleFail() throws Exception {
        when(eventRegistrationService.unirseAEvento(userId, eventId))
                .thenThrow(new RuntimeException("User already registered"));

        mockMvc.perform(post("/api/inscripciones/unirse")
                        .param("idUsuario", String.valueOf(userId))
                        .param("idEvento", String.valueOf(eventId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User already registered"));
    }

    // ====================================
    // TEST: LEAVE EVENT SUCCESS
    // ====================================
    @Test
    void testLeaveEvent_Success() throws Exception {
        when(eventRegistrationService.desunirseDeEvento(userId, eventId)).thenReturn(true);

        mockMvc.perform(delete("/api/inscripciones/desunirse")
                        .param("idUsuario", String.valueOf(userId))
                        .param("idEvento", String.valueOf(eventId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully left the event"));
    }

    // ====================================
    // TEST: LEAVE EVENT NOT FOUND
    // ====================================
    @Test
    void testLeaveEvent_NotFound() throws Exception {
        when(eventRegistrationService.desunirseDeEvento(userId, eventId)).thenReturn(false);

        mockMvc.perform(delete("/api/inscripciones/desunirse")
                        .param("idUsuario", String.valueOf(userId))
                        .param("idEvento", String.valueOf(eventId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Registration not found"));
    }
}
