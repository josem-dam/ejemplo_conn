package edu.acceso.ejemplo_conn.modelo;

import java.time.LocalDate;

public class Estudiante implements Entity {

    private int id;
    private int nombre;
    private LocalDate nacimiento;
    @Fk(key="centroId")
    private Centro centro;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getNombre() {
        return nombre;
    }
    public void setNombre(int nombre) {
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
