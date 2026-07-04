package com.example.cursos.service;

import com.example.cursos.repository.MatriculaRepository;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioDeletadoListener {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioDeletadoListener.class);

    private final MatriculaRepository matriculaRepository;

    public UsuarioDeletadoListener(MatriculaRepository matriculaRepository) {
        this.matriculaRepository = matriculaRepository;
    }

    @SqsListener("${sqs.queue.user-deletion}")
    @Transactional
    public void onUsuarioDeletado(String usuarioId) {
        logger.info("Evento SQS recebido: excluindo matrículas do usuário {}", usuarioId);
        matriculaRepository.deleteByUsuarioId(usuarioId);
        logger.info("Matrículas do usuário {} removidas com sucesso", usuarioId);
    }
}