package br.com.fiap.clyvo.security;

import br.com.fiap.clyvo.exception.AcessoNegadoException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * NOVO - Sprint 4.
 * Le a identidade do usuario direto do JWT validado pelo Spring Security.
 * Regra do projeto: nenhuma decisao de ownership pode usar ID vindo do corpo
 * da requisicao ou da URL; tudo sai daqui.
 */
@Component
public class AuthUser {

    private Jwt tokenAtual() {
        Authentication autenticacao =
                SecurityContextHolder.getContext().getAuthentication();

        if (autenticacao instanceof JwtAuthenticationToken tokenJwt) {
            return tokenJwt.getToken();
        }

        throw new AcessoNegadoException("Requisicao sem usuario autenticado.");
    }

    /** ID do tutor ou veterinario logado, lido da claim "id" do token. */
    public Long getId() {
        Object claim = tokenAtual().getClaim("id");

        if (claim instanceof Number numero) {
            return numero.longValue();
        }

        throw new AcessoNegadoException("Token sem identificacao de usuario.");
    }

    /** Perfil do usuario logado: TUTOR ou VETERINARIO. */
    public String getPerfil() {
        Object claim = tokenAtual().getClaim("role");
        return claim == null ? "" : claim.toString();
    }

    public boolean isTutor() {
        return "TUTOR".equals(getPerfil());
    }

    public boolean isVeterinario() {
        return "VETERINARIO".equals(getPerfil());
    }
}
