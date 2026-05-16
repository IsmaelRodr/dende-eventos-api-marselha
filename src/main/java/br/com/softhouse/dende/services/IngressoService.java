package br.com.softhouse.dende.services;

import br.com.softhouse.dende.dto.ingresso.*;
import br.com.softhouse.dende.dto.usuario.*;
import br.com.softhouse.dende.exceptions.DadosInvalidosException;
import br.com.softhouse.dende.exceptions.evento.*;
import br.com.softhouse.dende.exceptions.ingresso.*;
import br.com.softhouse.dende.exceptions.usuario.UsuarioInativoException;
import br.com.softhouse.dende.exceptions.usuario.UsuarioNaoEncontradoException;
import br.com.softhouse.dende.mapper.IngressoMapper;
import br.com.softhouse.dende.mapper.UsuarioMapper;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class IngressoService {

    private final Repositorio repositorio = Repositorio.getInstance();

    public ResultadoCompraIngressoDto comprarIngresso(Long usuarioId, Long eventoId, Long organizadorId) {
        Usuario usuario = repositorio.buscarUsuarioPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));
        if (!usuario.isAtivo()) throw new UsuarioInativoException("Usuário inativo.");

        Evento evento = repositorio.buscarEventoPorId(eventoId)
                .orElseThrow(() -> new EventoNaoEncontradoException("Evento não encontrado."));
        if (!evento.isEventoAtivo()) throw new EventoInativoException("Evento inativo.");
        if (evento.getIngressosDisponiveis() <= 0)
            throw new EventoSemIngressosDisponiveisException("Ingressos esgotados.");
        if (evento.getDataInicio().isBefore(LocalDateTime.now()))
            throw new EventoExpiradoException("Evento já iniciado.");
        if (!repositorio.isEventoDoOrganizador(organizadorId, eventoId)) {
            throw new EventoNaoEncontradoException("Evento não pertence ao organizador.");
        }
        List<Ingresso> ingressosGerados = new ArrayList<>();
        double valorTotal = 0.0;
        if (evento.getEventoPrincipal() != null) {
            Evento principal = evento.getEventoPrincipal();
            if (!principal.isEventoAtivo())
                throw new EventoInativoException("Evento principal está inativo.");
            if (principal.getDataInicio().isBefore(LocalDateTime.now()))
                throw new EventoExpiradoException("Evento principal já iniciado.");

            Ingresso ingressoPrincipal = new Ingresso(null, usuario, principal,
                    principal.getPrecoUnitarioIngresso(), usuario.getEmail());
            repositorio.salvarIngresso(ingressoPrincipal);
            usuario.addIngresso(ingressoPrincipal);
            principal.addIngresso(ingressoPrincipal);
            principal.setIngressosDisponiveis(principal.getIngressosDisponiveis() - 1);
            valorTotal += principal.getPrecoUnitarioIngresso();
            ingressosGerados.add(ingressoPrincipal);
        }
        Ingresso ingressoEvento = new Ingresso(null, usuario, evento,
                evento.getPrecoUnitarioIngresso(), usuario.getEmail());
        repositorio.salvarIngresso(ingressoEvento);
        usuario.addIngresso(ingressoEvento);
        evento.addIngresso(ingressoEvento);
        evento.setIngressosDisponiveis(evento.getIngressosDisponiveis() - 1);
        valorTotal += evento.getPrecoUnitarioIngresso();
        ingressosGerados.add(ingressoEvento);

        List<IngressoGeradoDto> dtos = ingressosGerados.stream()
                .map(IngressoMapper::toGeradoDto)
                .collect(Collectors.toList());
        return new ResultadoCompraIngressoDto(valorTotal, dtos);
    }

    public CancelarIngressoUsuarioDto cancelarIngresso(Long usuarioId, Long ingressoId) {
        Usuario usuario = repositorio.buscarUsuarioPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        Ingresso ingresso = repositorio.buscarIngressoPorId(ingressoId)
                .orElseThrow(() -> new IngressoNaoEncontradoException("Ingresso não encontrado."));
        if (ingresso.isCancelado()) throw new IngressoJaCanceladoException("Ingresso já cancelado.");
        if (!ingresso.getUsuario().getId().equals(usuarioId)) {
            throw new CancelamentoNaoPermitidoException("Ingresso não pertence ao usuário.");
        }

        Evento evento = ingresso.getEvento();
        if (!evento.isEventoEstorno()) {
            throw new CancelamentoNaoPermitidoException("Evento não permite estorno.");
        }

        double taxa = evento.getTaxaCancelamento();
        double valorEstorno = ingresso.getValorPago() * (1 - taxa / 100.0);
        ingresso.setValorEstornado(valorEstorno);
        ingresso.setStatus(Ingresso.StatusIngresso.CANCELADO);

        // Atualiza coleções
        evento.removeIngresso(ingresso);
        usuario.removeIngresso(ingresso);
        evento.setIngressosDisponiveis(evento.getIngressosDisponiveis() + 1);

        return UsuarioMapper.toCancelarDTO("Ingresso cancelado com sucesso.", ingresso);
    }

    public List<ListaIngressosUsuarioDto> listarIngressosUsuario(Long usuarioId) {
        if (repositorio.buscarUsuarioPorId(usuarioId).isEmpty()) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado.");
        }
        List<Ingresso> ingressos = repositorio.listarIngressosPorUsuario(usuarioId);
        Comparator<Ingresso> byStartDate = Comparator.comparing(i -> i.getEvento().getDataInicio());
        Comparator<Ingresso> byNomeEvento = Comparator.comparing(i -> i.getEvento().getNome());
        Comparator<Ingresso> groupComparator = (i1, i2) -> {
            boolean i1Active = !i1.isCancelado() && i1.getEvento().isEventoAtivo() && i1.getEvento().getDataInicio().isAfter(LocalDateTime.now());
            boolean i2Active = !i2.isCancelado() && i2.getEvento().isEventoAtivo() && i2.getEvento().getDataInicio().isAfter(LocalDateTime.now());
            if (i1Active && !i2Active) return -1;
            if (!i1Active && i2Active) return 1;
            return 0;
        };
        return ingressos.stream()
                .sorted(groupComparator.thenComparing(byStartDate).thenComparing(byNomeEvento))
                .map(IngressoMapper::toListaUsuarioDto)
                .collect(Collectors.toList());
    }
}