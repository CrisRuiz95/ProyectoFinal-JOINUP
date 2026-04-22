package com.proyecto.tfg.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
public class Pago {

    // ========================
    // Payment identifier
    // ========================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;

    // ========================
    // Relationship with User
    // ========================
    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Usuario usuario; // User who made the payment

    // ========================
    // Payment data
    // ========================
    private Double monto;         // Paid amount
    private String moneda;        // Currency
    private String estado;        // Payment status: succeeded, failed
    private LocalDateTime fecha;  // Payment date and time

    @Column(name = "id_transaccion")
    private String idTransaccion; // Transaction ID returned by Stripe or another provider

    // ========================
    // Getters and Setters
    // ========================
    public Long getIdPago() { return idPago; }
    public void setIdPago(Long idPago) { this.idPago = idPago; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getIdTransaccion() { return idTransaccion; }
    public void setIdTransaccion(String idTransaccion) { this.idTransaccion = idTransaccion; }
}
