package br.com.fiap.clyvo.service;

import br.com.fiap.clyvo.dto.ia.ClyvoAiRequestDTO;
import br.com.fiap.clyvo.dto.ia.ClyvoAiResponseDTO;
import br.com.fiap.clyvo.dto.ia.PetRegistrationAiResponseDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ClyvoAiService {

    private final RestClient restClient;

    public ClyvoAiService(
            RestClient.Builder builder,
            @Value("${clyvo.ai.url}") String clyvoAiUrl
    ) {
        this.restClient = builder
                .baseUrl(clyvoAiUrl)
                .build();
    }

    public ClyvoAiResponseDTO calcularScore(
            ClyvoAiRequestDTO request
    ) {
        return restClient
                .post()
                .uri("/api/v2/score/consolidado")
                .body(request)
                .retrieve()
                .body(ClyvoAiResponseDTO.class);
    }

    public PetRegistrationAiResponseDTO analisarFotoCadastro(
            MultipartFile imagem
    ) throws IOException {

        ByteArrayResource resource = new ByteArrayResource(
                imagem.getBytes()
        ) {
            @Override
            public String getFilename() {
                return imagem.getOriginalFilename();
            }
        };

        MultiValueMap<String, Object> body =
                new LinkedMultiValueMap<>();

        body.add("imagem", resource);

        return restClient
                .post()
                .uri("/api/v2/pets/analyze-registration-photo")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(PetRegistrationAiResponseDTO.class);
    }
}