package edu.ub.pis2425.projectejady0.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Value object representing the unique identifier for a NutritionPlan
 */
public class NutritionPlanId implements Serializable {
    private final String id;

    public NutritionPlanId(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("NutritionPlan ID cannot be null or empty");
        }
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NutritionPlanId that = (NutritionPlanId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}