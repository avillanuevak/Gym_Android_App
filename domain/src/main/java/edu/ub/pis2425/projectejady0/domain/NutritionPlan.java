package edu.ub.pis2425.projectejady0.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a user's nutrition plan with personal metrics and goals.
 */
public class NutritionPlan implements Serializable {
    private NutritionPlanId id;
    private String clientId;
    private String planName;
    private int age;
    private double height; // in cm
    private double weight; // in kg
    private String sex;
    private double weightChangeGoal; // in kg (positive for gain, negative for loss)
    private int timeForChange; // in weeks
    private boolean muscleGainFocus;
    private ActivityLevel activityLevel;
    private boolean isActive;
    private double targetCalories;
    private double proteinGrams;
    private double carbsGrams;
    private double fatsGrams;
    public enum ActivityLevel {
        SEDENTARY,
        LIGHTLY_ACTIVE,
        MODERATELY_ACTIVE,
        VERY_ACTIVE
    }

    public NutritionPlan() {    }

    public NutritionPlan(NutritionPlanId id, int age, String planName, double height, double weight,
                         String sex, double weightChangeGoal, int timeForChange,
                         boolean muscleGainFocus, ActivityLevel activityLevel) {
        this.id = id;
        this.age = age;
        this.planName = planName;
        this.height = height;
        this.weight = weight;
        this.sex = sex;
        this.weightChangeGoal = weightChangeGoal;
        this.timeForChange = timeForChange;
        this.muscleGainFocus = muscleGainFocus;
        this.activityLevel = activityLevel;
        this.targetCalories = 0;
        this.carbsGrams = 0;
        this.proteinGrams = 0;
        this.fatsGrams = 0;
    }

    // Getters and Setters
    public NutritionPlanId getId() {
        return id;
    }

    public void setId(NutritionPlanId id) {
        this.id = id;
    }
    
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getName() {
        return planName;
    }

    public void setName(String planName) {
        this.planName = planName;
    }

    public int getAge() { return age; }

    public void setAge(int age) {
        if (age <= 0) {
            throw new IllegalArgumentException("Age must be positive");
        }
        this.age = age;
    }
    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }


    public void setWeight(double weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        this.weight = weight;
    }

    public String getSex() { return sex; }

    public void setSex(String sex) {
        if (!"male".equalsIgnoreCase(sex) && !"female".equalsIgnoreCase(sex)) {
            throw new IllegalArgumentException("Sex must be 'male' or 'female'");
        }
        this.sex = sex.toLowerCase();
    }

    public double getWeightChangeGoal() {
        return weightChangeGoal;
    }

    public void setWeightChangeGoal(double weightChangeGoal) {
        this.weightChangeGoal = weightChangeGoal;
    }

    public int getTimeForChange() {
        return timeForChange;
    }

    public void setTimeForChange(int timeForChange) {
        if (timeForChange <= 0) {
            throw new IllegalArgumentException("Time for change must be positive");
        }
        this.timeForChange = timeForChange;
    }

    public boolean isMuscleGainFocus() {
        return muscleGainFocus;
    }

    public void setMuscleGainFocus(boolean muscleGainFocus) {
        this.muscleGainFocus = muscleGainFocus;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = ActivityLevel.valueOf(Objects.requireNonNull(activityLevel, "Activity level cannot be null"));
    }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }

    public double getTargetCalories() {
        return targetCalories;
    }

    public void setTargetCalories(double targetCalories) {
        this.targetCalories = targetCalories;
    }

    public double getProteinGrams() {
        return proteinGrams;
    }

    public void setProteinGrams(double proteinGrams) {
        this.proteinGrams = proteinGrams;
    }

    public double getCarbsGrams() {
        return carbsGrams;
    }

    public void setCarbsGrams(double carbsGrams) {
        this.carbsGrams = carbsGrams;
    }

    public double getFatsGrams() {
        return fatsGrams;
    }

    public void setFatsGrams(double fatsGrams) {
        this.fatsGrams = fatsGrams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NutritionPlan that = (NutritionPlan) o;
        return age == that.age &&
                Double.compare(that.height, height) == 0 &&
                Double.compare(that.weight, weight) == 0 &&
                Double.compare(that.weightChangeGoal, weightChangeGoal) == 0 &&
                timeForChange == that.timeForChange &&
                muscleGainFocus == that.muscleGainFocus &&
                Objects.equals(id, that.id) &&
                activityLevel == that.activityLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, age, height, weight, weightChangeGoal,
                timeForChange, muscleGainFocus, activityLevel);
    }
}