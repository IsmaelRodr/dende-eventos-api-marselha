package br.com.softhouse.dende.model;

public class Empresa {
    
    // [AVALIAÇÃO - Qualidade] A classe Empresa não possui nenhuma validação dos seus atributos.
    // O CNPJ, por exemplo, possui um formato específico (14 dígitos) e poderia ser validado.
    // Sugestão: adicione um construtor ou método de validação para garantir a consistência dos dados
    // antes de persistir. Ou utilize anotações de validação como @NotBlank caso o projeto
    // utilize Bean Validation (javax.validation / jakarta.validation).
    // [AVALIAÇÃO - Item 2] O atributo 'cnpj' é do tipo String sem nenhuma formatação controlada.
    // Considere criar uma classe ou enum de valor (Value Object) que encapsula a validação do CNPJ.
    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;

    // Construtor vazio exigido pelo Jackson
    public Empresa() {
    }

    // Getters e Setters
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }

    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }
}