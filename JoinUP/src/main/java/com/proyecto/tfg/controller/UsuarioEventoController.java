package com.proyecto.tfg.controller;

import com.proyecto.tfg.service.IUsuarioEventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inscripciones")
@CrossOrigin(origins = "*")
public class UsuarioEventoController {

    @Autowired
    private IUsuarioEventoService eventRegistrationService;

    // -------------------------------------------------------------------
    // JOIN EVENT
    // -------------------------------------------------------------------
    @Operation(
            summary = "Join an event",
            description = "Registers a user in a specific event. Returns a success message instead of the entity."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid request or business rule violation",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/unirse")
    public ResponseEntity<?> joinEvent(
            @RequestParam int idUsuario,
            @RequestParam int idEvento) {
        try {
            eventRegistrationService.unirseAEvento(idUsuario, idEvento);

            return new ResponseEntity<>("Successfully joined the event", HttpStatus.OK);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // -------------------------------------------------------------------
    // LEAVE EVENT
    // -------------------------------------------------------------------
    @Operation(
            summary = "Leave an event",
            description = "Removes the user's registration from an event."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully unregistered"),
            @ApiResponse(responseCode = "404", description = "Registration not found",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @DeleteMapping("/desunirse")
    public ResponseEntity<?> leaveEvent(
            @RequestParam int idUsuario,
            @RequestParam int idEvento) {

        boolean removed = eventRegistrationService.desunirseDeEvento(idUsuario, idEvento);

        if (removed) {
            return new ResponseEntity<>("Successfully left the event", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Registration not found", HttpStatus.NOT_FOUND);
        }
    }
}
