package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.GetMapping;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/eventos")
public class EventoController {

    private final Repositorio repositorio;

    public  EventoController(){
        this.repositorio = Repositorio.getInstance();
    }

    // [AVALIAÇÃO - Item 1] O nome do método 'feedEvento()' está no singular e mistura um substantivo
    // ("feed") com um substantivo ("evento"). Métodos devem ser verbos ou expressões verbais claras.
    // Sugestão: renomeie para 'listarFeedDeEventos()' ou 'exibirFeedEventos()'.
    // A nova assinatura ficaria: public ResponseEntity<?> listarFeedDeEventos()
    // [AVALIAÇÃO - Item 9] Quando a lista de eventos ativos está vazia, o método retorna 200 com uma
    // lista vazia, o que é aceitável. Considere documentar este comportamento explicitamente.
    // [AVALIAÇÃO - Item 4/5] O retorno usa Map<String, Object> no lugar de um DTO tipado.
    // Uma boa prática seria criar um 'EventoFeedResponseDTO' para tipagem segura em compile-time.
    // Não era obrigatório nesta avaliação, mas é recomendado.
    @GetMapping
    public ResponseEntity<?> feedEvento(){
        // [AVALIAÇÃO - Bug herdado do Repositório] O método 'listarEventoAtivos()' no Repositório
        // filtra por 'capacidadeMaxima > 0' em vez de 'ingressosDisponiveis > 0'. Isso faz com que
        // eventos com ingressos esgotados ainda apareçam neste feed, contrariando a US 12.
        // Esse filtro de dataFim.isAfter(agora) aqui no controller está correto para excluir eventos
        // já encerrados, mas o filtro de ingressos disponíveis precisa ser corrigido no Repositório.
        List<Map<String, Object>> eventosFiltrados = repositorio.listarEventoAtivos().stream()
                .filter(e -> e.getDataFim().isAfter(LocalDateTime.now()))
                .sorted(Comparator
                        .comparing(Evento::getDataInicio)
                        .thenComparing(Evento::getNome))
                .map(e -> { Map<String, Object> eventoMap = new HashMap<>();
                    eventoMap.put("nome", e.getNome());
                    eventoMap.put("dataInicio", e.getDataInicio());
                    eventoMap.put("dataFim", e.getDataFim());
                    eventoMap.put("local", e.getLocalEvento());
                    eventoMap.put("precoIngresso", e.getPrecoUnitarioIngresso());
                    eventoMap.put("capacidadeMaxima", e.getCapacidadeMaxima());
                    return eventoMap; })
                .toList();

        return ResponseEntity.status(200, eventosFiltrados);
    }
}
