package br.com.softhouse.dende.repositories;
//package br.com.dende.softhouse.repositorry;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {

    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();                     // Retorna todos os registros

    T update(T entity);                    // Atualiza e retorna a entidade

    void deleteById(ID id);

    void delete(T entity);

    boolean existsById(ID id);

    long count();

    <V> Optional<T> findByField(String fieldName, V value);

    // Opcionais, mas seus repositórios já têm:
    // Iterable<T> findAllById(Iterable<ID> ids);
    // void deleteAll();
}