package com.proyecto.tfg.model;

import jakarta.persistence.*;

/**
 * Represents the relation of a user who has marked an event as desired
 */
@Entity
@Table(name = "usuarios_deseados")
public class UsuarioDeseado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_deseado")
    private Integer idUsuarioDeseado;  // Unique identifier of the record

    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;         // ID of the user who desires the event

    @Column(name = "id_evento", nullable = false)
    private Integer idEvento;          // ID of the desired event

    // ========================
    // Getters and Setters
    // ========================
    public Integer getIdUsuarioDeseado() {
        return idUsuarioDeseado;
    }

    public void setIdUsuarioDeseado(Integer idUsuarioDeseado) {
        this.idUsuarioDeseado = idUsuarioDeseado;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Integer idEvento) {
        this.idEvento = idEvento;
    }
}
