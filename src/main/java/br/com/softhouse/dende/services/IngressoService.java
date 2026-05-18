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
        if (evento.getDataInicio().isBefore(LocalDateTime.now()))
            throw new EventoExpiradoException("Evento já iniciado.");
        if (!evento.getOrganizador().getId().equals(organizadorId)) {
            throw new EventoNaoEncontradoException("Evento não pertence ao organizador.");
        }

        List<Ingresso> ingressosGerados = new ArrayList<>();
        double valorTotal = 0.0;

        if (evento.getEventoPrincipal() != null) {
            Evento principal = eventoRepository.findById(evento.getEventoPrincipal().getId())
                    .orElseThrow(() -> new EventoNaoEncontradoException("Evento principal não encontrado."));
            if (!principal.isEventoAtivo())
                throw new EventoInativoException("Evento principal está inativo.");
            if (principal.getDataInicio().isBefore(LocalDateTime.now()))
                throw new EventoExpiradoException("Evento principal já iniciado.");

            Ingresso ingressoPrincipal = new Ingresso(null, usuario, principal,
                    principal.getPrecoUnitarioIngresso(), usuario.getEmail());

            principal.adicionarIngresso(ingressoPrincipal);
            usuario.adicionarIngresso(ingressoPrincipal);
            ingressoRepository.save(ingressoPrincipal);
            eventoRepository.update(principal);
            valorTotal += principal.getPrecoUnitarioIngresso();
            ingressosGerados.add(ingressoPrincipal);
        }

        // Ingresso do evento escolhido
        Ingresso ingressoEvento = new Ingresso(null, usuario, evento,
                evento.getPrecoUnitarioIngresso(), usuario.getEmail());
        evento.adicionarIngresso(ingressoEvento);
        usuario.adicionarIngresso(ingressoEvento);
        ingressoRepository.save(ingressoEvento);
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
        if (!ingresso.getUsuario().getId().equals(usuarioId)) {
            throw new CancelamentoNaoPermitidoException("Ingresso não pertence ao usuário.");
        }
        Evento evento = eventoRepository.findById(ingresso.getEvento().getId())
                .orElseThrow(() -> new EventoNaoEncontradoException("Evento não encontrado."));
        evento.cancelarIngressoIndividual(ingresso);
        ingressoRepository.update(ingresso);
        eventoRepository.update(evento);
        return UsuarioMapper.toCancelarDTO("Ingresso cancelado com sucesso.", ingresso);
    }

    public List<ListaIngressosUsuarioDto> listarIngressosUsuario(Long usuarioId) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        List<Ingresso> ingressos = ingressoRepository.findAllByUsuarioIdWithEvento(usuarioId);

        Comparator<Ingresso> groupComparator = Comparator.comparing(Ingresso::isAtivoParaListagem).reversed();
        Comparator<Ingresso> byStartDate = Comparator.comparing(i -> i.getEvento().getDataInicio());
        Comparator<Ingresso> byNomeEvento = Comparator.comparing(i -> i.getEvento().getNome());

        return ingressos.stream()
                .sorted(groupComparator.thenComparing(byStartDate).thenComparing(byNomeEvento))
                .map(IngressoMapper::toListaUsuarioDto)
                .collect(Collectors.toList());
    }
}