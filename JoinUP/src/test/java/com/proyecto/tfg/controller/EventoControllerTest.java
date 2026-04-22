package com.proyecto.tfg.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.tfg.model.Evento;
import com.proyecto.tfg.model.Usuario;
import com.proyecto.tfg.service.IEventoService;
import com.proyecto.tfg.model.Response;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventoController.class)
class EventoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IEventoService eventoService;

    @Autowired
    private ObjectMapper objectMapper;


    // ===========================
    // CREATE EVENT
    // ===========================
    @Test
    void testCreateEvent() throws Exception {
        Evento evento = new Evento();
        evento.setTitulo("Evento Test");

        Mockito.when(eventoService.save(any())).thenReturn(evento);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/eventos/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evento)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("201"));
    }


    // ===========================
    // UPDATE EVENT
    // ===========================
    @Test
    void testUpdateEvent_Success() throws Exception {
        Evento evento = new Evento();
        evento.setTitulo("Nuevo titulo");

        Mockito.when(eventoService.updateEvento(eq(1), any())).thenReturn(evento);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/eventos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evento)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Event updated successfully."));
    }

    @Test
    void testUpdateEvent_NotFound() throws Exception {
        Evento evento = new Evento();

        Mockito.when(eventoService.updateEvento(eq(1), any()))
                .thenThrow(new RuntimeException("Event not found"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/eventos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evento)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("417"));
    }


    // ===========================
    // LIST ALL EVENTS
    // ===========================
    @Test
    void testListAll_EventsFound() throws Exception {
        Mockito.when(eventoService.listAll())
                .thenReturn(Arrays.asList(new Evento(), new Evento()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/eventos/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testListAll_NoContent() throws Exception {
        Mockito.when(eventoService.listAll())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/eventos/all"))
                .andExpect(status().isNoContent());
    }


    // ===========================
    // GET EVENT BY ID
    // ===========================
    @Test
    void testGetEvent_Found() throws Exception {
        Evento evento = new Evento();
        evento.setIdEvento(1);

        Mockito.when(eventoService.fetchEvento(1))
                .thenReturn(Optional.of(evento));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/eventos/detalle/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEvento").value(1));
    }

    @Test
    void testGetEvent_NotFound() throws Exception {
        Mockito.when(eventoService.fetchEvento(1))
                .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/eventos/detalle/1"))
                .andExpect(status().isNotFound());
    }


    // ===========================
    // SEARCH BY TITLE
    // ===========================
    @Test
    void testSearchByTitle_Found() throws Exception {
        Mockito.when(eventoService.findByTitulo("test"))
                .thenReturn(Arrays.asList(new Evento()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/eventos/buscar")
                        .param("titulo", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testSearchByTitle_NoContent() throws Exception {
        Mockito.when(eventoService.findByTitulo("test"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/eventos/buscar")
                        .param("titulo", "test"))
                .andExpect(status().isNoContent());
    }


    // ===========================
    // SEARCH BY TAGS
    // ===========================
    @Test
    void testSearchByTags() throws Exception {
        Mockito.when(eventoService.findByTags("music"))
                .thenReturn(Arrays.asList(new Evento(), new Evento()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/eventos/tags")
                        .param("tag", "music"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }


    // ===========================
    // GET PARTICIPANTS
    // ===========================
    @Test
    void testGetParticipants_Found() throws Exception {
        Mockito.when(eventoService.getParticipantes(1))
                .thenReturn(Arrays.asList(new Usuario(), new Usuario()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/eventos/1/participantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetParticipants_NoContent() throws Exception {
        Mockito.when(eventoService.getParticipantes(1))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/eventos/1/participantes"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetParticipants_EventNotFound() throws Exception {
        Mockito.when(eventoService.getParticipantes(1))
                .thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/eventos/1/participantes"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetParticipants_ServerError() throws Exception {
        Mockito.when(eventoService.getParticipantes(1))
                .thenThrow(new IllegalStateException("Error"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/eventos/1/participantes"))
                .andExpect(status().isInternalServerError());
    }


    // ===========================
    // DELETE EVENT
    // ===========================
    @Test
    void testDeleteEvent_Success() throws Exception {
        Mockito.when(eventoService.delete(1)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/eventos/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Event deleted successfully."));
    }

    @Test
    void testDeleteEvent_NotFound() throws Exception {
        Mockito.when(eventoService.delete(1)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/eventos/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Event not found."));
    }

}
