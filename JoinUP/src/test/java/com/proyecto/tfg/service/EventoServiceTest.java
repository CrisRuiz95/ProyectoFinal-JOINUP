package com.proyecto.tfg.service;

import com.proyecto.tfg.exception.UserAlreadyExistsException;
import com.proyecto.tfg.model.Evento;
import com.proyecto.tfg.model.Usuario;
import com.proyecto.tfg.model.UsuarioEvento;
import com.proyecto.tfg.repository.EventosRepository;
import com.proyecto.tfg.service.impl.EventoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventosRepository repo;

    @InjectMocks
    private EventoServiceImpl service;

    private Evento evento;

    @BeforeEach
    void setUp() {
        evento = new Evento();
        evento.setIdEvento(1);
        evento.setTitulo("Evento prueba");
        evento.setDescripcion("Descripción");
        evento.setFecha(new Date());
        evento.setMaxParticipantes(10);
        evento.setPro(false);
        evento.setPrecio(String.valueOf(0.0));
        evento.setProvincia("Madrid");
        evento.setPoblacion("Madrid");
        evento.setUsuarios(new ArrayList<>());
    }

    // =========================
    // listAll
    // =========================

    @Test
    void listAll_ok() {
        when(repo.findAll()).thenReturn(List.of(evento));

        List<Evento> result = service.listAll();

        assertEquals(1, result.size());
        verify(repo).findAll();
    }

    // =========================
    // fetchEvento
    // =========================

    @Test
    void fetchEvento_found() {
        when(repo.findById(1)).thenReturn(Optional.of(evento));

        Optional<Evento> result = service.fetchEvento(1);

        assertTrue(result.isPresent());
        assertEquals("Evento prueba", result.get().getTitulo());
    }

    @Test
    void fetchEvento_notFound() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        Optional<Evento> result = service.fetchEvento(1);

        assertTrue(result.isEmpty());
    }

    // =========================
    // save
    // =========================

    @Test
    void save_ok() {
        when(repo.findByTituloContainingIgnoreCase(evento.getTitulo()))
                .thenReturn(List.of());
        when(repo.save(any(Evento.class))).thenAnswer(inv -> inv.getArgument(0));

        Evento saved = service.save(evento);

        assertNotNull(saved);
        verify(repo).save(any(Evento.class));
    }

    @Test
    void save_duplicateTitle() {
        when(repo.findByTituloContainingIgnoreCase(evento.getTitulo()))
                .thenReturn(List.of(evento));

        assertThrows(UserAlreadyExistsException.class,
                () -> service.save(evento));

        verify(repo, never()).save(any());
    }

    // =========================
    // delete
    // =========================

    @Test
    void delete_ok() {
        when(repo.findById(1)).thenReturn(Optional.of(evento));

        boolean result = service.delete(1);

        assertTrue(result);
        verify(repo).delete(evento);
    }

    @Test
    void delete_notFound() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        boolean result = service.delete(1);

        assertFalse(result);
        verify(repo, never()).delete(any());
    }

    // =========================
    // findByTitulo
    // =========================

    @Test
    void findByTitulo_ok() {
        when(repo.findByTituloContainingIgnoreCase("Evento"))
                .thenReturn(List.of(evento));

        List<Evento> result = service.findByTitulo("Evento");

        assertEquals(1, result.size());
    }

    // =========================
    // updateEvento
    // =========================

    @Test
    void updateEvento_ok() {
        Evento updated = new Evento();
        updated.setTitulo("Evento actualizado");
        updated.setDescripcion("Nueva descripción");

        when(repo.findById(1)).thenReturn(Optional.of(evento));
        when(repo.save(any(Evento.class))).thenAnswer(inv -> inv.getArgument(0));

        Evento result = service.updateEvento(1, updated);

        assertEquals("Evento actualizado", result.getTitulo());
        verify(repo).save(evento);
    }

    @Test
    void updateEvento_notFound() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.updateEvento(1, evento));
    }

    // =========================
    // getParticipantes
    // =========================

    @Test
    void getParticipantes_ok() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Juan");

        UsuarioEvento ue = new UsuarioEvento();
        ue.setUsuario(usuario);

        evento.setUsuarios(List.of(ue));

        when(repo.findById(1)).thenReturn(Optional.of(evento));

        List<Usuario> participantes = service.getParticipantes(1);

        assertEquals(1, participantes.size());
        assertEquals("Juan", participantes.get(0).getNombre());
    }

    @Test
    void getParticipantes_eventNotFound() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.getParticipantes(1));
    }

    // =========================
    // findByTags
    // =========================

    @Test
    void findByTags_ok() {
        when(repo.findByTag1ContainingIgnoreCaseOrTag2ContainingIgnoreCaseOrTag3ContainingIgnoreCase(
                "musica", "musica", "musica"))
                .thenReturn(List.of(evento));

        List<Evento> result = service.findByTags("musica");

        assertEquals(1, result.size());
    }
}

