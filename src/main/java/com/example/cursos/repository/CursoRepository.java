package com.example.cursos.repository;

import com.example.cursos.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    List<Curso> findByNomeContainingIgnoreCase(String nome);
    List<Curso> findByCategoriaIgnoreCase(String categoria);
    List<Curso> findByNomeContainingIgnoreCaseAndCategoriaIgnoreCase(String nome, String categoria);
}