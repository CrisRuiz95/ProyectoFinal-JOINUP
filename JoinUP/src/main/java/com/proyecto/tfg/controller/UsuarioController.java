package com.proyecto.tfg.controller;

import com.proyecto.tfg.constants.TFGConstants;
import com.proyecto.tfg.model.Response;
import com.proyecto.tfg.model.Usuario;
import com.proyecto.tfg.service.IUsuarioService;

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

/**
 * REST Controller to manage CRUD operations for users.
 * Includes endpoints to create, read, update and delete users.
 */

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;


    // ========================
    // LIST ALL USERS
    // ========================
    @Operation(
            summary = "List All Users",
            description = "Returns a list with all registered users."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public List<Usuario> listAllUsers() {
        return usuarioService.findAll();
    }


    // ========================
    // GET USER BY ID
    // ========================
    @Operation(
            summary = "Get User by ID",
            description = "Returns user details based on the provided ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUserById(@PathVariable Integer id) {
        Optional<Usuario> userOpt = usuarioService.fetchAccount(id);

        return userOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // ========================
    // CREATE USER ACCOUNT
    // ========================
    @Operation(
            summary = "Create User",
            description = "Creates a new user and stores it in the database."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = Response.class)))
    })
    @PostMapping("/create")
    public ResponseEntity<Response> createUser(@Valid @RequestBody Usuario usuario) {
        usuarioService.createAccount(usuario);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new Response(TFGConstants.STATUS_201, TFGConstants.MESSAGE_201));
    }


    // ========================
    // LOGIN USER
    // ========================
    @Operation(
            summary = "User Login",
            description = "Authenticates a user using email and password."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "401", description = "Invalid email or password",
                    content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = Response.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario loginRequest) {

        Optional<Usuario> usuarioOpt =
                usuarioService.login(loginRequest.getEmail(), loginRequest.getPassword());

        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(usuarioOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Response("401", "Invalid email or password"));
        }
    }


    // ========================
    // UPDATE USER BY ID
    // ========================
    @Operation(
            summary = "Update User",
            description = "Updates the details of an existing user based on ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "417", description = "Expectation failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = Response.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateUser(@PathVariable Integer id, @RequestBody Usuario usuario) {

        Optional<Usuario> existing = usuarioService.fetchAccount(id);

        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        usuario.setIdCliente(id);
        return ResponseEntity.ok(usuarioService.updateAccount(usuario));
    }


    // ========================
    // DELETE USER BY ID
    // ========================
    @Operation(
            summary = "Delete User",
            description = "Deletes an existing user based on ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = Response.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        boolean deleted = usuarioService.deleteAccount(id);

        if (deleted) {
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("User not found.");
        }
    }

}
