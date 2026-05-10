package com.example.cursos.exception;

public class MatriculaDuplicadaException extends RuntimeException {
    public MatriculaDuplicadaException(Long alunoId, Long cursoId) {
        super("Aluno " + alunoId + " já está matriculado no curso " + cursoId);
    }
}
