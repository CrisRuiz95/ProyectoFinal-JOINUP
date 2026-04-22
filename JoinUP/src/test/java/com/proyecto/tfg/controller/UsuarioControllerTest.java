package com.proyecto.tfg.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.tfg.model.Usuario;
import com.proyecto.tfg.model.Response;
import com.proyecto.tfg.service.IUsuarioService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;



    // ===========================
    // LIST ALL USERS
    // ===========================
    @Test
    void testListAllUsers() throws Exception {
        Mockito.when(usuarioService.findAll())
                .thenReturn(Arrays.asList(new Usuario(), new Usuario()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }


    // ===========================
    // GET USER BY ID
    // ===========================
    @Test
    void testGetUserById_Found() throws Exception {
        Usuario user = new Usuario();
        user.setIdCliente(1);

        Mockito.when(usuarioService.fetchAccount(1)).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCliente").value(1));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        Mockito.when(usuarioService.fetchAccount(1)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/usuarios/1"))
                .andExpect(status().isNotFound());
    }


    // ===========================
    // CREATE USER
    // ===========================
    @Test
    void testCreateUser() throws Exception {
        Usuario newUser = new Usuario();
        newUser.setEmail("test@example.com");

        Mockito.doNothing().when(usuarioService).createAccount(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/usuarios/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("201"));
    }


    // ===========================
    // LOGIN USER
    // ===========================
    @Test
    void testLogin_Success() throws Exception {
        Usuario loginUser = new Usuario();
        loginUser.setEmail("test@example.com");
        loginUser.setPassword("1234");

        Usuario dbUser = new Usuario();
        dbUser.setEmail("test@example.com");

        Mockito.when(usuarioService.login(anyString(), anyString()))
                .thenReturn(Optional.of(dbUser));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testLogin_Fail() throws Exception {
        Usuario loginUser = new Usuario();
        loginUser.setEmail("wrong@example.com");
        loginUser.setPassword("wrongpass");

        Mockito.when(usuarioService.login(anyString(), anyString()))
                .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }


    // ===========================
    // UPDATE USER
    // ===========================
    @Test
    void testUpdateUser_Found() throws Exception {
        Usuario existing = new Usuario();
        existing.setIdCliente(1);

        Usuario updated = new Usuario();
        updated.setNombre("Nuevo");

        Mockito.when(usuarioService.fetchAccount(1)).thenReturn(Optional.of(existing));
        Mockito.when(usuarioService.updateAccount(any())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        Mockito.when(usuarioService.fetchAccount(1)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Usuario())))
                .andExpect(status().isNotFound());
    }


    // ===========================
    // DELETE USER
    // ===========================
    @Test
    void testDeleteUser_Success() throws Exception {
        Mockito.when(usuarioService.deleteAccount(1)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully."));
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        Mockito.when(usuarioService.deleteAccount(1)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/usuarios/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found."));
    }
}
