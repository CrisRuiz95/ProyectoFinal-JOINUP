package com.proyecto.tfg.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.proyecto.tfg.service.IStripeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeServiceImpl implements IStripeService {

    @Value("${stripe.api.key}")
    private String apiKey;

    @Override
    public String crearPagoTest(Double monto, String moneda) throws StripeException {

        // Set Stripe API key
        Stripe.apiKey = apiKey;

        // Configure PaymentIntent parameters
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (monto * 100))           // Stripe uses smallest currency unit (cents)
                .setCurrency(moneda.toLowerCase())         // set currency
                .setPaymentMethod("pm_card_visa")          // test card payment
                .setConfirm(true)                          // automatically confirm payment
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)       // enable automatic payment methods
                                .setAllowRedirects(
                                        PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER
                                )
                                .build()
                )
                .build();

        // Create PaymentIntent in Stripe and return its ID
        PaymentIntent intent = PaymentIntent.create(params);
        return intent.getId();
    }
}
