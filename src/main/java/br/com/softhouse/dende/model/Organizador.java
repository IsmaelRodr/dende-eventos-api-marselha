package br.com.softhouse.dende.model;

import java.time.LocalDate;
import java.util.Objects;

public class Organizador {

    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    // [AVALIAÇÃO - Item 2] O atributo 'sexo' é do tipo String, o que permite qualquer valor livre.
    // Sugestão: utilize o mesmo enum Sexo { MASCULINO, FEMININO, NAO_INFORMADO } criado para Usuario.
    // A nova declaração ficaria: private Sexo sexo;
    private String sexo;
    private String email;
    private String senha;
    private boolean ativo = true;
    // [AVALIAÇÃO - Princípio OO] As classes Usuario e Organizador possuem exatamente os mesmos atributos
    // básicos (id, nome, dataNascimento, sexo, email, senha, ativo). Isso configura duplicação de código.
    // No projeto orientado a objetos, uma boa solução seria criar uma classe abstrata 'Pessoa' ou
    // 'UsuarioBase' com os atributos comuns, e Organizador e Usuario herdariam dela.
    // Exemplo: public class Organizador extends Pessoa { private Empresa empresa; }
    // A alteração de mestre: Composição usando a classe opcional Empresa
    private Empresa empresa;

    // Construtor vazio exigido pelo Jackson para receber o JSON
    public Organizador() {}

    // [AVALIAÇÃO - Item 5] Mesma observação da classe Usuario: o record 'Credenciais' está embutido
    // no modelo. O ideal seria um pacote 'dto' com classes específicas de entrada/saída.
    // [AVALIAÇÃO - Item 4] Da mesma forma que Usuario, ao serializar Organizador diretamente a senha
    // fica exposta. Um OrganizadorResponseDTO que omite senha seria o mais adequado.
    public record Credenciais(String email, String senha) {}
    
    // Getters e Setters

    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    
    public String getEmail() { return email; }
    private void setEmail(String email){this.email = email;}
    
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    // Get e Set da Empresa
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Organizador organizador = (Organizador) object;
        // Agora verifica pelo ID e E-mail, tal como o utilizador
        return Objects.equals(id, organizador.id) && 
               Objects.equals(email, organizador.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "Organizador{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", sexo='" + sexo + '\'' +
                ", email='" + email + '\'' +
                ", empresa=" + (empresa != null ? empresa.getRazaoSocial() : "Nenhuma") +
                '}';
    }
}