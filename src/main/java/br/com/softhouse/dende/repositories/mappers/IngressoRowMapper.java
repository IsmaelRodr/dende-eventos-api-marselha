package br.com.softhouse.dende.repositories.mappers;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;
import br.com.dende.softhouse.repositorry.RowMapper;
import java.time.LocalDateTime;

public class IngressoRowMapper implements RowMapper<Ingresso> {

    public Ingresso mapRow(String[] row) {
        Ingresso ingresso = new Ingresso();


        ingresso.setId(Long.parseLong(row[0]));
        Usuario usuario = new Usuario();
        usuario.setId(Long.parseLong(row[1]));
        ingresso.setUsuario(usuario);
        Evento evento = new Evento();
        evento.setId(Long.parseLong(row[2]));
        ingresso.setEvento(evento);
        ingresso.setValorPago(Double.parseDouble(row[3]));
        ingresso.setValorEstornado(Double.parseDouble(row[4]));
        ingresso.setDataCompra(LocalDateTime.parse(row[5].replace(" ", "T")));
        ingresso.setStatus(Ingresso.StatusIngresso.valueOf(row[6]));
        ingresso.setEmail(row[7]);

        return ingresso;
    }
}