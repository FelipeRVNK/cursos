package com.example.cursos.controller;

import com.example.cursos.exception.AlunoNaoEncontradoException;
import com.example.cursos.model.Aluno;
import com.example.cursos.model.Matricula;
import com.example.cursos.repository.AlunoRepository;
import com.example.cursos.repository.MatriculaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    private static final Logger logger = LoggerFactory.getLogger(AlunoController.class);

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @GetMapping
    public List<Aluno> listar() {
        logger.info("Listando todos os alunos");
        return alunoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Aluno buscar(@PathVariable Long id) {
        logger.info("Buscando aluno id={}", id);
        return alunoRepository.findById(id)
                .orElseThrow(() -> new AlunoNaoEncontradoException(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Aluno criar(@RequestBody Aluno aluno) {
        logger.info("Criando aluno: {}", aluno.getNome());
        return alunoRepository.save(aluno);
    }

    @PutMapping("/{id}")
    public Aluno atualizar(@PathVariable Long id, @RequestBody Aluno alunoAtualizado) {
        logger.info("Atualizando aluno id={}", id);
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new AlunoNaoEncontradoException(id));
        aluno.setNome(alunoAtualizado.getNome());
        aluno.setEmail(alunoAtualizado.getEmail());
        return alunoRepository.save(aluno);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        logger.info("Deletando aluno id={}", id);
        if (!alunoRepository.existsById(id)) {
            throw new AlunoNaoEncontradoException(id);
        }
        alunoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/cursos")
    public List<Matricula> listarCursos(@PathVariable Long id) {
        logger.info("Listando cursos do aluno id={}", id);
        alunoRepository.findById(id)
                .orElseThrow(() -> new AlunoNaoEncontradoException(id));
        return matriculaRepository.findByAlunoId(id);
    }
}