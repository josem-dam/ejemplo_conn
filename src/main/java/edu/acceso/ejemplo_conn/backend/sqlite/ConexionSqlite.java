package edu.acceso.ejemplo_conn.backend.sqlite;

import java.util.Map;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import edu.acceso.ejemplo_conn.backend.Conexion;
import edu.acceso.ejemplo_conn.modelo.Centro;
import edu.acceso.ejemplo_conn.modelo.Estudiante;
import edu.acceso.sqlutils.Crud;

/**
 * Modela la conexión a una base de dato SQLite
 */
public class ConexionSqlite implements Conexion {
    final static String protocol = "jdbc:sqlite:";
    final static short maxConn = 10;
    final static short minConn = 1;

    private final HikariDataSource ds;

    /**
     * Constructor de la conexión.
     * @param opciones Las opciones de conexión.
     */
    public ConexionSqlite(Map<String, Object> opciones) {
        String path = (String) opciones.get("url");
        if(path == null) throw new IllegalArgumentException("No se ha fijado la url de la base de datos");

        String dbUrl = String.format("%s%s", protocol, path);
        Short maxConn = (Short) opciones.getOrDefault("maxconn", ConexionSqlite.maxConn);
        Short minConn = (Short) opciones.getOrDefault("minconn", ConexionSqlite.minConn);


        HikariConfig hconfig = new HikariConfig();
        hconfig.setJdbcUrl(dbUrl);
        hconfig.setMaximumPoolSize(maxConn);
        hconfig.setMinimumIdle(minConn);

        ds = new HikariDataSource(hconfig);

        initDB();
    }

    @Override
    public Crud<Centro> getCentroDao() {
        return new CentroSqlite(ds);
    }

    @Override
    public Crud<Estudiante> getEstudianteDao() {
        return new EstudianteSqlite(ds);
    }

    private void initDB() {
        // TODO: Inicializar la base de datos en caso de que no exista a partir del esquema.
    }
}