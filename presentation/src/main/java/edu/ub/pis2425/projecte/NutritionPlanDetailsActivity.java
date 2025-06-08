package edu.ub.pis2425.projecte;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import edu.ub.pis2425.projectejady0.domain.NutritionPlan;

public class NutritionPlanDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_plan_details);

        // Set up close button
        ImageView ivClose = findViewById(R.id.iv_close);
        ivClose.setOnClickListener(v -> finish());

        NutritionPlan plan = (NutritionPlan) getIntent().getSerializableExtra("nutrition_plan");
        if (plan != null) {
            displayPlanDetails(plan);
        }
    }

    private void displayPlanDetails(NutritionPlan plan) {
        TextView tvPlanName = findViewById(R.id.tv_plan_name);
        TextView tvUserInfo = findViewById(R.id.tv_user_info);
        TextView tvGoals = findViewById(R.id.tv_goals);
        TextView tvMacros = findViewById(R.id.tv_macros);
        TextView tvCalories = findViewById(R.id.tv_calories);

        // Plan Name
        tvPlanName.setText(plan.getName());

        // User Information
        String userInfo = String.format(
                "• Age: %d years\n" +
                        "• Height: %.1f cm\n" +
                        "• Weight: %.1f kg\n" +
                        "• Gender: %s\n" +
                        "• Activity Level: %s",
                plan.getAge(),
                plan.getHeight(),
                plan.getWeight(),
                plan.getSex().equalsIgnoreCase("male") ? "Male" : "Female",
                formatActivityLevel(String.valueOf(plan.getActivityLevel()))
        );
        tvUserInfo.setText(userInfo);

        // Goals
        String goalDirection = plan.getWeightChangeGoal() > 0 ? "Gain" : "Lose";
        String goals = String.format(
                "• %s weight: %.1f kg per week\n" +
                        "• Timeframe: %d weeks\n" +
                        "• Muscle focus: %s",
                goalDirection,
                Math.abs(plan.getWeightChangeGoal()),
                plan.getTimeForChange(),
                plan.isMuscleGainFocus() ? "Yes" : "No"
        );
        tvGoals.setText(goals);

        // Macros
        String macros = String.format(
                "• Protein: %.1f g\n" +
                        "• Carbohydrates: %.1f g\n" +
                        "• Fats: %.1f g",
                plan.getProteinGrams(),
                plan.getCarbsGrams(),
                plan.getFatsGrams()
        );
        tvMacros.setText(macros);

        // Calories
        tvCalories.setText(String.format("%.0f kcal/day", plan.getTargetCalories()));
    }

    private String formatActivityLevel(String activityLevel) {
        if (activityLevel == null) return "Moderate";

        return activityLevel.toLowerCase()
                .replace("_", " ")
                .replace("sedentary", "Sedentary")
                .replace("lightly active", "Lightly Active")
                .replace("moderately active", "Moderately Active")
                .replace("very active", "Very Active")
                .replace("extra active", "Extra Active");
    }
}