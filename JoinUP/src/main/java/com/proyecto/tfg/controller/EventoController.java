package com.proyecto.tfg.controller;

import com.proyecto.tfg.constants.TFGConstants;
import com.proyecto.tfg.model.Evento;
import com.proyecto.tfg.model.Response;
import com.proyecto.tfg.model.Usuario;
import com.proyecto.tfg.service.IEventoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "*")
public class EventoController {

    @Autowired
    private IEventoService eventoService;


    // ========================
    // CREATE EVENT
    // ========================
    @Operation(
            summary = "Create Event",
            description = "Creates a new event and stores it in the database."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Event created successfully",
                    content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "Invalid event data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/create")
    public ResponseEntity<Response> createEvent(@Valid @RequestBody Evento evento) {
        eventoService.save(evento);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new Response(TFGConstants.STATUS_201, TFGConstants.MESSAGE_201));
    }


    // ========================
    // UPDATE EVENT
    // ========================
    @Operation(
            summary = "Update Event",
            description = "Updates an existing event based on its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event updated successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "400", description = "Invalid event data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateEvent(@PathVariable int id, @Valid @RequestBody Evento evento) {
        try {
            Evento updated = eventoService.updateEvento(id, evento);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new Response(TFGConstants.STATUS_200, "Event updated successfully."));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new Response(TFGConstants.STATUS_417, e.getMessage()));
        }
    }


    // ========================
    // LIST ALL EVENTS
    // ========================
    @Operation(
            summary = "List All Events",
            description = "Returns a list of all registered events."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Evento.class))),
            @ApiResponse(responseCode = "204", description = "No events found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/all")
    public ResponseEntity<List<Evento>> listAll() {
        List<Evento> eventos = eventoService.listAll();
        if (eventos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(eventos);
    }


    // ========================
    // GET EVENT BY ID
    // ========================
    @Operation(
            summary = "Get Event by ID",
            description = "Fetches a specific event using its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event found",
                    content = @Content(schema = @Schema(implementation = Evento.class))),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/detalle/{id}")
    public ResponseEntity<Evento> getEvent(@PathVariable int id) {
        Optional<Evento> optionalEvento = eventoService.fetchEvento(id);

        return optionalEvento
                .map(evento -> ResponseEntity.ok(evento))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // ========================
    // SEARCH BY TITLE
    // ========================
    @Operation(
            summary = "Search Events by Title",
            description = "Returns events that match the given title, either partially or fully."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Events found",
                    content = @Content(schema = @Schema(implementation = Evento.class))),
            @ApiResponse(responseCode = "204", description = "No events match the title"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<Evento>> searchByTitle(@RequestParam("titulo") String title) {
        List<Evento> eventos = eventoService.findByTitulo(title);
        if (eventos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(eventos);
    }


    // ========================
    // SEARCH BY TAGS
    // ========================
    @Operation(
            summary = "Search Events by Tag",
            description = "Returns events that contain the given tag."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Events found"),
            @ApiResponse(responseCode = "204", description = "No events found with the given tag")
    })
    @GetMapping("/tags")
    public List<Evento> searchByTags(@RequestParam String tag) {
        return eventoService.findByTags(tag);
    }


    // ========================
    // GET PARTICIPANTS OF AN EVENT
    // ========================
    @Operation(
            summary = "Get Participants of an Event",
            description = "Retrieves users currently registered for a specific event."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Participants retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "204", description = "No participants found"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/participantes")
    public ResponseEntity<List<Usuario>> getParticipants(@PathVariable int id) {
        try {
            List<Usuario> participantes = eventoService.getParticipantes(id);

            if (participantes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(participantes);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // ========================
    // DELETE EVENT
    // ========================
    @Operation(
            summary = "Delete Event",
            description = "Deletes an event based on its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Integer id) {
        boolean deleted = eventoService.delete(id);

        if (deleted) {
            return ResponseEntity.ok("Event deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("Event not found.");
        }
    }

}
