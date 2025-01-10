package edu.acceso.ejemplo_conn.backend.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Stream;

import javax.sql.DataSource;

import edu.acceso.ejemplo_conn.backend.Crud;
import edu.acceso.ejemplo_conn.modelo.Centro;
import edu.acceso.sqlutils.DataAccessException;

public class CentroSqlite implements Crud<Centro> {

    private DataSource ds;

    public CentroSqlite(DataSource ds) {
        this.ds = ds;        
    }

    private Centro resultToCentro(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_centro");
        String nombre = rs.getString("nombre");
        String titularidad = rs.getString("titularidad");
        return new Centro(id, nombre, titularidad);
    }

    private void setCentroParams(Centro centro, PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, centro.getNombre());
        pstmt.setString(2, centro.getTitularidad());
        pstmt.setString(3, centro.getTitularidad()); // TODO: Esto en realidad es un JSON.
        pstmt.setInt(4, centro.getId());
    }

    @Override
    public Stream<Centro> get() {
        return null;
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
    public void create(Centro centro) {
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
