package com.proyecto.tfg.service;

public interface IUsuarioDeseadoService {

    // Toggle a desired event for a user (add/remove)
    String toggleDeseado(int idCliente, int idEvento);

    // Check if a user has marked an event as desired
    boolean esDeseado(int idCliente, int idEvento);

    // Remove a desired event for a user
    void eliminarDeseado(int idCliente, int idEvento);
}
