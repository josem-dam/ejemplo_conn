package edu.acceso.ejemplo_conn;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Estos datos debería obtenerse de alguna manera.

        Map<String, Object> opciones = Map.of(
            "base", "sqlite",
            "path",  "memory",
            "user", null,
            "password", null
        );
    }
}