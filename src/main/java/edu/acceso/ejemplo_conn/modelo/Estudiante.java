package edu.acceso.ejemplo_conn.modelo;

import java.time.LocalDate;

import edu.acceso.sqlutils.Entity;
import edu.acceso.sqlutils.annotations.Fk;


public class Estudiante implements Entity {

    private int id;
    private String nombre;
    private LocalDate nacimiento;

    @Fk
    private Centro centro;

    public Estudiante() {
        super();
    }

    public Estudiante cargarDatos(int id, String nombre, LocalDate nacimiento, Centro centro) {
        setId(id);
        setNombre(nombre);
        setNacimiento(nacimiento);
        setCentro(centro);

        return this;
    }

    public Estudiante(int id, String nombre, LocalDate nacimiento, Centro centro) {
        this.cargarDatos(id, nombre, nacimiento, centro);
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public LocalDate getNacimiento() {
        return nacimiento;
    }
    public void setNacimiento(LocalDate nacimiento) {
        this.nacimiento = nacimiento;
    }
    public Centro getCentro() {
        return centro;
    }
    public void setCentro(Centro centro) {
        this.centro = centro;
    }
    
}
