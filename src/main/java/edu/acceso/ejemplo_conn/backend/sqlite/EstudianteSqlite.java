package edu.acceso.ejemplo_conn.backend.sqlite;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import edu.acceso.sqlutils.SqlUtils;
import edu.acceso.sqlutils.TransactionManager;

/**
 * Modela para un Estudiante las operaciones de acceso a una base de datos SQLite.
 */
public class EstudianteSqlite implements Crud<Estudiante> {

    /**
     * Fuente de datos a la que hacer conexiones.
     */
    private DataSource ds;

    /**
     * Constructor de la clase.
     * @param ds Fuente de datos.
     */
    public EstudianteSqlite(DataSource ds) {
        this.ds = ds;        
    }

    /**
     * Recupera los datos de un registro de la tabla para convertirlos en objeto Estudiante.
     * La obtención del centro al que está adscrito el alumno es perezosa: se apunta el identificador
     * y no se obtiene el centro en sí hasta que no se use el getter correspondiente.
     * 
     * @param rs El ResultSet que contiene el registro.
     * @return Un objeto Estudiante que modela los datos del registro.
     * @throws SQLException Cuando se produce un error al recuperar los datos del registro.
     */
    private static Estudiante resultToEstudiante(ResultSet rs, DataSource ds) throws SQLException {
        int id = rs.getInt("id_estudiante");
        String nombre = rs.getString("nombre");
        Integer idCentro = rs.getInt("centro");
        if(rs.wasNull()) idCentro = null;
        LocalDate nacimiento = rs.getDate("nacimiento").toLocalDate();

        Estudiante estudiante = new Estudiante();
        Centro centro = null;

        // Carga inmediata: obtenemos inmediatamente el centro.
        //if(IdCentro != null) centro = new CentroSqlite(ds).get(IdCentro).orElse(null);

        // Carga perezosa: proxy al que se le carga la clave foránea
        FkLazyLoader<Estudiante> loader = new FkLazyLoader<>(estudiante);
        loader.setFk("centro", idCentro, new CentroSqlite(ds));
        estudiante = loader.createProxy();

        // Cargamos datos en el objeto y entregamos.
        return estudiante.cargarDatos(id, nombre, nacimiento, centro);
    }

    /**
     * Fija los valores de los campos de un registro para una sentencia parametrizada.
     * @param centro El objeto Centro.
     * @param pstmt La sentencia parametrizada.
     * @throws SQLException Cuando se produce un error al establecer valor para los parámentros de la consulta.
     */
    private static void setEstudianteParams(Estudiante estudiante, PreparedStatement pstmt) throws SQLException {
        Centro centro = estudiante.getCentro();

        pstmt.setString(1, estudiante.getNombre());
        pstmt.setDate(2, Date.valueOf(estudiante.getNacimiento()));
        pstmt.setObject(3, centro == null?centro:centro.getId(), Types.INTEGER);
        pstmt.setInt(4, estudiante.getId());
    }

    @Override
    public Stream<Estudiante> get() {
        final String sqlString = "SELECT * FROM Estudiante";
        
        try {
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlString);

            return SqlUtils.resultSetToStream(conn, rs, fila -> resultToEstudiante(fila, ds));
        }
        catch(SQLException err) {
            throw new DataAccessException(err);
        }
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
            return rs.next()?Optional.of(resultToEstudiante(rs, ds)):Optional.empty();
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
        final String sqlString = "INSERT INTO Estudiante (nombre, nacimiento, centro, id_estudiante) VALUES (?, ?, ?, ?)";

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
