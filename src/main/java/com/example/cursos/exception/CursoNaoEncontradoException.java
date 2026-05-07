package com.example.cursos.exception;

public class CursoNaoEncontradoException extends RuntimeException {
    public CursoNaoEncontradoException(Long id) {
        super("Curso não encontrado com id: " + id);
    }
}