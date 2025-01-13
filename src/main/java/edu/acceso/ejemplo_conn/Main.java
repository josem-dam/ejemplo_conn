package edu.acceso.ejemplo_conn;

import java.util.Map;

import edu.acceso.ejemplo_conn.backend.BackendFactory;
import edu.acceso.ejemplo_conn.backend.Conexion;

public class Main {
    public static void main(String[] args) {
        // Estos datos debería obtenerse de alguna manera.
        Map<String, Object> opciones = Map.of(
            "base", "sqlite",
            "url",  ":memory:",
            "user", null,
            "password", null
        );

        Conexion conexion = BackendFactory.crearConexion(opciones);
    }
}