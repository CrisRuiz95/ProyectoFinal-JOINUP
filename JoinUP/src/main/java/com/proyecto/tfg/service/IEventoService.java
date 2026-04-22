package com.proyecto.tfg.service;

import com.proyecto.tfg.model.Evento;
import com.proyecto.tfg.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface IEventoService {

    // List all events
    List<Evento> listAll();

    // Find an event by its ID
    Optional<Evento> fetchEvento(int idEvento);

    // Save a new event or update an existing one
    Evento save(Evento event);

    // Delete an event by ID
    boolean delete(Integer id);

    // Find events by title
    List<Evento> findByTitulo(String titulo);

    // Update event details
    Evento updateEvento(int idEvento, Evento updatedEvento);

    // Get all participants of a specific event
    List<Usuario> getParticipantes(int idEvento);

    // Find events by a specific tag
    List<Evento> findByTags(String tag);
}
