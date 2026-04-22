package com.proyecto.tfg.service;


import com.proyecto.tfg.model.Evento;
import com.proyecto.tfg.model.Usuario;
import com.proyecto.tfg.model.UsuarioEvento;
import com.proyecto.tfg.repository.EventosRepository;
import com.proyecto.tfg.repository.UsuarioEventoRepository;
import com.proyecto.tfg.repository.UsuariosRepository;
import com.proyecto.tfg.service.impl.UsuarioEventoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioEventoServiceTest {

    @Mock
    private UsuarioEventoRepository repo;

    @Mock
    private UsuariosRepository usuarioRepo;

    @Mock
    private EventosRepository eventoRepo;

    @InjectMocks
    private UsuarioEventoServiceImpl service;

    private Usuario usuario;
    private Evento evento;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdCliente(1);
        usuario.setNombre("Juan");

        evento = new Evento();
        evento.setIdEvento(10);
        evento.setMaxParticipantes(5);
        evento.setUsuarios(new ArrayList<>());
    }

    // =========================
    // unirseAEvento
    // =========================

    @Test
    void unirseAEvento_ok() {
        when(usuarioRepo.findById(1)).thenReturn(Optional.of(usuario));
        when(eventoRepo.findById(10)).thenReturn(Optional.of(evento));
        when(repo.exists(any(Example.class))).thenReturn(false);
        when(repo.save(any(UsuarioEvento.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        UsuarioEvento result = service.unirseAEvento(1, 10);

        assertNotNull(result);
        verify(repo).save(any(UsuarioEvento.class));
    }

    @Test
    void unirseAEvento_userNotFound() {
        when(usuarioRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.unirseAEvento(1, 10));
    }

    @Test
    void unirseAEvento_eventNotFound() {
        when(usuarioRepo.findById(1)).thenReturn(Optional.of(usuario));
        when(eventoRepo.findById(10)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.unirseAEvento(1, 10));
    }

    @Test
    void unirseAEvento_alreadyRegistered() {
        when(usuarioRepo.findById(1)).thenReturn(Optional.of(usuario));
        when(eventoRepo.findById(10)).thenReturn(Optional.of(evento));
        when(repo.exists(any(Example.class))).thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> service.unirseAEvento(1, 10));

        verify(repo, never()).save(any());
    }

    @Test
    void unirseAEvento_eventFull() {
        evento.setMaxParticipantes(1);
        evento.getUsuarios().add(new UsuarioEvento());

        when(usuarioRepo.findById(1)).thenReturn(Optional.of(usuario));
        when(eventoRepo.findById(10)).thenReturn(Optional.of(evento));
        when(repo.exists(any(Example.class))).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> service.unirseAEvento(1, 10));
    }

    // =========================
    // desunirseDeEvento
    // =========================

    @Test
    void desunirseDeEvento_ok() {
        UsuarioEvento inscripcion = new UsuarioEvento();
        inscripcion.setUsuario(usuario);
        inscripcion.setEvento(evento);

        when(usuarioRepo.findById(1)).thenReturn(Optional.of(usuario));
        when(eventoRepo.findById(10)).thenReturn(Optional.of(evento));
        when(repo.findByUsuarioAndEvento(usuario, evento))
                .thenReturn(Optional.of(inscripcion));

        boolean result = service.desunirseDeEvento(1, 10);

        assertTrue(result);
        verify(repo).delete(inscripcion);
    }

    @Test
    void desunirseDeEvento_notRegistered() {
        when(usuarioRepo.findById(1)).thenReturn(Optional.of(usuario));
        when(eventoRepo.findById(10)).thenReturn(Optional.of(evento));
        when(repo.findByUsuarioAndEvento(usuario, evento))
                .thenReturn(Optional.empty());

        boolean result = service.desunirseDeEvento(1, 10);

        assertFalse(result);
        verify(repo, never()).delete(any());
    }

    @Test
    void desunirseDeEvento_userNotFound() {
        when(usuarioRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.desunirseDeEvento(1, 10));
    }

    @Test
    void desunirseDeEvento_eventNotFound() {
        when(usuarioRepo.findById(1)).thenReturn(Optional.of(usuario));
        when(eventoRepo.findById(10)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.desunirseDeEvento(1, 10));
    }

    // =========================
    // estaInscrito
    // =========================

    @Test
    void estaInscrito_true() {
        when(usuarioRepo.findById(1)).thenReturn(Optional.of(usuario));
        when(eventoRepo.findById(10)).thenReturn(Optional.of(evento));
        when(repo.exists(any(Example.class))).thenReturn(true);

        boolean result = service.estaInscrito(1, 10);

        assertTrue(result);
    }

    @Test
    void estaInscrito_false() {
        when(usuarioRepo.findById(1)).thenReturn(Optional.of(usuario));
        when(eventoRepo.findById(10)).thenReturn(Optional.of(evento));
        when(repo.exists(any(Example.class))).thenReturn(false);

        boolean result = service.estaInscrito(1, 10);

        assertFalse(result);
    }

    @Test
    void estaInscrito_userOrEventNotFound() {
        when(usuarioRepo.findById(1)).thenReturn(Optional.empty());

        boolean result = service.estaInscrito(1, 10);

        assertFalse(result);
        verify(repo, never()).exists(any());
    }
}
