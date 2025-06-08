package edu.ub.pis2425.projectejady0.domain;


import java.util.ArrayList;
import java.util.List;

/**
 * Domain entity holding the data and the behavior of a client.
 */
public final class Client {
    /* Attributes */
    private ClientId id;
    private String username;

    private String email;
    private String password;

    private String photoUrl;
    private List<Exercise> ejercicios;

    /**
     * Constructor.
     * @param id The id of the client.
     * @param password The password of the client.
     */
    public Client(ClientId id, String username, String email, String password, String photoUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.photoUrl = photoUrl;
        this.ejercicios = new ArrayList<>();
    }
    /**
     * Constructor.
     * @param id The id of the client.
     * @param username The username of the client.
     * @param email The email of the client.
     * @param password The password of the client.
     * @param ejercicios The list of exercises of the client.
     */
    public Client(ClientId id, String username, String email, String password, List<Exercise> ejercicios) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.photoUrl = null;
        this.ejercicios = ejercicios;
    }

    /**
     * Empty constructor.
     */
    @SuppressWarnings("unused")
    public Client() { }

    /**
     * Gets the id of the client.
     * @return The id of the client.
     */
    public ClientId getId() {
        return id;
    }

    /**
     * Gets the username of the client.
     * @return The username of the client.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password of the client.
     * @return The password of the client.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the photo URL of the client.
     * @return The photo URL of the client.
     */
    public String getEmail() {
        return email;
    }

    public boolean hasExercise(ExerciseId id) {
        for (Exercise ejercicio : ejercicios) {
            if (ejercicio.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

/**
 * Gets the photo URL of the client.
 * @return The photo URL of the client.
 */
public String getPhotoUrl() { return photoUrl; }

/**
 * @param photoUrl The photo URL of the client.
 */
public void setPhotoUrl(String photoUrl) {
    if (photoUrl != null && !photoUrl.isEmpty()) { this.photoUrl = photoUrl; }
    else { this.photoUrl = null; }
}
}