package com.proyecto.tfg.service;

import com.proyecto.tfg.model.Pago;

public interface IPagoService {

    // Process a payment for a user with the given amount and currency
    public Pago procesarPago(Integer idUsuario, Double monto, String moneda) throws Exception;
}
