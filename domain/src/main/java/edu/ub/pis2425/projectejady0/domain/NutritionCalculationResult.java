package edu.ub.pis2425.projectejady0.domain;

/**
 * Contains the calculated nutrition values for a user
 */
public class NutritionCalculationResult {
    private double bmr; // Basal Metabolic Rate
    private double tdee; // Total Daily Energy Expenditure
    private double targetCalories;
    private double proteinGrams;
    private double carbsGrams;
    private double fatsGrams;

    public NutritionCalculationResult(double bmr, double tdee, double targetCalories,
                                      double proteinGrams, double carbsGrams, double fatsGrams) {
        this.bmr = bmr;
        this.tdee = tdee;
        this.targetCalories = targetCalories;
        this.proteinGrams = proteinGrams;
        this.carbsGrams = carbsGrams;
        this.fatsGrams = fatsGrams;
    }

    // Getters
    public double getBmr() { return bmr; }
    public double getTdee() { return tdee; }
    public double getTargetCalories() { return targetCalories; }
    public double getProteinGrams() { return proteinGrams; }
    public double getCarbsGrams() { return carbsGrams; }
    public double getFatsGrams() { return fatsGrams; }
}