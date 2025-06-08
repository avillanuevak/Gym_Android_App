package edu.ub.pis2425.projectejady0.data.dtos.firestore;

import com.google.firebase.firestore.DocumentId;
import java.util.List;

public class RoutineFirestoreDto {
    @DocumentId
    private String id;
    private String name;
    private List<RoutineExerciseFirestoreDto> exercises;
    private List<String> days;
    private String clientId;

    public RoutineFirestoreDto() {}

    public RoutineFirestoreDto(String id, String name, List<RoutineExerciseFirestoreDto> exercises,
                               List<String> days, String clientId) {
        this.id = id;
        this.name = name;
        this.exercises = exercises;
        this.days = days;
        this.clientId = clientId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RoutineExerciseFirestoreDto> getExercises() {
        return exercises;
    }

    public void setExercises(List<RoutineExerciseFirestoreDto> exercises) {
        this.exercises = exercises;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}