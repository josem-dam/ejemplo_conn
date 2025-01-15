package edu.acceso.ejemplo_conn.backend.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.stream.Stream;

import javax.sql.DataSource;

import edu.acceso.ejemplo_conn.modelo.Centro;
import edu.acceso.sqlutils.Crud;
import edu.acceso.sqlutils.DataAccessException;
import edu.acceso.sqlutils.SqlUtils;
import edu.acceso.sqlutils.TransactionManager;

/**
 * Modela para un Centro las operaciones de acceso a una base de datos SQLite.
 */
public class CentroSqlite implements Crud<Centro> {

    /**
     * Fuente de datos a la que hacer conexiones.
     */
    private DataSource ds;

    /**
     * Constructor de la clase.
     * @param ds Fuente de datos.
     */
    public CentroSqlite(DataSource ds) {
        this.ds = ds;        
    }

    /**
     * Recupera los datos de un registro de la tabla para convertirlos en objeto Centro.
     * @param rs El ResultSet que contiene el registro.
     * @return Un objeto Centro que modela los datos del registro.
     * @throws SQLException Cuando se produce un error al recuperar los datos del registro.
     */
    private static Centro resultToCentro(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_centro");
        String nombre = rs.getString("nombre");
        String titularidad = rs.getString("titularidad");
        return new Centro(id, nombre, titularidad);
    }

    /**
     * Fija los valores de los campos de un registro para una sentencia parametrizada.
     * @param centro El objeto Centro.
     * @param pstmt La sentencia parametrizada.
     * @throws SQLException Cuando se produce un error al establecer valor para los parámentros de la consulta.
     */
    private static void setCentroParams(Centro centro, PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, centro.getNombre());
        pstmt.setString(2, centro.getTitularidad());
        pstmt.setString(3, centro.getTitularidad()); // TODO: Esto en realidad es un JSON.
        pstmt.setInt(4, centro.getId());
    }

    @Override
    public Stream<Centro> get() {
        final String sqlString = "SELECT * FROM Centro";
        
        try {
            // No pueden cerrarse ahora, sino cuando se agote el Stream
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlString);

            return SqlUtils.resultSetToStream(conn, rs, CentroSqlite::resultToCentro);
        }
        catch(SQLException err) {
            throw new DataAccessException(err);
        }
    }

    @Override
    public Optional<Centro> get(int id) {
        final String sqlString = "SELECT * FROM Centro WHERE id_centro = ?";

        try(
            Connection conn = ds.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
        ) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next()?Optional.of(resultToCentro(rs)):Optional.empty();
        }
        catch(SQLException err) {
            throw new DataAccessException(err);
        }
    }

    @Override
    public void insert(Centro centro) {
        final String sqlString = "INSERT INTO Centro (nombre, titularidad, direccion, id_centro) VALUES (?, ?, ?, ?, ?)";
        
        try(
            Connection conn = ds.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
        ) {
            setCentroParams(centro, pstmt);
            pstmt.executeUpdate();
        }
        catch(SQLException err) {
            throw new DataAccessException(err);
        }
    }

    @Override
    public void insert(Iterable<Centro> centros) {
        final String sqlString = "INSERT INTO Centro (nombre, titularidad, direccion, id_centro) VALUES (?, ?, ?, ?, ?)";

        try(
            TransactionManager tm = new TransactionManager(ds.getConnection());
            PreparedStatement pstmt = tm.getConn().prepareStatement(sqlString);
        ) {
            for(Centro centro: centros) {
                setCentroParams(centro, pstmt);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            tm.commit();
        }
        catch(SQLException err) {
            throw new DataAccessException(err);
        }
    }

    @Override
    public boolean delete(int id) {
        final String sqlString = "DELETE FROM Centro WHERE id_centro = ?";

        try (
            Connection conn = ds.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
        ) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
        catch(SQLException err) {
            throw new DataAccessException(err);
        }
    }

    @Override
    public boolean update(Centro centro) {
        final String sqlString = "UPDATE Centro SET nombre = ?, titularidad = ?, direccion = ? WHERE id_centro = ?";
        
        try(
            Connection conn = ds.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
        ) {
            setCentroParams(centro, pstmt);
            return pstmt.executeUpdate() > 0;
        }
        catch(SQLException err) {
            throw new DataAccessException(err);
        }
    }

    @Override
    public boolean update(int oldId, int newId) {
        final String sqlString = "UPDATE Centro SET id_centro = ? WHERE id_centro = ?";
        
        try(
            Connection conn = ds.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
        ) {
            pstmt.setInt(1, newId);
            pstmt.setInt(2, oldId);
            return pstmt.executeUpdate() > 0;
        }
        catch(SQLException err) {
            throw new DataAccessException(err);
        }
    }
}