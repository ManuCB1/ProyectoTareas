package com.example.proyectotareas.model;

public class Tarea {

    private int id;
    private String nombre;
    private String contenido;
    private String fecha;

    public Tarea(int id, String nombre, String contenido) {
        this.id = id;
        this.nombre = nombre;
        this.contenido = contenido;
    }

    public Tarea(int id, String nombre, String contenido, String fecha) {
        this.id = id;
        this.nombre = nombre;
        this.contenido = contenido;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContenido() {
        return contenido;
    }

    public String getFecha() {
        return fecha;
    }
}
