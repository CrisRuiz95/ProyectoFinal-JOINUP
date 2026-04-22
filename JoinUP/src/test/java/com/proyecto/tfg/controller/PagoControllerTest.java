package com.proyecto.tfg.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.tfg.model.Pago;
import com.proyecto.tfg.model.Usuario;
import com.proyecto.tfg.repository.UsuariosRepository;
import com.proyecto.tfg.service.impl.PagoServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagoController.class)
public class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagoServiceImpl pagoService;

    @MockBean
    private UsuariosRepository usuariosRepository;

    private ObjectMapper objectMapper;
    private Usuario usuario;
    private Pago pago;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        usuario = new Usuario();
        usuario.setIdCliente(1);
        usuario.setNombre("Jordi");

        pago = new Pago();
        pago.setIdPago(10L);
        pago.setUsuario(usuario);       // CORRECTO: asignar el objeto Usuario
        pago.setMonto(20.0);
        pago.setMoneda("EUR");
        pago.setEstado("exitoso");
    }

    // ====================================
    // TEST: PAYMENT SUCCESS
    // ====================================
    @Test
    void testCreatePayment_Success() throws Exception {

        Mockito.when(usuariosRepository.findById(1))
                .thenReturn(Optional.of(usuario));

        Mockito.when(pagoService.procesarPago(1, 20.0, "EUR"))
                .thenReturn(pago);

        mockMvc.perform(post("/api/pagos")
                        .param("idUsuario", "1")
                        .param("monto", "20.0")
                        .param("moneda", "EUR")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPago").value(10))
                .andExpect(jsonPath("$.usuario.idCliente").value(1))
                .andExpect(jsonPath("$.usuario.nombre").value("Jordi"))
                .andExpect(jsonPath("$.monto").value(20.0))
                .andExpect(jsonPath("$.moneda").value("EUR"))
                .andExpect(jsonPath("$.estado").value("exitoso"));
    }

    // ====================================
    // TEST: USER NOT FOUND (404)
    // ====================================
    @Test
    void testCreatePayment_UserNotFound() throws Exception {

        Mockito.when(usuariosRepository.findById(1))
                .thenReturn(Optional.empty());

        Mockito.when(pagoService.procesarPago(any(), any(), any()))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(post("/api/pagos")
                        .param("idUsuario", "1")
                        .param("monto", "20.0")
                        .param("moneda", "EUR"))
                .andExpect(status().isInternalServerError());
    }

    // ====================================
    // TEST: ERROR IN PAYMENT SERVICE (500)
    // ====================================
    @Test
    void testCreatePayment_ServiceError() throws Exception {

        Mockito.when(usuariosRepository.findById(1))
                .thenReturn(Optional.of(usuario));

        Mockito.when(pagoService.procesarPago(eq(1), eq(20.0), eq("EUR")))
                .thenThrow(new RuntimeException("Payment processing failed"));

        mockMvc.perform(post("/api/pagos")
                        .param("idUsuario", "1")
                        .param("monto", "20.0")
                        .param("moneda", "EUR"))
                .andExpect(status().isInternalServerError());
    }
}
