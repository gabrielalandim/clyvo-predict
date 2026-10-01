package br.com.fiap.clyvo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.net.SocketTimeoutException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class TratadorDeErros {

    // ===== NOVO - Sprint 4 =====
    // Estes dois handlers precisam existir porque AcessoNegadoException e
    // AccessDeniedException sao RuntimeException: sem eles, todo 403 do
    // service cairia no handler generico la embaixo e viraria 400.

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<String> tratarAcessoNegado(AcessoNegadoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> tratarAcessoNegadoSpring(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Acesso negado para o usuario autenticado.");
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<String> tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    // ===== FIM DO BLOCO NOVO =====

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<String> tratarRespostaIa(RestClientException ex) {
        Throwable cause = ex;
        while (cause != null) {
            if (cause instanceof SocketTimeoutException) {
                return ResponseEntity.status(504).body("A IA excedeu o tempo de espera. Tente novamente.");
            }
            cause = cause.getCause();
        }
        if (ex instanceof ResourceAccessException) {
            return ResponseEntity.status(503).body("Não foi possível conectar ao serviço de IA.");
        }
        return ResponseEntity.status(502).body("O serviço de IA retornou uma resposta inválida ou um erro.");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> tratarStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> tratarTamanhoImagem(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(413).body("Imagem muito grande. O limite é 10 MB por arquivo.");
    }

    // 1. Trata os erros do Bean Validation (@NotBlank, @NotNull, @Email, etc)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DadosErroValidacao>> tratarErro400(MethodArgumentNotValidException ex) {
        var erros = ex.getFieldErrors();
        return ResponseEntity.badRequest().body(erros.stream().map(DadosErroValidacao::new).toList());
    }

    // 2. Trata as nossas regras de negócio (ex: "E-mail já cadastrado")
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> tratarErroRegraDeNegocio(RuntimeException ex) {
        return ResponseEntity.badRequest().body("Erro: " + ex.getMessage());
    }
}
