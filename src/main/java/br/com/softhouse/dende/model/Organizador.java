package br.com.softhouse.dende.model;

import br.com.softhouse.dende.exceptions.DadosInvalidosException;
import br.com.softhouse.dende.exceptions.evento.EventoNaoEncontradoException;
import br.com.softhouse.dende.exceptions.organizador.OrganizadorComEventosAtivosException;
import br.com.softhouse.dende.exceptions.organizador.OrganizadorJaInativoException;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class Organizador {

    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private String sexo;
    private String email;
    private String senha;
    private boolean ativo = true;
    private Empresa empresa;
    private final List<Evento> eventos = new ArrayList<>();

    public Organizador() {}

    public Organizador(Long id, String nome, LocalDate dataNascimento, String sexo,
                       String email, String senha, Empresa empresa) {
        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.email = email;
        this.senha = senha;
        this.empresa = empresa;
    }

    public void desativar(){
        if (!this.isAtivo()) {
            throw new OrganizadorJaInativoException("Organizador já está inativo.");
        }
        boolean temEventosAtivos = eventos.stream().anyMatch(Evento::isEventoAtivo);
        if (temEventosAtivos) {
            throw new OrganizadorComEventosAtivosException("Organizador possui eventos ativos.");
        }
        this.ativo = false;
    }

    public List<Evento> getEventos() {
        return List.copyOf(eventos);
    }

    public void setEventos(List<Evento> eventos) {
        this.eventos.clear();
        if (eventos != null) {
            this.eventos.addAll(eventos);
        }
    }


    /*
    private Evento buscarEvento(Long id){
        return eventos.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new EventoNaoEncontradoException(
                                "Evento não encontrado."
                        )
                );
    } retirado apos a analise de custo
    */


    public void adicionarEvento(Evento evento) {
        if (evento == null){
            throw new DadosInvalidosException("O evento não pode ser nulo.");
        }

        if (!eventos.contains(evento)){
            eventos.add(evento);
            evento.setOrganizador(this);
        }
    }

    /*public void ativarEvento(Long id){
        buscarEvento(id).ativar(); retirado após a analise de custo.
    }*/

    /*
    public void desativarEvento(long id){
        buscarEvento(id).desativar(); retirado após a analise de custo.
    }*/

    /*
    public void removeEvento(Evento evento) {
        remover uma referência do organizador.
    }*/

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Organizador that = (Organizador) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Organizador{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", ativo=" + ativo +
                ", empresa=" + (empresa != null ? empresa.getRazaoSocial() : "Nenhuma") +
                '}';
    }
}