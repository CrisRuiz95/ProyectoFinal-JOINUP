package com.proyecto.tfg.controller;

import com.proyecto.tfg.model.Pago;
import com.proyecto.tfg.model.Usuario;
import com.proyecto.tfg.service.impl.PagoServiceImpl;
import com.proyecto.tfg.repository.UsuariosRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoServiceImpl pagoService;

    @Autowired
    private UsuariosRepository usuariosRepository;


    // ========================
    // CREATE PAYMENT
    // ========================
    @Operation(
            summary = "Create Payment",
            description = "Processes a payment for a user and updates their role if the transaction is successful."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment completed successfully",
                    content = @Content(schema = @Schema(implementation = Pago.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public Pago createPayment(
            @RequestParam Integer idUsuario,
            @RequestParam Double monto,
            @RequestParam String moneda) throws Exception {

        // Process payment and update user role if successful
        Pago pago = pagoService.procesarPago(idUsuario, monto, moneda);

        // Ensure user exists
        Usuario usuario = usuariosRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Return payment info
        return pago;
    }
}
