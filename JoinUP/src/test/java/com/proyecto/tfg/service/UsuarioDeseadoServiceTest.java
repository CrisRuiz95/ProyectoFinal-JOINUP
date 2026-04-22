package com.proyecto.tfg.service;


import com.proyecto.tfg.model.UsuarioDeseado;
import com.proyecto.tfg.repository.UsuarioDeseadoRepository;
import com.proyecto.tfg.service.impl.UsuarioDeseadoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioDeseadoServiceTest {

    @Mock
    private UsuarioDeseadoRepository usuarioDeseadoRepository;

    @InjectMocks
    private UsuarioDeseadoServiceImpl service;

    // =========================
    // toggleDeseado
    // =========================

    @Test
    void toggleDeseado_addEvent() {
        int idCliente = 1;
        int idEvento = 10;

        when(usuarioDeseadoRepository.existsByIdClienteAndIdEvento(idCliente, idEvento))
                .thenReturn(false);

        String result = service.toggleDeseado(idCliente, idEvento);

        assertEquals("Event added to your desired list", result);
        verify(usuarioDeseadoRepository).save(any(UsuarioDeseado.class));
        verify(usuarioDeseadoRepository, never())
                .deleteByIdClienteAndIdEvento(anyInt(), anyInt());
    }

    @Test
    void toggleDeseado_removeEvent() {
        int idCliente = 1;
        int idEvento = 10;

        when(usuarioDeseadoRepository.existsByIdClienteAndIdEvento(idCliente, idEvento))
                .thenReturn(true);

        String result = service.toggleDeseado(idCliente, idEvento);

        assertEquals("Event removed from your desired list", result);
        verify(usuarioDeseadoRepository)
                .deleteByIdClienteAndIdEvento(idCliente, idEvento);
        verify(usuarioDeseadoRepository, never()).save(any());
    }

    @Test
    void toggleDeseado_duplicateHandled() {
        int idCliente = 1;
        int idEvento = 10;

        when(usuarioDeseadoRepository.existsByIdClienteAndIdEvento(idCliente, idEvento))
                .thenReturn(false);

        doThrow(DataIntegrityViolationException.class)
                .when(usuarioDeseadoRepository)
                .save(any(UsuarioDeseado.class));

        String result = service.toggleDeseado(idCliente, idEvento);

        assertEquals("This event is already marked as desired", result);
    }

    // =========================
    // esDeseado
    // =========================

    @Test
    void esDeseado_true() {
        when(usuarioDeseadoRepository.existsByIdClienteAndIdEvento(1, 10))
                .thenReturn(true);

        boolean result = service.esDeseado(1, 10);

        assertTrue(result);
    }

    @Test
    void esDeseado_false() {
        when(usuarioDeseadoRepository.existsByIdClienteAndIdEvento(1, 10))
                .thenReturn(false);

        boolean result = service.esDeseado(1, 10);

        assertFalse(result);
    }

    // =========================
    // eliminarDeseado
    // =========================

    @Test
    void eliminarDeseado_ok() {
        service.eliminarDeseado(1, 10);

        verify(usuarioDeseadoRepository)
                .deleteByIdClienteAndIdEvento(1, 10);
    }
}
