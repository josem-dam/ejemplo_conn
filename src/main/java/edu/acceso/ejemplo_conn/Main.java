package edu.acceso.ejemplo_conn;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import edu.acceso.ejemplo_conn.backend.BackendFactory;
import edu.acceso.ejemplo_conn.backend.Conexion;
import edu.acceso.ejemplo_conn.modelo.Centro;
import edu.acceso.ejemplo_conn.modelo.Estudiante;
import edu.acceso.sqlutils.Crud;

public class Main {

    static DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        // Estos datos debería obtenerse de alguna manera.
        Path path = Path.of(System.getProperty("java.io.tmpdir"), "caca.db");

        Map<String, Object> opciones = Map.of(
            "base", "sqlite",
//            "url",  path.toString(),
            "url", "file::memory:?cache=shared",  // Para que se comparta la base de datos entre todas las conexiones del pool
            "user", "",
            "password", ""
        );

        // Establecemos una conexión con el backend y obtenemos
        // los objetos que nos sirven para recuperar y guardar objetos.
        Conexion conexion = BackendFactory.crearConexion(opciones);
        Crud<Centro> centroDao = conexion.getCentroDao();
        Crud<Estudiante> estudianteDao = conexion.getEstudianteDao();

        // Obtiene un centro existente.
        Centro castillo = centroDao.get(11004866).orElse(null);
        System.out.println(castillo);

        // Creación de algunos estudiantes:
        Estudiante[] estudiantes = new Estudiante[] {
            new Estudiante(1, "Perico de los palotes", LocalDate.parse("10/12/1994", formato), castillo),
            new Estudiante(2, "María de la O", LocalDate.parse("23/04/1990", formato), castillo)
        };

        estudianteDao.insert(Arrays.asList(estudiantes));

        Estudiante perico = estudianteDao.get(1).orElse(null);
        System.out.println(perico);

        /*
        try(Stream<Centro> centros = centroDao.get()) {
            centros.forEach(System.out::println);
        }
        */
    }
}