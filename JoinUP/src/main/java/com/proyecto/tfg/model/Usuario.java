package com.proyecto.tfg.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a user in the application
 */
@Entity
@Table(name = "Usuarios")
public class Usuario {

    // ========================
    // Main user fields
    // ========================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private int idCliente;  // Unique user ID (primary key)

    @Column(name = "d_nombre", nullable = false, length = 45)
    private String nombre;  // User's first name

    @Column(name = "d_ap1", length = 45)
    private String ap1;  // First surname

    @Column(name = "d_ap2", length = 45)
    private String ap2;  // Second surname

    @Column(name = "d_email", unique = true, nullable = false, length = 100)
    private String email;  // Unique email

    @Column(name = "d_password", nullable = false, length = 90)
    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-]).{8,}$",
            message = "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character"
    )
    private String password;  // Password (usually encrypted)

    // ========================
    // Fecha de nacimiento
    // ========================
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    @Column(name = "fec_nac", nullable = false)
    private LocalDate fecNac;


    // ========================
    // Phone number validation
    // ========================
    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+?\\d{9}$",
            message = "Invalid phone number. Must contain 9 digits."
    )
    @Column(name = "d_numTelefono", nullable = false, length = 90)
    private String numTelefono; // Valid phone number

    @Enumerated(EnumType.STRING)
    @Column(name = "d_rol", nullable = false)
    private Rol rol; // User role (e.g., FREE, ADMIN, etc.)



    // ========================
    // User address
    // ========================
    @Column(name = "dir_provin", length = 45)
    private String provincia;

    @Column(name = "dir_pobla", length = 45)
    private String poblacion;    // City / Town

    @Column(name = "dir_infoExtra", length = 255)
    private String infoExtra;    // Additional address info

    // ========================
    // Social media links
    // ========================
    @Column(name = "url_twitter", length = 255)
    private String urlTwitter;

    @Column(name = "url_facebook", length = 255)
    private String urlFacebook;

    @Column(name = "url_linkedin", length = 255)
    private String urlLinkedin;

    @Column(name = "url_instagram", length = 255)
    private String urlInstagram;


    // ========================
    // Interests, max 3
    // ========================
    @Column(name = "int_v1", length = 45)
    private String intV1;

    @Column(name = "int_v2", length = 45)
    private String intV2;

    @Column(name = "int_v3", length = 45)
    private String intV3;

    @Column(name = "imagen", columnDefinition = "LONGTEXT")
    private String imagen; // Profile image URL

    // ========================
    // Relations with other models
    // ========================
    @OneToMany(mappedBy = "usuario")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<UsuarioEvento> eventos;

    // ========================
    // Getters and Setters
    // ========================
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getAp1() { return ap1; }
    public void setAp1(String ap1) { this.ap1 = ap1; }

    public String getAp2() { return ap2; }
    public void setAp2(String ap2) { this.ap2 = ap2; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDate getFecNac() { return fecNac; }
    public void setFecNac(LocalDate fecNac) { this.fecNac = fecNac; }

    public String getNumTelefono() { return numTelefono; }
    public void setNumTelefono(String numTelefono) { this.numTelefono = numTelefono; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public String getPoblacion() { return poblacion; }
    public void setPoblacion(String poblacion) { this.poblacion = poblacion; }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
    public String getInfoExtra() { return infoExtra; }
    public void setInfoExtra(String infoExtra) { this.infoExtra = infoExtra; }

    public String getIntV1() { return intV1; }
    public void setIntV1(String intV1) { this.intV1 = intV1; }

    public String getIntV2() { return intV2; }
    public void setIntV2(String intV2) { this.intV2 = intV2; }

    public String getIntV3() { return intV3; }
    public void setIntV3(String intV3) { this.intV3 = intV3; }
    public String getUrlTwitter() { return urlTwitter; }
    public void setUrlTwitter(String urlTwitter) { this.urlTwitter = urlTwitter; }

    public String getUrlFacebook() { return urlFacebook; }
    public void setUrlFacebook(String urlFacebook) { this.urlFacebook = urlFacebook; }

    public String getUrlLinkedin() { return urlLinkedin; }
    public void setUrlLinkedin(String urlLinkedin) { this.urlLinkedin = urlLinkedin; }

    public String getUrlInstagram() { return urlInstagram; }
    public void setUrlInstagram(String urlInstagram) { this.urlInstagram = urlInstagram; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public List<UsuarioEvento> getEventos() { return eventos; }
    public void setEventos(List<UsuarioEvento> eventos) { this.eventos = eventos; }
}
