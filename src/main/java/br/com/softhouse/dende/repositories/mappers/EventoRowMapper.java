package br.com.softhouse.dende.repositories.mappers;


import br.com.dende.softhouse.repositorry.RowMapper;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;

import java.time.LocalDateTime;

public class EventoRowMapper implements RowMapper<Evento> {


    @Override
    public Evento mapRow(String[] row) {
        Evento evento = new Evento();
        evento.setId(Long.parseLong(row[0]));

        // Organizador básico
        Organizador organizador = new Organizador();
        organizador.setId(Long.parseLong(row[1]));
        evento.setOrganizador(organizador);

        evento.setNome(row[2]);
        evento.setDescricao(row[3]);
        evento.setPaginaWeb(row[4]);
        evento.setDataInicio(LocalDateTime.parse(row[5].replace(" ", "T")));
        evento.setDataFim(LocalDateTime.parse(row[6].replace(" ", "T")));
        evento.setTipoEvento(Evento.TipoEvento.valueOf(row[7]));
        evento.setModalidade(Evento.Modalidade.valueOf(row[9]));
        evento.setPrecoUnitarioIngresso(Double.parseDouble(row[10]));
        evento.setTaxaCancelamento(Double.parseDouble(row[11]));
        evento.setEventoEstorno("1".equals(row[12]) || Boolean.parseBoolean(row[12]));
        evento.setCapacidadeMaxima(Integer.parseInt(row[13]));
        evento.setLocalEvento(row[14]);
        evento.setEventoAtivo("1".equals(row[15]) || Boolean.parseBoolean(row[15]));

        if (row[8] != null && !row[8].trim().isEmpty()) {
            Evento principal = new Evento();
            principal.setId(Long.parseLong(row[8]));
            evento.setEventoPrincipal(principal);
        }
        return evento;
    }
}