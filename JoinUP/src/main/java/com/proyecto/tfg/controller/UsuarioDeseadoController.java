package com.proyecto.tfg.controller;

import com.proyecto.tfg.service.IUsuarioDeseadoService;
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
@RequestMapping("/deseados")
@CrossOrigin(origins = "*")
public class UsuarioDeseadoController {

    @Autowired
    private IUsuarioDeseadoService wishlistService;

    // -------------------------------------------------------------
    // ✔ Toggle wishlist
    // -------------------------------------------------------------
    @Operation(
            summary = "Toggle wishlist status",
            description = "Adds an event to the user's wishlist if it is not already present. "
                    + "If it already exists, it removes it."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully toggled"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/toggle")
    public ResponseEntity<?> toggleWishlist(
            @RequestParam int idUsuario,
            @RequestParam int idEvento) {

        try {
            String message = wishlistService.toggleDeseado(idUsuario, idEvento);
            return new ResponseEntity<>(message, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Error processing request", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // -------------------------------------------------------------
    // ✔ Check if event is in wishlist
    // -------------------------------------------------------------
    @Operation(
            summary = "Check if event is wishlisted",
            description = "Returns TRUE if the event is currently marked as wishlisted by the user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status returned successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    @GetMapping("/check")
    public ResponseEntity<?> isWishlisted(
            @RequestParam int idUsuario,
            @RequestParam int idEvento) {

        boolean isWishlisted = wishlistService.esDeseado(idUsuario, idEvento);
        return new ResponseEntity<>(isWishlisted, HttpStatus.OK);
    }

    // -------------------------------------------------------------
    // ✔ Remove from wishlist
    // -------------------------------------------------------------
    @Operation(
            summary = "Remove event from wishlist",
            description = "Deletes the event from the user's wishlist permanently."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFromWishlist(
            @RequestParam int userId,
            @RequestParam int eventId) {

        wishlistService.eliminarDeseado(userId, eventId);
        return new ResponseEntity<>("Deleted successfully", HttpStatus.OK);
    }
}
