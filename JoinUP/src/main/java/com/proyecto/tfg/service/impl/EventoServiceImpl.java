package com.proyecto.tfg.service.impl;

import com.proyecto.tfg.exception.UserAlreadyExistsException;
import com.proyecto.tfg.model.Evento;
import com.proyecto.tfg.model.Usuario;
import com.proyecto.tfg.model.UsuarioEvento;
import com.proyecto.tfg.repository.EventosRepository;
import com.proyecto.tfg.service.IEventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class EventoServiceImpl implements IEventoService {

    @Autowired
    private EventosRepository repo;

    // List all events
    @Override
    public List<Evento> listAll() {
        return repo.findAll();
    }

    // Fetch an event by ID
    @Override
    public Optional<Evento> fetchEvento(int idEvemto) {
        return repo.findById(idEvemto);
    }

    // Save a new event (checks for duplicate title)
    @Override
    public Evento save(Evento event) {
        List<Evento> existingEvents = repo.findByTituloContainingIgnoreCase(event.getTitulo());
        if (!existingEvents.isEmpty()) {
            throw new UserAlreadyExistsException(
                    "Cannot use duplicate title: " + event.getTitulo()
            );
        }
        Evento newEvent = createNewEvent(event);
        return repo.save(newEvent);
    }

    // Create a new event object
    private Evento createNewEvent(Evento evento) {
        Evento newEvento = new Evento();
        newEvento.setIdOrgan(evento.getIdOrgan());
        newEvento.setFecha(evento.getFecha());
        newEvento.setTitulo(evento.getTitulo());
        newEvento.setDescripcion(evento.getDescripcion());
        newEvento.setMaxParticipantes(evento.getMaxParticipantes());
        newEvento.setPro(evento.isPro());
        newEvento.setPrecio(evento.getPrecio());
        newEvento.setTag1(evento.getTag1());
        newEvento.setTag2(evento.getTag2());
        newEvento.setTag3(evento.getTag3());
        newEvento.setTipoVia(evento.getTipoVia());
        newEvento.setVia(evento.getVia());
        newEvento.setNumVia(evento.getNumVia());
        newEvento.setPiso(evento.getPiso());
        newEvento.setPuerta(evento.getPuerta());
        newEvento.setCodigoPostal(evento.getCodigoPostal());
        newEvento.setProvincia(evento.getProvincia());
        newEvento.setPoblacion(evento.getPoblacion());
        newEvento.setInfoExtra(evento.getInfoExtra());
        newEvento.setImagen(evento.getImagen());
        newEvento.setUsuarios(new ArrayList<>()); // Initialize participants list
        return newEvento;
    }

    // Delete an event by ID
    @Override
    public boolean delete(Integer id) {
        Optional<Evento> eventoOpt = repo.findById(id);
        if (eventoOpt.isPresent()) {
            repo.delete(eventoOpt.get());
            return true;
        }
        return false; // Event not found
    }

    // Find events by title (partial match, case-insensitive)
    @Override
    public List<Evento> findByTitulo(String titulo) {
        return repo.findByTituloContainingIgnoreCase(titulo);
    }

    // Update an existing event
    @Override
    public Evento updateEvento(int idEvento, Evento updatedEvento) {
        Optional<Evento> optionalEvento = repo.findById(idEvento);

        if (optionalEvento.isPresent()) {
            Evento existingEvento = optionalEvento.get();

            // Update all fields
            existingEvento.setTitulo(updatedEvento.getTitulo());
            existingEvento.setDescripcion(updatedEvento.getDescripcion());
            existingEvento.setFecha(updatedEvento.getFecha());
            existingEvento.setMaxParticipantes(updatedEvento.getMaxParticipantes());
            existingEvento.setPro(updatedEvento.isPro());
            existingEvento.setPrecio(updatedEvento.getPrecio());
            existingEvento.setImagen(updatedEvento.getImagen());
            existingEvento.setTag1(updatedEvento.getTag1());
            existingEvento.setTag2(updatedEvento.getTag2());
            existingEvento.setTag3(updatedEvento.getTag3());
            existingEvento.setTipoVia(updatedEvento.getTipoVia());
            existingEvento.setVia(updatedEvento.getVia());
            existingEvento.setNumVia(updatedEvento.getNumVia());
            existingEvento.setPiso(updatedEvento.getPiso());
            existingEvento.setPuerta(updatedEvento.getPuerta());
            existingEvento.setCodigoPostal(updatedEvento.getCodigoPostal());
            existingEvento.setProvincia(updatedEvento.getProvincia());
            existingEvento.setPoblacion(updatedEvento.getPoblacion());
            existingEvento.setInfoExtra(updatedEvento.getInfoExtra());

            return repo.save(existingEvento);
        } else {
            throw new RuntimeException("Event with ID " + idEvento + " not found.");
        }
    }

    // Get all participants of an event
    @Override
    public List<Usuario> getParticipantes(int idEvento) {
        Optional<Evento> eventoOpt = repo.findById(idEvento);
        if (eventoOpt.isEmpty()) {
            throw new RuntimeException("Event not found with ID: " + idEvento);
        }

        // Map UsuarioEvento to Usuario
        return eventoOpt.get().getUsuarios()
                .stream()
                .map(UsuarioEvento::getUsuario)
                .collect(Collectors.toList());
    }

    // Find events by tag (checks all three tag fields)
    @Override
    public List<Evento> findByTags(String tag) {
        return repo.findByTag1ContainingIgnoreCaseOrTag2ContainingIgnoreCaseOrTag3ContainingIgnoreCase(
                tag, tag, tag
        );
    }
}
