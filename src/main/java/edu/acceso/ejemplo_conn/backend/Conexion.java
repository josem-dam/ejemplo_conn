package edu.acceso.ejemplo_conn.backend;

import edu.acceso.ejemplo_conn.modelo.Centro;
import edu.acceso.ejemplo_conn.modelo.Estudiante;
import edu.acceso.sqlutils.dao.Crud;
import edu.acceso.sqlutils.errors.DataAccessException;

public interface Conexion {

    @FunctionalInterface
    public interface Transaccionable {
        void run(Crud<Centro> centroDao, Crud<Estudiante> estudianteDao) throws DataAccessException;
    }

    public Crud<Centro> getCentroDao();
    public Crud<Estudiante> getEstudianteDao();
    public void transaccion(Transaccionable operaciones) throws DataAccessException;
}
