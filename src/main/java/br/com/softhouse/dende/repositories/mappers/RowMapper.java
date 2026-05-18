package br.com.softhouse.dende.repositories.mappers;
//package br.com.dende.softhouse.repositorry;

@FunctionalInterface
public interface RowMapper<T> {
    T mapRow(String[] row);
}