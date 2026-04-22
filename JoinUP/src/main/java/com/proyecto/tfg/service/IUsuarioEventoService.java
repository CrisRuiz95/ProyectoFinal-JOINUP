package com.proyecto.tfg.service;

import com.proyecto.tfg.model.UsuarioEvento;

public interface IUsuarioEventoService {

    // Register a user to an event
    UsuarioEvento unirseAEvento(int idUsuario, int idEvento);

    // Unregister a user from an event
    boolean desunirseDeEvento(int idUsuario, int idEvento);

    // Check if a user is registered for an event
    boolean estaInscrito(int idUsuario, int idEvento);
}
