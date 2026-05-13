package br.com.softhouse.dende.model;

public class Empresa {
    
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Empresa empresa = (Empresa) obj;

        return cnpj != null && cnpj.equals(empresa.cnpj);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(cnpj);
    }

    @Override
    public String toString() {
        return "Empresa{" +
                "cnpj='" + cnpj + '\'' +
                ", razaoSocial='" + razaoSocial + '\'' +
                ", nomeFantasia='" + nomeFantasia + '\'' +
                '}';
    }
}