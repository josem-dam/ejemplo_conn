package edu.acceso.ejemplo_conn;

import java.util.Map;
import java.util.stream.Stream;

import edu.acceso.ejemplo_conn.backend.BackendFactory;
import edu.acceso.ejemplo_conn.backend.Conexion;
import edu.acceso.ejemplo_conn.modelo.Centro;
import edu.acceso.sqlutils.Crud;

public class Main {
    public static void main(String[] args) {
        // Estos datos debería obtenerse de alguna manera.
        Map<String, Object> opciones = Map.of(
            "base", "sqlite",
            "url",  "/tmp/caca.db",
            "user", "",
            "password", ""
        );

        Conexion conexion = BackendFactory.crearConexion(opciones);
        Crud<Centro> centroDao = conexion.getCentroDao();

        try(Stream<Centro> centros = centroDao.get()) {
            centros.forEach(Centro::toString);
        }
    }
}