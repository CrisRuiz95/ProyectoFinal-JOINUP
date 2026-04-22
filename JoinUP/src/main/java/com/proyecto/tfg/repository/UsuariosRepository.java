package com.proyecto.tfg.repository;

import com.proyecto.tfg.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuario, Integer> {

    // Find user by email
    Optional<Usuario> findByEmail(String email);

    // Find user by ID
    Optional<Usuario> findByIdCliente(Integer idCliente);
}
