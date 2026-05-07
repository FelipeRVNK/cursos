package com.example.cursos.controller;

import com.example.cursos.model.Aluno;
import com.example.cursos.model.Curso;
import com.example.cursos.repository.AlunoRepository;
import com.example.cursos.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cursos")
public class CursoController {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private AlunoRepository alunoRepository;


    @GetMapping
    public List<Curso> listar() {
        return cursoRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Curso criar(@RequestBody Curso curso) {
        return cursoRepository.save(curso);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!cursoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        cursoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }



    @PostMapping("/{cursoId}/alunos")
    public ResponseEntity<Aluno> matricularAluno(@PathVariable Long cursoId, @RequestBody Aluno aluno) {
        Optional<Curso> cursoOpt = cursoRepository.findById(cursoId);

        if (cursoOpt.isPresent()) {
            Curso curso = cursoOpt.get();
            aluno.setCurso(curso);
            Aluno alunoSalvo = alunoRepository.save(aluno);
            return ResponseEntity.status(HttpStatus.CREATED).body(alunoSalvo);
        }

        return ResponseEntity.notFound().build();
    }
}