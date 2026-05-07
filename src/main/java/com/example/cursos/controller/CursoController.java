package com.example.cursos.controller;

import com.example.cursos.exception.AlunoNaoEncontradoException;
import com.example.cursos.exception.CursoNaoEncontradoException;
import com.example.cursos.model.Aluno;
import com.example.cursos.model.Curso;
import com.example.cursos.repository.AlunoRepository;
import com.example.cursos.repository.CursoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/cursos")
public class CursoController {

    private static final Logger logger = LoggerFactory.getLogger(CursoController.class);

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    // GET /cursos?nome=java&categoria=backend&ordenar=nome
    @GetMapping
    public List<Curso> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false, defaultValue = "id") String ordenar
    ) {
        logger.info("Listando cursos | nome={} | categoria={} | ordenar={}", nome, categoria, ordenar);

        List<Curso> cursos;

        if (nome != null && categoria != null) {
            cursos = cursoRepository.findByNomeContainingIgnoreCaseAndCategoriaIgnoreCase(nome, categoria);
        } else if (nome != null) {
            cursos = cursoRepository.findByNomeContainingIgnoreCase(nome);
        } else if (categoria != null) {
            cursos = cursoRepository.findByCategoriaIgnoreCase(categoria);
        } else {
            cursos = cursoRepository.findAll();
        }

        if (ordenar.equals("nome")) {
            cursos.sort(Comparator.comparing(Curso::getNome));
        } else if (ordenar.equals("categoria")) {
            cursos.sort(Comparator.comparing(Curso::getCategoria));
        }

        return cursos;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Curso criar(@RequestBody Curso curso) {
        logger.info("Criando curso: {}", curso.getNome());
        return cursoRepository.save(curso);
    }

    // PUT /cursos/{id}
    @PutMapping("/{id}")
    public Curso atualizar(@PathVariable Long id, @RequestBody Curso cursoAtualizado) {
        logger.info("Atualizando curso id={}", id);
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new CursoNaoEncontradoException(id));

        curso.setNome(cursoAtualizado.getNome());
        curso.setCategoria(cursoAtualizado.getCategoria());

        return cursoRepository.save(curso);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        logger.info("Deletando curso id={}", id);
        if (!cursoRepository.existsById(id)) {
            throw new CursoNaoEncontradoException(id);
        }
        cursoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{cursoId}/alunos")
    public ResponseEntity<Aluno> matricularAluno(@PathVariable Long cursoId, @RequestBody Aluno aluno) {
        logger.info("Matriculando aluno {} no curso id={}", aluno.getNome(), cursoId);
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new CursoNaoEncontradoException(cursoId));

        aluno.setCurso(curso);
        Aluno alunoSalvo = alunoRepository.save(aluno);
        return ResponseEntity.status(HttpStatus.CREATED).body(alunoSalvo);
    }

    // DELETE de aluno (ação destrutiva também protegida pelo Security)
    @DeleteMapping("/{cursoId}/alunos/{alunoId}")
    public ResponseEntity<Void> removerAluno(@PathVariable Long cursoId, @PathVariable Long alunoId) {
        logger.info("Removendo aluno id={} do curso id={}", alunoId, cursoId);
        // valida que o curso existe
        cursoRepository.findById(cursoId)
                .orElseThrow(() -> new CursoNaoEncontradoException(cursoId));

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new AlunoNaoEncontradoException(alunoId));

        alunoRepository.delete(aluno);
        return ResponseEntity.noContent().build();
    }
}