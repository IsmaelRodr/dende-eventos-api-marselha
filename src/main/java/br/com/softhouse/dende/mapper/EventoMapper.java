package br.com.softhouse.dende.mapper;

import br.com.softhouse.dende.dto.evento.*;
import br.com.softhouse.dende.model.Evento;

public class EventoMapper {

    public static Evento toModel(CadastrarEventoDto dto) {
        if (dto == null) return null;

        return Evento.builder()
                .nome(dto.nome())
                .descricao(dto.descricao())
                .paginaWeb(dto.paginaWeb())
                .dataInicio(dto.dataInicio())
                .dataFim(dto.dataFim())
                .tipoEvento(parseTipoEvento(dto.tipoEvento()))
                .modalidade(parseModalidade(dto.modalidade()))
                .precoUnitarioIngresso(
                        dto.precoUnitarioIngresso() != null
                                ? dto.precoUnitarioIngresso()
                                : 0.0
                )
                .taxaCancelamento(
                        dto.taxaCancelamento() != null
                                ? dto.taxaCancelamento()
                                : 0.0
                )
                .eventoEstorno(dto.eventoEstorno())
                .capacidadeMaxima(
                        dto.capacidadeMaxima() != null
                                ? dto.capacidadeMaxima()
                                : 0
                )
                .localEvento(dto.localEvento())
                // eventoPrincipal é gerido no controller
                // eventoAtivo começa false
                .build();
    }

    public static Evento toModel(AtualizarEventoDto dto) {
        if (dto == null) return null;

        return Evento.builder()
                .nome(dto.nome())
                .descricao(dto.descricao())
                .paginaWeb(dto.paginaWeb())
                .dataInicio(dto.dataInicio())
                .dataFim(dto.dataFim())
                .tipoEvento(parseTipoEvento(dto.tipoEvento()))
                .modalidade(parseModalidade(dto.modalidade()))
                .precoUnitarioIngresso(
                        dto.precoUnitarioIngresso() != null
                                ? dto.precoUnitarioIngresso()
                                : 0.0
                )
                .taxaCancelamento(
                        dto.taxaCancelamento() != null
                                ? dto.taxaCancelamento()
                                : 0.0
                )
                .eventoEstorno(dto.eventoEstorno())
                .capacidadeMaxima(
                        dto.capacidadeMaxima() != null
                                ? dto.capacidadeMaxima()
                                : 0
                )
                .localEvento(dto.localEvento())
                .build();
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
                evento.getCapacidadeMaxima(),
                evento.getTipoEvento() != null ? evento.getTipoEvento().name() : null,
                evento.getModalidade() != null ? evento.getModalidade().name() : null
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

    private static Evento.TipoEvento parseTipoEvento(String tipoEvento) {
        if (tipoEvento == null || tipoEvento.isBlank()) return null;
        return Evento.TipoEvento.valueOf(tipoEvento.trim().toUpperCase());
    }

    private static Evento.Modalidade parseModalidade(String modalidade) {
        if (modalidade == null || modalidade.isBlank()) return null;
        return Evento.Modalidade.valueOf(modalidade.trim().toUpperCase());
    }
}