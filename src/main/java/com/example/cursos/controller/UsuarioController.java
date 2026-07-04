package com.example.cursos.controller;

import com.example.cursos.service.SqsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    private final SqsService sqsService;

    public UsuarioController(SqsService sqsService) {
        this.sqsService = sqsService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable String id) {
        logger.info("Deletando usuário id={}, publicando no SQS", id);
        sqsService.publicarUsuarioDeletado(id);
        return ResponseEntity.noContent().build();
    }
}