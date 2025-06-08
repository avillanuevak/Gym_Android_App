package edu.ub.pis2425.projectejady0.domain;

import java.util.Objects;

public class ExerciseId {

    private String id;

    public ExerciseId(String id) {
        this.id = id;
    }

    public ExerciseId() {
    }

    public String getId() {
        return id;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ExerciseId ejercicioId = (ExerciseId) obj;
        return Objects.equals(id, ejercicioId.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    @Override
    public String toString() {
        return id;
    }
}
