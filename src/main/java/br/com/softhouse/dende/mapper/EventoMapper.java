package br.com.softhouse.dende.mapper;

import br.com.softhouse.dende.dto.evento.AtualizarEventoDto;
import br.com.softhouse.dende.dto.evento.CadastrarEventoDto;
import br.com.softhouse.dende.dto.evento.EventosOrganizadorDto;
import br.com.softhouse.dende.dto.evento.FeedEventoDto;
import br.com.softhouse.dende.dto.evento.StatusEventoDto;
import br.com.softhouse.dende.model.Evento;

public class EventoMapper {

    public static Evento toModel(CadastrarEventoDto dto) {
        if (dto == null) return null;

        Evento evento = new Evento();
        preencherEvento(evento, dto.nome(), dto.descricao(), dto.paginaWeb(), dto.dataInicio(), dto.dataFim(),
                dto.tipoEvento(), dto.eventoPrincipalId(), dto.modalidade(), dto.precoUnitarioIngresso(),
                dto.taxaCancelamento(), dto.eventoEstorno(), dto.capacidadeMaxima(), dto.localEvento());
        return evento;
    }

    public static Evento toModel(AtualizarEventoDto dto) {
        if (dto == null) return null;

        Evento evento = new Evento();
        preencherEvento(evento, dto.nome(), dto.descricao(), dto.paginaWeb(), dto.dataInicio(), dto.dataFim(),
                dto.tipoEvento(), dto.eventoPrincipalId(), dto.modalidade(), dto.precoUnitarioIngresso(),
                dto.taxaCancelamento(), dto.eventoEstorno(), dto.capacidadeMaxima(), dto.localEvento());
        return evento;
    }

    public static EventosOrganizadorDto toEventosOrganizadorDto(Evento evento) {
        if (evento == null) return null;
        return new EventosOrganizadorDto(
                evento.getNome(),
                evento.getDataInicio(),
                evento.getDataFim(),
                evento.getLocalEvento(),
                evento.getPrecoUnitarioIngresso(),
                evento.getCapacidadeMaxima()
        );
    }

    public static FeedEventoDto toFeedEventoDto(Evento evento) {
        if (evento == null) return null;
        return new FeedEventoDto(
                evento.getNome(),
                evento.getDataInicio(),
                evento.getDataFim(),
                evento.getLocalEvento(),
                evento.getPrecoUnitarioIngresso(),
                evento.getCapacidadeMaxima()
        );
    }

    public static StatusEventoDto toStatusEventoDto(String mensagem, Evento evento) {
        if (evento == null) return null;
        return new StatusEventoDto(
                mensagem,
                evento.getId(),
                evento.isEventoAtivo()
        );
    }

    private static void preencherEvento(
            Evento evento,
            String nome,
            String descricao,
            String paginaWeb,
            java.time.LocalDateTime dataInicio,
            java.time.LocalDateTime dataFim,
            String tipoEvento,
            Long eventoPrincipalId,
            String modalidade,
            Double precoUnitarioIngresso,
            Double taxaCancelamento,
            boolean eventoEstorno,
            Integer capacidadeMaxima,
            String localEvento
    ) {
        evento.setNome(nome);
        evento.setDescricao(descricao);
        evento.setPaginaWeb(paginaWeb);
        evento.setDataInicio(dataInicio);
        evento.setDataFim(dataFim);
        evento.setTipoEvento(parseTipoEvento(tipoEvento));
        evento.setEventoPrincipal(toEventoPrincipal(eventoPrincipalId));
        evento.setModalidade(parseModalidade(modalidade));
        if (precoUnitarioIngresso != null) {
            evento.setPrecoUnitarioIngresso(precoUnitarioIngresso);
        }
        if (taxaCancelamento != null) {
            evento.setTaxaCancelamento(taxaCancelamento);
        }
        evento.setEventoEstorno(eventoEstorno);
        if (capacidadeMaxima != null) {
            evento.setCapacidadeMaxima(capacidadeMaxima);
        }
        evento.setLocalEvento(localEvento);
    }

    private static Evento.TipoEvento parseTipoEvento(String tipoEvento) {
        if (tipoEvento == null || tipoEvento.isBlank()) return null;
        return Evento.TipoEvento.valueOf(tipoEvento.trim().toUpperCase());
    }

    private static Evento.Modalidade parseModalidade(String modalidade) {
        if (modalidade == null || modalidade.isBlank()) return null;
        return Evento.Modalidade.valueOf(modalidade.trim().toUpperCase());
    }

    private static Evento toEventoPrincipal(Long eventoPrincipalId) {
        if (eventoPrincipalId == null) return null;
        Evento eventoPrincipal = new Evento();
        eventoPrincipal.setId(eventoPrincipalId);
        return eventoPrincipal;
    }
}
