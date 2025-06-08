package edu.ub.pis2425.projectejady0.data.dtos.firestore;

import com.google.firebase.firestore.DocumentId;

public class RoutineExerciseFirestoreDto {
    @DocumentId
    private String id;
    private String exerciseId;
    private ExerciseFirestoreDto exerciseDetails;
    private int sets;
    private int repetitions;
    private double weight;

    public RoutineExerciseFirestoreDto() {}

    public RoutineExerciseFirestoreDto(String id, String exerciseId, ExerciseFirestoreDto exerciseDetails,
                                       int sets, int repetitions, double weight) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.exerciseDetails = exerciseDetails;
        this.sets = sets;
        this.repetitions = repetitions;
        this.weight = weight;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public ExerciseFirestoreDto getExerciseDetails() {
        return exerciseDetails;
    }

    public void setExerciseDetails(ExerciseFirestoreDto exerciseDetails) {
        this.exerciseDetails = exerciseDetails;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}