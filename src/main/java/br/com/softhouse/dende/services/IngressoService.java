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
import br.com.softhouse.dende.repositories.EventoRepository;
import br.com.softhouse.dende.repositories.IngressoRepository;
import br.com.softhouse.dende.repositories.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class IngressoService {

    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;
    private final IngressoRepository ingressoRepository;

    public IngressoService() {
        this.usuarioRepository = new UsuarioRepository();
        this.eventoRepository = new EventoRepository();
        this.ingressoRepository = new IngressoRepository();
    }

    public ResultadoCompraIngressoDto comprarIngresso(Long usuarioId, Long eventoId, Long organizadorId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));
        if (!usuario.isAtivo()) throw new UsuarioInativoException("Usuário inativo.");

        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EventoNaoEncontradoException("Evento não encontrado."));
        if (!evento.isEventoAtivo()) throw new EventoInativoException("Evento inativo.");
        if (evento.getIngressosDisponiveis() <= 0)
            throw new EventoSemIngressosDisponiveisException("Ingressos esgotados.");
        if (evento.getDataInicio().isBefore(LocalDateTime.now()))
            throw new EventoExpiradoException("Evento já iniciado.");

        // Verifica se o evento pertence ao organizador informado
        if (!evento.getOrganizador().getId().equals(organizadorId)) {
            throw new EventoNaoEncontradoException("Evento não pertence ao organizador.");
        }

        List<Ingresso> ingressosGerados = new ArrayList<>();
        double valorTotal = 0.0;

        // Se houver evento principal, gera ingresso para ele também
        if (evento.getEventoPrincipal() != null) {
            Evento principal = eventoRepository.findById(evento.getEventoPrincipal().getId())
                    .orElseThrow(() -> new EventoNaoEncontradoException("Evento principal não encontrado."));
            if (!principal.isEventoAtivo())
                throw new EventoInativoException("Evento principal está inativo.");
            if (principal.getDataInicio().isBefore(LocalDateTime.now()))
                throw new EventoExpiradoException("Evento principal já iniciado.");

            Ingresso ingressoPrincipal = new Ingresso(null, usuario, principal,
                    principal.getPrecoUnitarioIngresso(), usuario.getEmail());
            ingressoRepository.save(ingressoPrincipal);
            principal.setIngressosDisponiveis(principal.getIngressosDisponiveis() - 1);
            eventoRepository.update(principal);
            valorTotal += principal.getPrecoUnitarioIngresso();
            ingressosGerados.add(ingressoPrincipal);
        }

        // Ingresso do evento escolhido
        Ingresso ingressoEvento = new Ingresso(null, usuario, evento,
                evento.getPrecoUnitarioIngresso(), usuario.getEmail());
        ingressoRepository.save(ingressoEvento);
        evento.setIngressosDisponiveis(evento.getIngressosDisponiveis() - 1);
        eventoRepository.update(evento);
        valorTotal += evento.getPrecoUnitarioIngresso();
        ingressosGerados.add(ingressoEvento);

        List<IngressoGeradoDto> dtos = ingressosGerados.stream()
                .map(IngressoMapper::toGeradoDto)
                .collect(Collectors.toList());

        return new ResultadoCompraIngressoDto(valorTotal, dtos);
    }

    public CancelarIngressoUsuarioDto cancelarIngresso(Long usuarioId, Long ingressoId) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        Ingresso ingresso = ingressoRepository.findById(ingressoId)
                .orElseThrow(() -> new IngressoNaoEncontradoException("Ingresso não encontrado."));
        if (ingresso.isCancelado()) throw new IngressoJaCanceladoException("Ingresso já cancelado.");
        if (!ingresso.getUsuario().getId().equals(usuarioId)) {
            throw new CancelamentoNaoPermitidoException("Ingresso não pertence ao usuário.");
        }

        Evento evento = eventoRepository.findById(ingresso.getEvento().getId())
                .orElseThrow(() -> new EventoNaoEncontradoException("Evento não encontrado."));
        if (!evento.isEventoEstorno()) {
            throw new CancelamentoNaoPermitidoException("Evento não permite estorno.");
        }

        double taxa = evento.getTaxaCancelamento();
        double valorEstorno = ingresso.getValorPago() * (1 - taxa / 100.0);
        ingresso.setValorEstornado(valorEstorno);
        ingresso.setStatus(Ingresso.StatusIngresso.CANCELADO);
        ingressoRepository.update(ingresso);

        evento.setIngressosDisponiveis(evento.getIngressosDisponiveis() + 1);
        eventoRepository.update(evento);

        return UsuarioMapper.toCancelarDTO("Ingresso cancelado com sucesso.", ingresso);
    }

    public List<ListaIngressosUsuarioDto> listarIngressosUsuario(Long usuarioId) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        List<Ingresso> ingressos = ingressoRepository.findAllByUsuarioId(usuarioId);

        // Carrega os dados completos dos eventos para ordenação (evita proxies vazios)
        for (Ingresso ingresso : ingressos) {
            Long eventoId = ingresso.getEvento().getId();
            eventoRepository.findById(eventoId)
                    .ifPresent(ingresso::setEvento);
        }

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