package com.proyecto.tfg.service.impl;

import com.proyecto.tfg.model.Evento;
import com.proyecto.tfg.model.Usuario;
import com.proyecto.tfg.model.UsuarioEvento;
import com.proyecto.tfg.repository.EventosRepository;
import com.proyecto.tfg.repository.UsuarioEventoRepository;
import com.proyecto.tfg.repository.UsuariosRepository;
import com.proyecto.tfg.service.IUsuarioEventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioEventoServiceImpl implements IUsuarioEventoService {

    @Autowired
    private UsuarioEventoRepository repo;

    @Autowired
    private UsuariosRepository usuarioRepo;

    @Autowired
    private EventosRepository eventoRepo;

    @Override
    @Transactional
    public UsuarioEvento unirseAEvento(int idUsuario, int idEvento) {
        //  Find user and event entities
        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + idUsuario));

        Evento evento = eventoRepo.findById(idEvento)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + idEvento));

        //  Check if user is already registered
        if (estaInscrito(idUsuario, idEvento)) {
            throw new RuntimeException("User is already registered for this event.");
        }

        //  Validate event capacity
        if (evento.getMaxParticipantes() > 0) {
            long inscritos = evento.getUsuarios() != null ? evento.getUsuarios().size() : 0;
            if (inscritos >= evento.getMaxParticipantes()) {
                throw new RuntimeException("Event has reached maximum capacity.");
            }
        }

        // 4. Save registration
        return repo.save(createInscripcion(usuario, evento));
    }

    // Helper method to create a UsuarioEvento instance
    private UsuarioEvento createInscripcion(Usuario usuario, Evento evento) {
        UsuarioEvento inscripcion = new UsuarioEvento();
        inscripcion.setUsuario(usuario);
        inscripcion.setEvento(evento);
        return inscripcion;
    }

    @Override
    @Transactional
    public boolean desunirseDeEvento(int idUsuario, int idEvento) {
        //  Retrieve user and event entities
        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + idUsuario));

        Evento evento = eventoRepo.findById(idEvento)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + idEvento));

        //  Find existing registration
        Optional<UsuarioEvento> inscripcion = repo.findByUsuarioAndEvento(usuario, evento);

        if (inscripcion.isPresent()) {
            //  Delete registration if exists
            repo.delete(inscripcion.get());
            return true;
        }

        return false;
    }

    @Override
    public boolean estaInscrito(int idUsuario, int idEvento) {
        Usuario usuario = usuarioRepo.findById(idUsuario).orElse(null);
        Evento evento = eventoRepo.findById(idEvento).orElse(null);

        if (usuario != null && evento != null) {
            // Create a probe for Query-By-Example (QBE)
            UsuarioEvento probe = new UsuarioEvento();
            probe.setUsuario(usuario);
            probe.setEvento(evento);

            // Check if a matching registration exists
            return repo.exists(Example.of(probe));
        }
        return false;
    }
}
