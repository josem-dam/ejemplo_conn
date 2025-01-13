package edu.acceso.ejemplo_conn.modelo;

import edu.acceso.sqlutils.Entity;

public class Centro implements Entity {

    private int id;
    private String nombre;
    private String titularidad;

    public Centro() {
        super();
    }

    public Centro cargarDatos(int id, String nombre, String titularidad) {
        setId(id);
        setNombre(nombre);
        setTitularidad(titularidad);
        return this;
    }

    public Centro(int id, String nombre, String titularidad) {
        cargarDatos(id, nombre, titularidad);
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
    public String getTitularidad() {
        return titularidad;
    }
    public void setTitularidad(String titularidad) {
        this.titularidad = titularidad;
    }
}
