package com.proyecto.tfg.service;

import com.proyecto.tfg.exception.UserAlreadyExistsException;
import com.proyecto.tfg.model.Rol;
import com.proyecto.tfg.model.Usuario;
import com.proyecto.tfg.repository.UsuariosRepository;
import com.proyecto.tfg.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuariosRepository repo;

    @InjectMocks
    private UsuarioServiceImpl service;

    private Usuario usuario;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdCliente(1);
        usuario.setNombre("Juan");
        usuario.setEmail("juan@test.com");
        usuario.setPassword("1234");
        usuario.setRol(Rol.GRATUITO);
    }

    // =========================
    // createAccount
    // =========================

    @Test
    void createAccount_ok() {
        when(repo.findByEmail(usuario.getEmail())).thenReturn(Optional.empty());

        service.createAccount(usuario);

        verify(repo, times(1)).save(any(Usuario.class));
    }

    @Test
    void createAccount_emailAlreadyExists() {
        when(repo.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));

        assertThrows(UserAlreadyExistsException.class,
                () -> service.createAccount(usuario));

        verify(repo, never()).save(any());
    }

    // =========================
    // fetchAccount
    // =========================

    @Test
    void fetchAccount_found() {
        when(repo.findById(1)).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = service.fetchAccount(1);

        assertTrue(result.isPresent());
        assertEquals("Juan", result.get().getNombre());
    }

    @Test
    void fetchAccount_notFound() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        Optional<Usuario> result = service.fetchAccount(1);

        assertTrue(result.isEmpty());
    }

    // =========================
    // findAll
    // =========================

    @Test
    void findAll_ok() {
        when(repo.findAll()).thenReturn(List.of(usuario));

        List<Usuario> result = service.findAll();

        assertEquals(1, result.size());
    }

    // =========================
    // updateAccount
    // =========================

    @Test
    void updateAccount_ok() {
        Usuario updated = new Usuario();
        updated.setIdCliente(1);
        updated.setNombre("Pedro");
        updated.setPassword("nuevaPass");

        when(repo.findById(1)).thenReturn(Optional.of(usuario));

        boolean result = service.updateAccount(updated);

        assertTrue(result);
        verify(repo).save(any(Usuario.class));
    }

    @Test
    void updateAccount_userNotFound() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        boolean result = service.updateAccount(usuario);

        assertFalse(result);
        verify(repo, never()).save(any());
    }

    // =========================
    // deleteAccount
    // =========================

    @Test
    void deleteAccount_ok() {
        when(repo.findById(1)).thenReturn(Optional.of(usuario));

        boolean result = service.deleteAccount(1);

        assertTrue(result);
        verify(repo).delete(usuario);
    }

    @Test
    void deleteAccount_notFound() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        boolean result = service.deleteAccount(1);

        assertFalse(result);
        verify(repo, never()).delete(any());
    }

    // =========================
    // login
    // =========================

    @Test
    void login_ok() {
        String rawPassword = "1234";
        usuario.setPassword(encoder.encode(rawPassword));

        when(repo.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));

        Optional<Usuario> result =
                service.login(usuario.getEmail(), rawPassword);

        assertTrue(result.isPresent());
    }

    @Test
    void login_userNotFound() {
        when(repo.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.empty());

        Optional<Usuario> result =
                service.login(usuario.getEmail(), "1234");

        assertTrue(result.isEmpty());
    }

    @Test
    void login_wrongPassword() {
        usuario.setPassword(encoder.encode("correcta"));

        when(repo.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));

        Optional<Usuario> result =
                service.login(usuario.getEmail(), "incorrecta");

        assertTrue(result.isEmpty());
    }
}
