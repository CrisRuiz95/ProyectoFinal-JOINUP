package com.proyecto.tfg.service;

import com.proyecto.tfg.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface IUsuarioService {

    // Create a new user account
    void createAccount(Usuario usuario);

    // Fetch user account by ID
    Optional<Usuario> fetchAccount(int idCliente);

    // Get all users
    List<Usuario> findAll();

    // Update an existing account
    boolean updateAccount(Usuario usuario);

    // Delete an account by ID
    boolean deleteAccount(int idCliente);

    //Login with email and password
    Optional<Usuario> login(String email, String password);
}
