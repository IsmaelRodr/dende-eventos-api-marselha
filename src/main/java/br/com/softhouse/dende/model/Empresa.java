package br.com.softhouse.dende.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Empresa {

    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;

    public Empresa() {}

    public Empresa(String cnpj, String razaoSocial, String nomeFantasia) {
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
    }

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