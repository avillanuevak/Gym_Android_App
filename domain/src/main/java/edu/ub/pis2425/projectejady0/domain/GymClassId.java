package edu.ub.pis2425.projectejady0.domain;

import java.io.Serializable;
import java.util.Objects;

public class GymClassId implements Serializable {
    private String id;

    public GymClassId(String id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public GymClassId() {}

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GymClassId gymClassId = (GymClassId) obj;
        return Objects.equals(id, gymClassId.id);
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