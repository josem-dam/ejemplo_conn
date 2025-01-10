package edu.acceso.ejemplo_conn.backend;

import java.util.Optional;
import java.util.stream.Stream;

import edu.acceso.ejemplo_conn.modelo.Entity;

public interface Crud<T extends Entity> {

    public Optional<T> get(int id);
    public Stream<T> get();

    public boolean delete(int id);
    default boolean delete(T obj) {
        return delete(obj.getId());
    }

    public void create(T obj);
    default void create(Iterable<T> objs) {
        for(T obj: objs) create(obj);
    }

    public boolean update(T obj);
    public boolean update(int oldId, int newId);
    default boolean update(T obj, int newId) {
        return update(obj.getId(), newId);
    }
}
