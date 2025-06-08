package edu.ub.pis2425.projectejady0.features;

import edu.ub.pis2425.projectejady0.domain.NutritionPlan;
import edu.ub.pis2425.projectejady0.domain.NutritionCalculationResult;
import edu.ub.pis2425.projectejady0.domain.NutritionPlan.ActivityLevel;

/**
 * Calculates nutrition requirements based on user metrics and goals
 */
public class CalculateNutritionPlanUseCase {

    private static final double PROTEIN_CALORIES_PER_GRAM = 4;
    private static final double CARBS_CALORIES_PER_GRAM = 4;
    private static final double FATS_CALORIES_PER_GRAM = 9;

    public NutritionCalculationResult execute(NutritionPlan plan) {
        // 1. Calculate BMR (Mifflin-St Jeor Equation)
        double bmr = calculateBMR(plan);

        // 2. Calculate TDEE based on activity level
        double tdee = calculateTDEE(bmr, plan.getActivityLevel());

        // 3. Calculate target calories based on weight change goal
        double targetCalories = calculateTargetCalories(tdee, plan);

        // 4. Calculate macronutrient distribution
        return calculateMacronutrients(targetCalories, plan.isMuscleGainFocus());
    }

    private double calculateBMR(NutritionPlan plan) {
        // Mifflin-St Jeor Equation
        if (plan.getAge() <= 0 || plan.getWeight() <= 0 || plan.getHeight() <= 0) {
            throw new IllegalArgumentException("Invalid parameters for BMR calculation");
        }

        if ("male".equalsIgnoreCase(plan.getSex())) {
            return (10 * plan.getWeight()) + (6.25 * plan.getHeight()) - (5 * plan.getAge()) + 5;
        } else { // female
            return (10 * plan.getWeight()) + (6.25 * plan.getHeight()) - (5 * plan.getAge()) - 161;
        }
    }

    private double calculateTDEE(double bmr, ActivityLevel activityLevel) {
        double activityMultiplier = switch (activityLevel) {
            case LIGHTLY_ACTIVE -> 1.375;
            case MODERATELY_ACTIVE -> 1.55;
            case VERY_ACTIVE -> 1.725;
            default -> 1.2;
        };

        return bmr * activityMultiplier;
    }

    private double calculateTargetCalories(double tdee, NutritionPlan plan) {
        if (plan.getTimeForChange() <= 0) {
            return tdee; // Maintenance if no time specified
        }

        // Calculate weekly calorie adjustment
        double weeklyCalorieAdjustment = plan.getWeightChangeGoal() * 7700 / plan.getTimeForChange();
        double dailyCalorieAdjustment = weeklyCalorieAdjustment / 7;

        return tdee + dailyCalorieAdjustment;
    }

    private NutritionCalculationResult calculateMacronutrients(double targetCalories, boolean muscleGainFocus) {
        // Macronutrient ratios based on goals
        double proteinRatio = muscleGainFocus ? 0.3 : 0.25;
        double fatRatio = 0.25; // Fixed ratio
        double carbRatio = 1 - proteinRatio - fatRatio;

        // Calculate grams
        double proteinGrams = (targetCalories * proteinRatio) / PROTEIN_CALORIES_PER_GRAM;
        double fatGrams = (targetCalories * fatRatio) / FATS_CALORIES_PER_GRAM;
        double carbGrams = (targetCalories * carbRatio) / CARBS_CALORIES_PER_GRAM;

        return new NutritionCalculationResult(0, 0, targetCalories, proteinGrams, carbGrams, fatGrams);
    }
}