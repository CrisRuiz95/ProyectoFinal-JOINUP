package com.proyecto.tfg.controller;

import com.proyecto.tfg.service.IUsuarioDeseadoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioDeseadoController.class)
public class UsuarioDeseadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUsuarioDeseadoService wishlistService;

    private int userId;
    private int eventId;

    @BeforeEach
    void setUp() {
        userId = 1;
        eventId = 100;
    }

    // ====================================
    // TEST: TOGGLE WISHLIST
    // ====================================
    @Test
    void testToggleWishlist_Add() throws Exception {
        when(wishlistService.toggleDeseado(userId, eventId))
                .thenReturn("Evento añadido a tu lista de deseados");

        mockMvc.perform(post("/deseados/toggle")
                        .param("idUsuario", String.valueOf(userId))
                        .param("idEvento", String.valueOf(eventId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Evento añadido a tu lista de deseados"));
    }

    @Test
    void testToggleWishlist_Remove() throws Exception {
        when(wishlistService.toggleDeseado(userId, eventId))
                .thenReturn("Evento eliminado de tu lista de deseados");

        mockMvc.perform(post("/deseados/toggle")
                        .param("idUsuario", String.valueOf(userId))
                        .param("idEvento", String.valueOf(eventId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Evento eliminado de tu lista de deseados"));
    }

    // ====================================
    // TEST: CHECK IF WISHLISTED
    // ====================================
    @Test
    void testIsWishlisted_True() throws Exception {
        when(wishlistService.esDeseado(userId, eventId)).thenReturn(true);

        mockMvc.perform(get("/deseados/check")
                        .param("idUsuario", String.valueOf(userId))
                        .param("idEvento", String.valueOf(eventId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testIsWishlisted_False() throws Exception {
        when(wishlistService.esDeseado(userId, eventId)).thenReturn(false);

        mockMvc.perform(get("/deseados/check")
                        .param("idUsuario", String.valueOf(userId))
                        .param("idEvento", String.valueOf(eventId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    // ====================================
    // TEST: DELETE FROM WISHLIST
    // ====================================
    @Test
    void testDeleteFromWishlist() throws Exception {
        doNothing().when(wishlistService).eliminarDeseado(userId, eventId);

        mockMvc.perform(delete("/deseados/delete")
                        .param("userId", String.valueOf(userId))
                        .param("eventId", String.valueOf(eventId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted successfully"));
    }
}
