package edu.acceso.ejemplo_conn.backend.sqlite;

import java.util.Map;

import com.zaxxer.hikari.HikariDataSource;

import edu.acceso.ejemplo_conn.backend.Conexion;
import edu.acceso.ejemplo_conn.modelo.Centro;
import edu.acceso.ejemplo_conn.modelo.Estudiante;
import edu.acceso.sqlutils.Crud;

public class ConexionSqlite implements Conexion {
    final static String protocol = "jdbc:sqlite:";

    private HikariDataSource ds;

    public ConexionSqlite(Map<String, Object> opciones) {

    }

    @Override
    public Crud<Centro> getCentroDao() {
        throw new UnsupportedOperationException("Método no soportado");
    }

    @Override
    public Crud<Estudiante> getEstudianteDao() {
        throw new UnsupportedOperationException("Método no soportado");
    }
}
