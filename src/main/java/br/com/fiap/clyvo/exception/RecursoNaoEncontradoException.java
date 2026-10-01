package br.com.fiap.clyvo.exception;

/**
 * NOVO - Sprint 4.
 * Lancada quando o recurso pedido nao existe no banco. O TratadorDeErros
 * converte esta excecao em 404 (antes tudo virava 400).
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
