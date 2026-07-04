package com.example.cursos.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

@Service
public class AvatarService {

    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public String gerarESalvarAvatar(String email, String nome) {
        try {
            String hashEmail = gerarMD5(email.trim().toLowerCase());
            String avatarUrl = "https://www.gravatar.com/avatar/" + hashEmail + "?d=404&s=200";

            byte[] imageBytes = baixarImagem(avatarUrl);

            if (imageBytes == null) {
                logger.info("Gravatar não encontrado. Gerando pelo UI-Avatars...");
                String nomeFormatado = nome.replace(" ", "+");
                avatarUrl = "https://ui-avatars.com/api/?name=" + nomeFormatado + "&background=random&format=png";
                imageBytes = baixarImagem(avatarUrl);
            }

            if (imageBytes != null) {
                return uploadParaS3(hashEmail + ".png", imageBytes);
            }

        } catch (Exception e) {
            logger.error("Erro ao processar avatar: ", e);
        }
        return null;
    }

    private byte[] baixarImagem(String stringUrl) {
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.connect();

            if (connection.getResponseCode() == 200) {
                return connection.getInputStream().readAllBytes();
            }
        } catch (Exception e) {
            logger.warn("Erro ao baixar imagem da URL: {}", stringUrl);
        }
        return null;
    }

    private String uploadParaS3(String fileName, byte[] bytes) {
        logger.info("Fazendo upload da imagem {} para o S3...", fileName);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        metadata.setContentType("image/png");

        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, new ByteArrayInputStream(bytes), metadata));

        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    private String gerarMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "default";
        }
    }
    public void deletarAvatar(String avatarUrl) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            try {
                String fileName = avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
                logger.info("Deletando imagem {} do bucket S3...", fileName);

                amazonS3.deleteObject(bucketName, fileName);
            } catch (Exception e) {
                logger.error("Erro ao deletar avatar do S3: ", e);
            }
        }
    }
}