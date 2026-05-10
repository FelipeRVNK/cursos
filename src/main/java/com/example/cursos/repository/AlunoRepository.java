package com.example.cursos.repository;

import com.example.cursos.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    @Query("SELECT a FROM Aluno a JOIN a.matriculas m WHERE m.curso.id = :cursoId")
    List<Aluno> findByCursoId(Long cursoId);
}