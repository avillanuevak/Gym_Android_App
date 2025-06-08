package edu.ub.pis2425.projectejady0.data.dtos.firestore;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

import edu.ub.pis2425.projectejady0.domain.NutritionPlan;

public class NutritionPlanFirestoreDto {
    @DocumentId
    private String id;
    private String clientId;
    private String name;
    private int age;
    private double height;
    private double weight;
    private String sex;
    private double weightChangeGoal;
    private int timeForChange;
    private boolean muscleGainFocus;
    private String activityLevel;
    private boolean isActive;
    @ServerTimestamp
    private Date createdAt;

    private double targetCalories;
    private double proteinGrams;
    private double carbsGrams;
    private double fatsGrams;

    // Empty constructor for Firestore
    public NutritionPlanFirestoreDto() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public String getSex() { return sex; }

    public void setSex(String sex) {this.sex = sex;}
    public double getWeightChangeGoal() { return weightChangeGoal; }
    public void setWeightChangeGoal(double weightChangeGoal) { this.weightChangeGoal = weightChangeGoal; }
    public int getTimeForChange() { return timeForChange; }
    public void setTimeForChange(int timeForChange) { this.timeForChange = timeForChange; }
    public boolean isMuscleGainFocus() { return muscleGainFocus; }
    public void setMuscleGainFocus(boolean muscleGainFocus) { this.muscleGainFocus = muscleGainFocus; }
    public String getActivityLevel() { return activityLevel; }
    public void setActivityLevel(String activityLevel) { this.activityLevel = activityLevel; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }


    public double getTargetCalories() { return targetCalories; }
    public void setTargetCalories(double dailyCalories) { this.targetCalories = dailyCalories; }

    public double getProteinGrams() { return proteinGrams; }
    public void setProteinGrams(double protein) { this.proteinGrams = protein; }

    public double getCarbsGrams() { return carbsGrams; }
    public void setCarbsGrams(double carbs) { this.carbsGrams = carbs; }

    public double getFatsGrams() { return fatsGrams; }
    public void setFatsGrams(double fats) { this.fatsGrams = fats; }
}