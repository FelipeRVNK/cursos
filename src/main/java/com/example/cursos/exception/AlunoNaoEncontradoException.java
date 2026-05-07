package com.example.cursos.exception;

public class AlunoNaoEncontradoException extends RuntimeException {
    public AlunoNaoEncontradoException(Long id) {
        super("Aluno não encontrado com id: " + id);
    }
}