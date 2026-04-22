package com.proyecto.tfg.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Indicates that a user is registered in an event
 */
@Entity
@Table(name = "Usuarios_Eventos")
public class UsuarioEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuarios_Eventos")
    private int idUsuariosEventos;  // Unique identifier of the relation

    // Relation with User
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Usuario usuario;

    // Relation with Event
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_evento", nullable = false)
    private Evento evento;

    // ========================
    // Getters and Setters
    // ========================
    public int getIdUsuariosEventos() {
        return idUsuariosEventos;
    }

    public void setIdUsuariosEventos(int idUsuariosEventos) {
        this.idUsuariosEventos = idUsuariosEventos;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }
}
