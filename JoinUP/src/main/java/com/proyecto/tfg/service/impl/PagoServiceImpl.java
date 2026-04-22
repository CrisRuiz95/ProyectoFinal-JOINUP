package com.proyecto.tfg.service.impl;

import com.proyecto.tfg.model.Pago;
import com.proyecto.tfg.model.Rol;
import com.proyecto.tfg.model.Usuario;
import com.proyecto.tfg.repository.PagoRepository;
import com.proyecto.tfg.repository.UsuariosRepository;
import com.proyecto.tfg.service.IPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PagoServiceImpl implements IPagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private UsuariosRepository usuarioRepository;

    @Autowired
    private StripeServiceImpl stripeService; // Service to interact with Stripe in test mode

    @Override
    public Pago procesarPago(Integer idUsuario, Double monto, String moneda) throws Exception {
        // Step 1: Find user by ID
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario not found"));

        // Step 2: Create a PaymentIntent in Stripe (test mode)
        String idTransaccion = stripeService.crearPagoTest(monto, moneda);

        // Step 3: Save payment record in the database
        Pago pago = new Pago();
        pago.setUsuario(usuario);
        pago.setMonto(monto);
        pago.setMoneda(moneda);
        pago.setEstado("exitoso"); // test mode always marks as successful
        pago.setFecha(LocalDateTime.now());
        pago.setIdTransaccion(idTransaccion);
        pagoRepository.save(pago);

        // Step 4: Update user role to PREMIUM if payment was successful
        if ("exitoso".equalsIgnoreCase(pago.getEstado())) {
            usuario.setRol(Rol.PREMIUM);
            usuarioRepository.save(usuario);
        }

        return pago;
    }

}
