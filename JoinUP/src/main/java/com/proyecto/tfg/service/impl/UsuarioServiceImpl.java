package com.proyecto.tfg.service.impl;

import com.proyecto.tfg.exception.UserAlreadyExistsException;
import com.proyecto.tfg.model.Rol;
import com.proyecto.tfg.model.Usuario;
import com.proyecto.tfg.repository.UsuariosRepository;
import com.proyecto.tfg.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    // BCrypt encoder for password encryption and verification
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UsuariosRepository repo;

    @Override
    public void createAccount(Usuario usuario) {
        // Check if user with same email already exists
        Optional<Usuario> optionalUsuario = repo.findByEmail(usuario.getEmail());
        if (optionalUsuario.isPresent()) {
            throw new UserAlreadyExistsException(
                    "Usuario ya registrado con este e-mail: " + usuario.getEmail());
        }

        // Save user with encrypted password
        repo.save(createNewAccount(usuario));
    }

    // Helper method to create new user account with default role and encrypted password
    private Usuario createNewAccount(Usuario usuario) {
        Usuario newUsuario = new Usuario();

        // === Personal data ===
        newUsuario.setNombre(usuario.getNombre());
        newUsuario.setAp1(usuario.getAp1());
        newUsuario.setAp2(usuario.getAp2());
        newUsuario.setEmail(usuario.getEmail());
        newUsuario.setNumTelefono(usuario.getNumTelefono());

        // === Encrypt password with BCrypt ===
        newUsuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // === Birth date ===
        newUsuario.setFecNac(usuario.getFecNac());

        // === Address fields ===
        newUsuario.setProvincia(usuario.getProvincia());
        newUsuario.setPoblacion(usuario.getPoblacion());
        newUsuario.setInfoExtra(usuario.getInfoExtra());

        // === User interests ===
        newUsuario.setIntV1(usuario.getIntV1());
        newUsuario.setIntV2(usuario.getIntV2());
        newUsuario.setIntV3(usuario.getIntV3());

        // === Profile image ===
        newUsuario.setImagen(usuario.getImagen());

        // === Social media links ===
        newUsuario.setUrlTwitter(usuario.getUrlTwitter());
        newUsuario.setUrlFacebook(usuario.getUrlFacebook());
        newUsuario.setUrlLinkedin(usuario.getUrlLinkedin());
        newUsuario.setUrlInstagram(usuario.getUrlInstagram());

        // === Assign role (default to GRATUITO if not provided) ===
        if (usuario.getRol() == null) {
            newUsuario.setRol(Rol.GRATUITO);
        } else {
            newUsuario.setRol(usuario.getRol());
        }

        return newUsuario;
    }


    @Override
    public Optional<Usuario> fetchAccount(int idCliente) {
        return repo.findById(idCliente);
    }

    @Override
    public List<Usuario> findAll() {
        return repo.findAll();
    }

    @Override
    public boolean updateAccount(Usuario usuario) {
        return repo.findById(usuario.getIdCliente()).map(existing -> {

            // === Personal data ===
            if (usuario.getNombre() != null) existing.setNombre(usuario.getNombre());
            if (usuario.getAp1() != null) existing.setAp1(usuario.getAp1());
            if (usuario.getAp2() != null) existing.setAp2(usuario.getAp2());
            if (usuario.getEmail() != null) existing.setEmail(usuario.getEmail());
            if (usuario.getNumTelefono() != null) existing.setNumTelefono(usuario.getNumTelefono());

            // === Birth date ===
            if (usuario.getFecNac() != null) existing.setFecNac(usuario.getFecNac());

            // === Password (encrypt if provided) ===
            if (usuario.getPassword() != null && !usuario.getPassword().isBlank()) {
                existing.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }

            // === Address information ===
            if (usuario.getProvincia() != null) existing.setProvincia(usuario.getProvincia());
            if (usuario.getPoblacion() != null) existing.setPoblacion(usuario.getPoblacion());
            if (usuario.getInfoExtra() != null) existing.setInfoExtra(usuario.getInfoExtra());

            // === User interests ===
            if (usuario.getIntV1() != null) existing.setIntV1(usuario.getIntV1());
            if (usuario.getIntV2() != null) existing.setIntV2(usuario.getIntV2());
            if (usuario.getIntV3() != null) existing.setIntV3(usuario.getIntV3());

            // === Profile image ===
            if (usuario.getImagen() != null) existing.setImagen(usuario.getImagen());

            // === Social media links ===
            if (usuario.getUrlTwitter() != null) existing.setUrlTwitter(usuario.getUrlTwitter());
            if (usuario.getUrlFacebook() != null) existing.setUrlFacebook(usuario.getUrlFacebook());
            if (usuario.getUrlLinkedin() != null) existing.setUrlLinkedin(usuario.getUrlLinkedin());
            if (usuario.getUrlInstagram() != null) existing.setUrlInstagram(usuario.getUrlInstagram());

            // === User role (updated only if provided) ===
            if (usuario.getRol() != null) existing.setRol(usuario.getRol());

            repo.save(existing);
            return true;

        }).orElse(false);
    }


    @Override
    public boolean deleteAccount(int idCliente) {
        Optional<Usuario> usuarioOpt = repo.findById(idCliente);
        if (usuarioOpt.isPresent()) {
            repo.delete(usuarioOpt.get());
            return true;
        }
        return false;
    }

    @Override
    public Optional<Usuario> login(String email, String password) {
        Optional<Usuario> userOpt = repo.findByEmail(email);

        if (userOpt.isEmpty()) {
            return Optional.empty(); // User not found
        }

        Usuario user = userOpt.get();

        // Compare input password with stored encrypted password
        if (passwordEncoder.matches(password, user.getPassword())) {
            return Optional.of(user); // Login successful
        }

        return Optional.empty(); // Incorrect password
    }
}
