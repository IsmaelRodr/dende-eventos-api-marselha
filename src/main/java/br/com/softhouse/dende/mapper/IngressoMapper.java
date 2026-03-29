package br.com.softhouse.dende.mapper;

import br.com.softhouse.dende.dto.usuario.ListaIngressosUsuarioDto;
import br.com.softhouse.dende.dto.ingresso.IngressoGeradoDto;
import br.com.softhouse.dende.model.Ingresso;

public class IngressoMapper {

    public static ListaIngressosUsuarioDto toListaUsuarioDto(Ingresso ingresso) {
        if (ingresso == null) return null;
        return new ListaIngressosUsuarioDto(
                ingresso.getUsuario().getId(),
                ingresso.getId(),
                ingresso.getEvento().getId(),
                ingresso.getEvento().getNome(),
                ingresso.getEvento().getDataInicio(),
                ingresso.getStatus().name(),
                ingresso.getValorPago(),
                ingresso.getValorEstornado(),
                ingresso.getEvento().isEventoAtivo(),
                ingresso.getDataCompra(),
                ingresso.getEmail()
        );
    }

    public static IngressoGeradoDto toGeradoDto(Ingresso ingresso) {
        if (ingresso == null) return null;
        return new IngressoGeradoDto(
                ingresso.getUsuario().getId(),
                ingresso.getId(),
                ingresso.getEvento().getId(),
                ingresso.getEvento().getNome(),
                ingresso.getValorPago()
        );
    }


}