package com.example.cursos.service;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SqsService {

    private static final Logger logger = LoggerFactory.getLogger(SqsService.class);

    private final SqsTemplate sqsTemplate;
    private final String queueName;

    public SqsService(SqsTemplate sqsTemplate,
                      @Value("${sqs.queue.user-deletion}") String queueName) {
        this.sqsTemplate = sqsTemplate;
        this.queueName = queueName;
    }

    public void publicarUsuarioDeletado(String usuarioId) {
        logger.info("Publicando evento de exclusão no SQS para usuário: {}", usuarioId);
        sqsTemplate.send(queueName, usuarioId);
    }
}