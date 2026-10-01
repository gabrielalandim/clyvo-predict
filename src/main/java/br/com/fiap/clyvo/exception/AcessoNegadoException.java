package br.com.fiap.clyvo.exception;

/**
 * NOVO - Sprint 4.
 * Lancada quando o usuario esta autenticado mas o recurso nao pertence a ele.
 * O TratadorDeErros converte esta excecao em 403.
 */
public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
