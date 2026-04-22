package com.proyecto.tfg.service.impl;

import com.proyecto.tfg.model.UsuarioDeseado;
import com.proyecto.tfg.repository.UsuarioDeseadoRepository;
import com.proyecto.tfg.service.IUsuarioDeseadoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UsuarioDeseadoServiceImpl implements IUsuarioDeseadoService {

    @Autowired
    private UsuarioDeseadoRepository usuarioDeseadoRepository;

    @Override
    public String toggleDeseado(int idCliente, int idEvento) {
        try {
            // Check if the desired event already exists for the user
            boolean existe = usuarioDeseadoRepository.existsByIdClienteAndIdEvento(idCliente, idEvento);

            if (existe) {
                // If it exists, remove it
                usuarioDeseadoRepository.deleteByIdClienteAndIdEvento(idCliente, idEvento);
                return "Event removed from your desired list";
            }

            // If it does not exist, add it
            UsuarioDeseado deseado = new UsuarioDeseado();
            deseado.setIdCliente(idCliente);
            deseado.setIdEvento(idEvento);

            usuarioDeseadoRepository.save(deseado);
            return "Event added to your desired list";

        } catch (DataIntegrityViolationException e) {
            // Handle potential duplicate insertion
            return "This event is already marked as desired";
        }
    }

    @Override
    public boolean esDeseado(int idCliente, int idEvento) {
        // Check if the event is in the user's desired list
        return usuarioDeseadoRepository.existsByIdClienteAndIdEvento(idCliente, idEvento);
    }

    @Override
    public void eliminarDeseado(int idCliente, int idEvento) {
        // Remove the event from the user's desired list
        usuarioDeseadoRepository.deleteByIdClienteAndIdEvento(idCliente, idEvento);
    }
}
