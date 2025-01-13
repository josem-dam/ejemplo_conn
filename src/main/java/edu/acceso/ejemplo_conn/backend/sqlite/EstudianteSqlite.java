package edu.acceso.ejemplo_conn.backend.sqlite;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import javax.sql.DataSource;

import edu.acceso.ejemplo_conn.modelo.Centro;
import edu.acceso.ejemplo_conn.modelo.Estudiante;
import edu.acceso.sqlutils.Crud;
import edu.acceso.sqlutils.DataAccessException;
import edu.acceso.sqlutils.FkLazyLoader;
import edu.acceso.sqlutils.TransactionManager;

public class EstudianteSqlite implements Crud<Estudiante> {

    private DataSource ds;

    public EstudianteSqlite(DataSource ds) {
        this.ds = ds;        
    }

    private Estudiante resultToEstudiante(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_estudiante");
        String nombre = rs.getString("nombre");
        Integer centro = rs.getInt("centro");
        if(rs.wasNull()) centro = null;
        LocalDate nacimiento = rs.getDate("nacimiento").toLocalDate();

        // Carga perezosa: no se obtiene el centro ahora, sino cuando se pida mediante getCentro.
        Estudiante estudiante = new Estudiante(id, nombre, nacimiento, null);
        FkLazyLoader<Estudiante> proxy = new FkLazyLoader<>(estudiante);
        proxy.setFk("centro", centro);
        return proxy.createProxy(new CentroSqlite(ds));
    }

    private void setEstudianteParams(Estudiante estudiante, PreparedStatement pstmt) throws SQLException {
        Centro centro = estudiante.getCentro();

        pstmt.setString(1, estudiante.getNombre());
        pstmt.setDate(2, Date.valueOf(estudiante.getNacimiento()));
        pstmt.setObject(3, centro == null?centro:centro.getId(), Types.INTEGER);
        pstmt.setInt(4, estudiante.getId());
    }

    @Override
    public Stream<Estudiante> get() {
        return null;
    }

    @Override
    public Optional<Estudiante> get(int id) {
        final String sqlString = "SELECT * FROM Estudiante WHERE id_estudiante = ?";

        try(
            Connection conn = ds.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
        ) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next()?Optional.of(resultToEstudiante(rs)):Optional.empty();
        }
        catch(SQLException err) {
            throw new DataAccessException(err);
        }
    }

    @Override
    public void insert(Estudiante centro) {
        final String sqlString = "INSERT INTO Estudiante (nombre, nacimiento, centro, id_estudiante) VALUES (?, ?, ?, ?, ?)";
        
        try(
            Connection conn = ds.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
        ) {
            setEstudianteParams(centro, pstmt);
            pstmt.executeUpdate();
        }
        catch(SQLException err) {
            throw new DataAccessException(err);
        }
    }

    @Override
    public void insert(Iterable<Estudiante> estudiantes) {
        final String sqlString = "INSERT INTO Estudiante (nombre, nacimiento, centro, id_estudiante) VALUES (?, ?, ?, ?, ?)";

        try(
            TransactionManager tm = new TransactionManager(ds.getConnection());
            PreparedStatement pstmt = tm.getConn().prepareStatement(sqlString);
        ) {
            for(Estudiante estudiante: estudiantes) {
                setEstudianteParams(estudiante, pstmt);
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
        final String sqlString = "DELETE FROM Estudiante WHERE id_estudiante = ?";

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
    public boolean update(Estudiante estudiante) {
        final String sqlString = "UPDATE Estudiante SET nombre = ?, nacimiento = ?, centro = ? WHERE id_estudiante = ?";
        
        try(
            Connection conn = ds.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
        ) {
            setEstudianteParams(estudiante, pstmt);
            return pstmt.executeUpdate() > 0;
        }
        catch(SQLException err) {
            throw new DataAccessException(err);
        }
    }

    @Override
    public boolean update(int oldId, int newId) {
        final String sqlString = "UPDATE Estudiante SET id_estudiante = ? WHERE id_estudiante = ?";
        
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