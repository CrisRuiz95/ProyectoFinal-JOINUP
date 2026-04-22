package com.proyecto.tfg.service;

import com.stripe.exception.StripeException;

public interface IStripeService {

    // Create a test payment with the specified amount and currency
    String crearPagoTest(Double monto, String moneda) throws StripeException;
}
