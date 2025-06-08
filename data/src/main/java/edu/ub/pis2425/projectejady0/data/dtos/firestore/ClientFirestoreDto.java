package edu.ub.pis2425.projectejady0.data.dtos.firestore;

import com.google.firebase.firestore.DocumentId;

/**
 * Domain entity holding the data and the behavior of a client.
 */
public class ClientFirestoreDto {
    /* Attributes */
    @DocumentId
    private String id;
    private String username;
    private String name;

    private String email;
    private String password;
    private String photoUrl;

    /**
     * Empty constructor.
     */
    @SuppressWarnings("unused")
    public ClientFirestoreDto() { }

    /**
     * Gets the username of the client.
     * @return The username of the client.
     */
    public String getId() {
        return id;
    }


    /**
     * Gets the email of the client.
     * @return The email of the client.
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
    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getEmail() {
        return email;
    }


    public void setId(String id) {
        this.id = id;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setPhotoUrl(String photoUrl) {this.photoUrl = photoUrl;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
