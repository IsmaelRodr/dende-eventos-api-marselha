package br.com.softhouse.dende.repositories.mappers;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;
import br.com.dende.softhouse.repositorry.RowMapper;
import java.time.LocalDateTime;

public class IngressoRowMapper implements RowMapper<Ingresso> {

    public Ingresso mapRow(String[] row) {
        Ingresso ingresso = new Ingresso();

        // row[0] = id, row[1] = usuario_id, row[2] = evento_id
        ingresso.setId(Long.parseLong(row[0]));

        Usuario usuario = new Usuario();
        usuario.setId(Long.parseLong(row[1]));
        ingresso.setUsuario(usuario);

        Evento evento = new Evento();
        evento.setId(Long.parseLong(row[2]));
        ingresso.setEvento(evento);

        // row[3] = valor_pago
        ingresso.setValorPago(Double.parseDouble(row[3]));

        // row[4] = valor_estornado (faltava no mapper antigo)
        ingresso.setValorEstornado(Double.parseDouble(row[4]));

        // row[5] = data_compra
        ingresso.setDataCompra(LocalDateTime.parse(row[5].replace(" ", "T")));

        // row[6] = status
        ingresso.setStatus(Ingresso.StatusIngresso.valueOf(row[6]));

        // row[7] = email
        ingresso.setEmail(row[7]);

        return ingresso;
    }
}