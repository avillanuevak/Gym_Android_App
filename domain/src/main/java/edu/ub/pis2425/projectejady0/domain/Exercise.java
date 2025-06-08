package edu.ub.pis2425.projectejady0.domain;

public class Exercise {

    private ExerciseId id;
    private String nombre;
    private String descripcion;
    private String imagen;


    @SuppressWarnings("unused")
    public Exercise() {
    }

    public Exercise(ExerciseId id, String nombre, String descripcion, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;

    }

    public ExerciseId getId() {
        return id;
    }
    public void setId(ExerciseId id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getImagen() {
        return imagen;
    }
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

}

