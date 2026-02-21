package br.com.softhouse.dende.service;

import br.com.softhouse.dende.model.*;
import br.com.softhouse.dende.repositories.Repositorio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class IngressoService {

    private final Repositorio repositorio;

    public IngressoService() {
        this.repositorio = Repositorio.getInstance();
    }

    private Long gerarId() {
        return System.nanoTime();
    }

    //Comprar ingresso
    public List<Ingresso> comprarIngresso(Usuario usuario, Evento evento) {

        if (!usuario.isAtivo())
            throw new IllegalStateException("Usuário inativo.");

        if (!evento.isEventoAtivo())
            throw new IllegalStateException("Evento inativo.");

        if (evento.getIngressosDisponiveis() <= 0)
            throw new IllegalStateException("Evento esgotado.");

        List<Ingresso> ingressos = new ArrayList<>();
        double valorTotal = evento.getPrecoUnitarioIngresso();

        // Evento vinculado
        if (evento.getEventoPrincipal() != null) {

            Evento principal = evento.getEventoPrincipal();
            valorTotal += principal.getPrecoUnitarioIngresso();

            Ingresso ingressoPrincipal =
                    new Ingresso(gerarId(), usuario, principal,
                            principal.getPrecoUnitarioIngresso());

            repositorio.salvarIngresso(usuario.getId(), ingressoPrincipal);
            ingressos.add(ingressoPrincipal);
        }

        Ingresso ingresso =
                new Ingresso(gerarId(), usuario, evento, valorTotal);

        repositorio.salvarIngresso(usuario.getId(), ingresso);
        ingressos.add(ingresso);

        evento.setIngressosDisponiveis(
                evento.getIngressosDisponiveis() - 1
        );

        return ingressos;
    }

    //Cancelar ingresso
    public void cancelarIngresso(Ingresso ingresso) {

        if (ingresso.isCancelado())
            throw new IllegalStateException("Ingresso já cancelado.");

        Evento evento = ingresso.getEvento();

        ingresso.setStatus(Ingresso.StatusIngresso.CANCELADO);

        if (evento.isEventoEstorno()) {
            double valorEstorno =
                    ingresso.getValorPago() - evento.getTaxaCancelamento();

            System.out.println("Valor estornado: " + valorEstorno);
        }

        evento.setIngressosDisponiveis(
                evento.getIngressosDisponiveis() + 1
        );
    }

    //Listar ingressos do usuário
    public List<Ingresso> listarIngressosUsuario(Long usuarioId) {

        return repositorio.listarIngressosUsuario(usuarioId)
                .stream()
                .sorted(Comparator
                        .comparing((Ingresso i) -> i.getEvento().getDataInicio())
                        .thenComparing(i -> i.getEvento().getNome()))
                .toList();
    }

    //Cancelamento dos ingresso por suspensão de evento
    public void cancelarIngressosDoEvento(Evento evento) {

        List<Ingresso> ingressos =
                repositorio.buscarIngressosPorEvento(evento.getId());

        for (Ingresso i : ingressos) {
            if (!i.isCancelado()) {
                cancelarIngresso(i);
            }
        }
    }
}