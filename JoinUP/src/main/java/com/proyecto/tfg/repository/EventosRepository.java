package com.proyecto.tfg.repository;

import com.proyecto.tfg.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventosRepository extends JpaRepository<Evento, Integer> {

    // Search events by title
    List<Evento> findByTituloContainingIgnoreCase(String titulo);

    // Search events by tag in tag1, tag2, or tag3
    List<Evento> findByTag1ContainingIgnoreCaseOrTag2ContainingIgnoreCaseOrTag3ContainingIgnoreCase(
            String tag1, String tag2, String tag3
    );
}
