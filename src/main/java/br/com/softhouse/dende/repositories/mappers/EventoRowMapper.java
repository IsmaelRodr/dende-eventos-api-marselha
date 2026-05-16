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
        
        // O evento precisa saber quem é o organizador dele. Por enquanto, setamos apenas o ID.
        // O Repositório vai preencher o resto se for necessário.
        Organizador organizador = new Organizador();
        organizador.setId(Long.parseLong(row[1]));
        evento.setOrganizador(organizador);
        
        evento.setNome(row[2]);
        evento.setDescricao(row[3]);
        evento.setPaginaWeb(row[4]);
        
        // Datas vêm do banco com hora (DATETIME) e o Java usa o LocalDateTime. As vezes o banco manda com espaço, substituímos por T.
        evento.setDataInicio(LocalDateTime.parse(row[5].replace(" ", "T")));
        evento.setDataFim(LocalDateTime.parse(row[6].replace(" ", "T")));
        
        evento.setTipoEvento(Evento.TipoEvento.valueOf(row[7])); // Converte String para o Enum
        evento.setModalidade(Evento.Modalidade.valueOf(row[8])); // Converte String para o Enum
       evento.setPrecoUnitarioIngresso(Double.parseDouble(row[9])); // Ajustado para o nome correto do seu model
        evento.setTaxaCancelamento(Double.parseDouble(row[10]));
        evento.setEventoEstorno(Boolean.parseBoolean(row[11]) || "1".equals(row[11]));
        evento.setCapacidadeMaxima(Integer.parseInt(row[12]));
        evento.setLocalEvento(row[13]);
        evento.setEventoAtivo(Boolean.parseBoolean(row[14]) || "1".equals(row[14]));

        return evento;
    }
}