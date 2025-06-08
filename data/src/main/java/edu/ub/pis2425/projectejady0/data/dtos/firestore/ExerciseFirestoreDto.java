package edu.ub.pis2425.projectejady0.data.dtos.firestore;

import com.google.firebase.firestore.DocumentId;

/**
 * Domain entity holding the data and the behavior of an exercise.
 */
public class ExerciseFirestoreDto {

    /* Attributes */
    @DocumentId
    private String id;
    private String nombre;
    private String descripcion;
    private String imagen;

    /**
     * Empty constructor.
     */
    public ExerciseFirestoreDto() { }

    /**
     * Constructor with parameters.
     */
    public ExerciseFirestoreDto(String id, String nombre, String descripcion, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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
