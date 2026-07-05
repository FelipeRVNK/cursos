package com.example.cursos.repository;

import com.example.cursos.model.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    List<Matricula> findByCursoId(Long cursoId);
    List<Matricula> findByAlunoId(Long alunoId);
    boolean existsByAlunoIdAndCursoId(Long alunoId, Long cursoId);
    List<Matricula> findByUsuarioId(String usuarioId);
    void deleteByUsuarioId(String usuarioId);
}