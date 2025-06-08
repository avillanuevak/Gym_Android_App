package edu.ub.pis2425.projectejady0.domain;

import java.util.Objects;

public class RoutineId {

    private String id;
    public RoutineId() {
    }


    public RoutineId(String id) {
        this.id = id;
    }


    public String getId() {
        return id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoutineId)) return false;
        RoutineId that = (RoutineId) o;
        return Objects.equals(id, that.id);
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

