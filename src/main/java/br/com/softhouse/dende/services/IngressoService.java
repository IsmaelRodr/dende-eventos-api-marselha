package br.com.softhouse.dende.services;

import br.com.softhouse.dende.exceptions.DadosInvalidosException;

import br.com.softhouse.dende.exceptions.evento.EventoNaoEncontradoException;
import br.com.softhouse.dende.exceptions.evento.EventoInativoException;
import br.com.softhouse.dende.exceptions.evento.EventoExpiradoException;
import br.com.softhouse.dende.exceptions.evento.EventoSemIngressosDisponiveisException;

import br.com.softhouse.dende.exceptions.ingresso.IngressoNaoEncontradoException;
import br.com.softhouse.dende.exceptions.ingresso.CompraIngressoException;

import br.com.softhouse.dende.exceptions.usuario.UsuarioNaoEncontradoException;
import br.com.softhouse.dende.exceptions.usuario.UsuarioInativoException;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;

import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class IngressoService {

    private final Repositorio repositorio;

    public IngressoService() {
        this.repositorio = Repositorio.getInstance();
    }

    public Map<String, Object> comprarIngresso(
            Long usuarioId,
            Long eventoId
    ) {

        if (usuarioId == null || usuarioId <= 0) {

            throw new DadosInvalidosException(
                    "ID do usuário inválido."
            );
        }

        if (eventoId == null || eventoId <= 0) {

            throw new DadosInvalidosException(
                    "ID do evento inválido."
            );
        }

        Usuario usuario =
                repositorio.buscarUsuarioPorId(usuarioId);

        if (usuario == null) {

            throw new UsuarioNaoEncontradoException(
                    "Usuário não encontrado."
            );
        }

        if (!usuario.isAtivo()) {

            throw new UsuarioInativoException(
                    "Usuário está inativo."
            );
        }

        Evento evento =
                repositorio.buscarEvento(eventoId);

        if (evento == null) {

            throw new EventoNaoEncontradoException(
                    "Evento não encontrado."
            );
        }

        if (!evento.isEventoAtivo()) {

            throw new EventoInativoException(
                    "Evento está inativo."
            );
        }

        if (evento.getIngressosDisponiveis() <= 0) {

            throw new EventoSemIngressosDisponiveisException(
                    "Ingressos esgotados."
            );
        }

        if (evento.getDataInicio()
                .isBefore(LocalDateTime.now())) {

            throw new EventoExpiradoException(
                    "Evento já iniciado."
            );
        }

        Map<String, Object> resultado =
                repositorio.comprarIngresso(
                        usuarioId,
                        eventoId
                );

        if (resultado == null) {

            throw new CompraIngressoException(
                    "Falha ao comprar ingresso."
            );
        }

        return resultado;
    }

    public void cancelarIngresso(
            Long usuarioId,
            Long ingressoId
    ) {

        if (usuarioId == null || usuarioId <= 0) {

            throw new DadosInvalidosException(
                    "ID do usuário inválido."
            );
        }

        if (ingressoId == null || ingressoId <= 0) {

            throw new DadosInvalidosException(
                    "ID do ingresso inválido."
            );
        }

        Usuario usuario =
                repositorio.buscarUsuarioPorId(usuarioId);

        if (usuario == null) {

            throw new UsuarioNaoEncontradoException(
                    "Usuário não encontrado."
            );
        }

        boolean cancelado =
                repositorio.cancelarIngresso(
                        usuarioId,
                        ingressoId
                );

        if (!cancelado) {

            throw new IngressoNaoEncontradoException(
                    "Ingresso não encontrado ou já cancelado."
            );
        }
    }

    public List<Ingresso> listarIngressosUsuario(
            Long usuarioId
    ) {

        if (usuarioId == null || usuarioId <= 0) {

            throw new DadosInvalidosException(
                    "ID do usuário inválido."
            );
        }

        Usuario usuario =
                repositorio.buscarUsuarioPorId(usuarioId);

        if (usuario == null) {

            throw new UsuarioNaoEncontradoException(
                    "Usuário não encontrado."
            );
        }

        return repositorio
                .listarIngressosUsuario(usuarioId);
    }
}