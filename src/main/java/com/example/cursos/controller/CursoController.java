package com.example.cursos.controller;

import com.example.cursos.exception.AlunoNaoEncontradoException;
import com.example.cursos.exception.CursoNaoEncontradoException;
import com.example.cursos.exception.MatriculaDuplicadaException;
import com.example.cursos.model.Aluno;
import com.example.cursos.model.Curso;
import com.example.cursos.model.Matricula;
import com.example.cursos.repository.AlunoRepository;
import com.example.cursos.repository.CursoRepository;
import com.example.cursos.repository.MatriculaRepository;
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

    @Autowired
    private MatriculaRepository matriculaRepository;

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

    @GetMapping("/{id}")
    public Curso buscar(@PathVariable Long id) {
        logger.info("Buscando curso id={}", id);
        return cursoRepository.findById(id)
                .orElseThrow(() -> new CursoNaoEncontradoException(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Curso criar(@RequestBody Curso curso) {
        logger.info("Criando curso: {}", curso.getNome());
        return cursoRepository.save(curso);
    }

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

    @GetMapping("/{id}/alunos")
    public List<Aluno> listarAlunos(@PathVariable Long id) {
        logger.info("Listando alunos do curso id={}", id);
        cursoRepository.findById(id)
                .orElseThrow(() -> new CursoNaoEncontradoException(id));
        return alunoRepository.findByCursoId(id);
    }

    @PostMapping("/{cursoId}/alunos/{alunoId}")
    public ResponseEntity<Matricula> matricularAluno(
            @PathVariable Long cursoId,
            @PathVariable Long alunoId) {

        logger.info("Matriculando aluno id={} no curso id={}", alunoId, cursoId);

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new CursoNaoEncontradoException(cursoId));

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new AlunoNaoEncontradoException(alunoId));

        if (matriculaRepository.existsByAlunoIdAndCursoId(alunoId, cursoId)) {
            throw new MatriculaDuplicadaException(alunoId, cursoId);
        }

        Matricula matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setCurso(curso);

        return ResponseEntity.status(HttpStatus.CREATED).body(matriculaRepository.save(matricula));
    }

    @DeleteMapping("/{cursoId}/alunos/{alunoId}")
    public ResponseEntity<Void> cancelarMatricula(
            @PathVariable Long cursoId,
            @PathVariable Long alunoId) {

        logger.info("Cancelando matrícula do aluno id={} no curso id={}", alunoId, cursoId);

        cursoRepository.findById(cursoId)
                .orElseThrow(() -> new CursoNaoEncontradoException(cursoId));

        Matricula matricula = matriculaRepository.findByCursoId(cursoId).stream()
                .filter(m -> m.getAluno().getId().equals(alunoId))
                .findFirst()
                .orElseThrow(() -> new AlunoNaoEncontradoException(alunoId));

        matriculaRepository.delete(matricula);
        return ResponseEntity.noContent().build();
    }
}