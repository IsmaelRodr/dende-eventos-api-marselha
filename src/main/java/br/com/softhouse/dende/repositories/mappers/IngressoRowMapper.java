package br.com.softhouse.dende.repositories.mappers;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;
import br.com.dende.softhouse.repositorry.RowMapper; // Import corrigido
import java.time.LocalDateTime;

public class IngressoRowMapper implements RowMapper<Ingresso> {

    @Override
    public Ingresso mapRow(String[] row) {
        Ingresso ingresso = new Ingresso();

        // 1. ID do Ingresso
        ingresso.setId(Long.parseLong(row[0]));

        // 2. Monta a referência do Usuário que comprou
        Usuario usuario = new Usuario();
        usuario.setId(Long.parseLong(row[1]));
        ingresso.setUsuario(usuario);

        // 3. Monta a referência do Evento
        Evento evento = new Evento();
        evento.setId(Long.parseLong(row[2]));
        ingresso.setEvento(evento);

        // 4. Dados da compra
        ingresso.setValorPago(Double.parseDouble(row[3]));
        ingresso.setDataCompra(LocalDateTime.parse(row[4].replace(" ", "T")));

        // 5. Converte o texto do banco para o Enum de Status do Ingresso
        ingresso.setStatus(Ingresso.StatusIngresso.valueOf(row[5]));

        // 6. QR Code
        ingresso.setQrCode(row[6]);

        return ingresso;
    }
}