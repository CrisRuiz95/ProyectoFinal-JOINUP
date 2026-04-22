package com.proyecto.tfg.repository;

import com.proyecto.tfg.model.UsuarioDeseado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioDeseadoRepository extends JpaRepository<UsuarioDeseado, Integer> {

    // Checks if a user has already marked an event as desired
    boolean existsByIdClienteAndIdEvento(int idCliente, int idEvento);

    // Deletes a desired event by user and event
    void deleteByIdClienteAndIdEvento(int idCliente, int idEvento);

    // Retrieves all desired events of a user
    List<UsuarioDeseado> findByIdCliente(int idCliente);

    // Finds a specific desired event
    Optional<UsuarioDeseado> findByIdClienteAndIdEvento(int idCliente, int idEvento);
}
