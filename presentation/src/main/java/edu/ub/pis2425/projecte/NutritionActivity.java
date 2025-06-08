package edu.ub.pis2425.projecte;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2425.projecte.databinding.ActivityNutritionBinding;
import edu.ub.pis2425.projectejady0.data.repositories.firestore.NutritionPlanFirestoreRepository;
import edu.ub.pis2425.projectejady0.domain.NutritionCalculationResult;
import edu.ub.pis2425.projectejady0.domain.NutritionPlan;
import edu.ub.pis2425.projectejady0.features.CalculateNutritionPlanUseCase;

public class NutritionActivity extends AppCompatActivity {

    private static final String TAG = "NutritionActivity";
    private ActivityNutritionBinding binding;
    private NutritionViewModel nutritionViewModel;
    private NutritionPlanAdapter nutritionPlanAdapter;
    private List<NutritionPlan> nutritionPlans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNutritionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d(TAG, "NutritionActivity created");

        initViewModel();
        NavigationHelper navigationHelper = new NavigationHelper(this);
        navigationHelper.setupNavigation();

        // Set default values
        setDefaultValues();

        initNutritionComponents();
        initWidgetListeners();

        String userId = SessionManager.getInstance(this).getUserId();
        Log.d(TAG, "onCreate: Loading nutrition plans for user ID: " + userId);
        nutritionViewModel.loadClientNutritionPlans(userId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "NutritionActivity resumed");

        // Reset to default values when resumed
        setDefaultValues();

        String userId = SessionManager.getInstance(this).getUserId();
        Log.d(TAG, "onResume: Reloading nutrition plans for user ID: " + userId);
        nutritionViewModel.loadClientNutritionPlans(userId);
    }

    private void setDefaultValues() {
        // Set default selections for spinners
        String[] weightGoals = getResources().getStringArray(R.array.weight_goal_options);
        String[] activityLevels = getResources().getStringArray(R.array.activity_level_options);

        binding.spinnerWeightGoal.setText(weightGoals[0], false); // First item is "Choose Weight Goal"
        binding.spinnerActivityLevel.setText(activityLevels[0], false); // First item is "Choose Activity Level"
    }

    private void initNutritionComponents() {
        nutritionPlanAdapter = new NutritionPlanAdapter(nutritionPlans, this::onPlanClicked, this::showDeleteConfirmationDialog);

        // Weight Goal Spinner - setup only once
        binding.spinnerWeightGoal.setSimpleItems(R.array.weight_goal_options);
        binding.spinnerWeightGoal.setOnClickListener(v -> binding.spinnerWeightGoal.showDropDown());

        // Rest of your existing initialization code...
        binding.rvNutritionPlans.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNutritionPlans.setAdapter(nutritionPlanAdapter);

        // Activity Level Spinner
        binding.spinnerActivityLevel.setSimpleItems(R.array.activity_level_options);
        binding.spinnerActivityLevel.setOnClickListener(v -> binding.spinnerActivityLevel.showDropDown());

        // Observe nutrition plans
        nutritionViewModel.getClientNutritionPlans().observe(this, plans -> {
            if (plans != null) {
                nutritionPlans.clear();
                nutritionPlans.addAll(plans);
                nutritionPlanAdapter.notifyDataSetChanged();
            }
        });

        // Observe calculation results to immediately proceed with saving when available
        nutritionViewModel.getNutritionResult().observe(this, result -> {
            if (result != null) {
                // When result is available, continue with saving the plan
                finalizeSavingNutritionPlan(result);
            }
        });
    }

    private void initWidgetListeners() {
        binding.btnSave.setOnClickListener(v -> saveNutritionPlan());
    }

    private void calculateNutritionPlan() {
        try {
            int age = Integer.parseInt(binding.etAge.getText().toString());
            double height = Double.parseDouble(binding.etHeight.getText().toString());
            double weight = Double.parseDouble(binding.etWeight.getText().toString());
            String sex = binding.rbMale.isChecked() ? "male" : "female";
            double weightChangeGoal = getSelectedWeightGoal();
            weightChangeGoal = Math.min(3, Math.max(-3, weightChangeGoal));
            int timeFrame = Integer.parseInt(binding.etTimeFrame.getText().toString());
            boolean muscleGain = binding.cbMuscleGain.isChecked();
            String activityLevel = getSelectedActivityLevel();

            String clientId = SessionManager.getInstance(this).getUserId();

            NutritionPlan plan = new NutritionPlan();
            plan.setClientId(clientId);
            plan.setAge(age);
            plan.setHeight(height);
            plan.setWeight(weight);
            plan.setSex(sex);
            plan.setWeightChangeGoal(weightChangeGoal);
            plan.setTimeForChange(timeFrame);
            plan.setMuscleGainFocus(muscleGain);
            plan.setActivityLevel(activityLevel);
            plan.setName("Plan " + (nutritionPlans.size() + 1));

            nutritionViewModel.calculateNutritionPlan(plan);

        } catch (NumberFormatException e) {
            // This shouldn't happen as validateInputs() should catch these first
            Toast.makeText(this, "Please fill all fields with valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private double getSelectedWeightGoal() {
        String selected = binding.spinnerWeightGoal.getText().toString();

        if (selected.contains("Maintain")) return 0;
        if (selected.contains("Lose weight slightly")) return -0.5;
        if (selected.contains("Lose weight moderately")) return -1;
        if (selected.contains("Lose weight aggressively")) return -2;
        if (selected.contains("Gain weight slightly")) return 0.5;
        if (selected.contains("Gain weight moderately")) return 1;
        if (selected.contains("Gain weight aggressively")) return 2;

        // Default to maintaining weight if nothing matches
        return 0;
    }

    private String getSelectedActivityLevel() {
        String selected = binding.spinnerActivityLevel.getText().toString();
        try {
            return selected.toUpperCase()
                    .replace(" ", "_")
                    .replace("-", "_");
        } catch (IllegalArgumentException e) {
            return "MODERATELY_ACTIVE";
        }
    }

    private void saveNutritionPlan() {
        // First validate all required fields
        if (!validateInputs()) {
            return; // Exit if validation fails
        }

        try {
            // Calculate the nutrition plan and wait for result via observer
            calculateNutritionPlan();
            // Show loading indicator
            Toast.makeText(this, "Calculating nutrition plan...", Toast.LENGTH_SHORT).show();

            // The finalizeSavingNutritionPlan will be called when the result is available
            // via the observer we set up in initNutritionComponents
        } catch (Exception e) {
            Toast.makeText(this, "Error creating nutrition plan", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error in saveNutritionPlan", e);
        }
    }

    private void finalizeSavingNutritionPlan(NutritionCalculationResult result) {
        try {
            String clientId = SessionManager.getInstance(this).getUserId();

            // Create new nutrition plan
            NutritionPlan plan = new NutritionPlan();
            plan.setClientId(clientId);
            plan.setAge(Integer.parseInt(binding.etAge.getText().toString()));
            plan.setHeight(Double.parseDouble(binding.etHeight.getText().toString()));
            plan.setWeight(Double.parseDouble(binding.etWeight.getText().toString()));
            plan.setSex(binding.rbMale.isChecked() ? "male" : "female");

            // Handle weight change goal
            double weightChangeGoal = getSelectedWeightGoal();
            plan.setWeightChangeGoal(weightChangeGoal);

            plan.setTimeForChange(Integer.parseInt(binding.etTimeFrame.getText().toString()));
            plan.setMuscleGainFocus(binding.cbMuscleGain.isChecked());
            plan.setActivityLevel(getSelectedActivityLevel());
            plan.setName("Plan " + (nutritionPlans.size() + 1));

            // Save calculation results
            plan.setTargetCalories(result.getTargetCalories());
            plan.setProteinGrams(result.getProteinGrams());
            plan.setCarbsGrams(result.getCarbsGrams());
            plan.setFatsGrams(result.getFatsGrams());

            // Save the plan to ViewModel
            nutritionViewModel.saveNutritionPlan(plan, result);

            // Update UI
            nutritionPlans.add(plan);
            nutritionPlanAdapter.notifyItemInserted(nutritionPlans.size() - 1);

            // Reset input fields
            resetInputFields();

            // Launch the details activity
            Intent intent = new Intent(this, NutritionPlanDetailsActivity.class);
            intent.putExtra("nutrition_plan", plan);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error finalizing nutrition plan", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error in finalizeSavingNutritionPlan", e);
        }
    }

    private boolean validateInputs() {
        // Validate age
        if (binding.etAge.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter your age", Toast.LENGTH_SHORT).show();
            binding.etAge.requestFocus();
            return false;
        }
        try {
            int age = Integer.parseInt(binding.etAge.getText().toString());
            if (age <= 0 || age > 120) {
                Toast.makeText(this, "Please enter a valid age (1-120)", Toast.LENGTH_SHORT).show();
                binding.etAge.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Age must be a whole number", Toast.LENGTH_SHORT).show();
            binding.etAge.requestFocus();
            return false;
        }

        // Validate height
        if (binding.etHeight.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter your height", Toast.LENGTH_SHORT).show();
            binding.etHeight.requestFocus();
            return false;
        }
        try {
            double height = Double.parseDouble(binding.etHeight.getText().toString());
            if (height <= 0 || height > 300) {
                Toast.makeText(this, "Please enter a valid height (1-300 cm)", Toast.LENGTH_SHORT).show();
                binding.etHeight.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Height must be a number", Toast.LENGTH_SHORT).show();
            binding.etHeight.requestFocus();
            return false;
        }

        // Validate weight
        if (binding.etWeight.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter your weight", Toast.LENGTH_SHORT).show();
            binding.etWeight.requestFocus();
            return false;
        }
        try {
            double weight = Double.parseDouble(binding.etWeight.getText().toString());
            if (weight <= 0 || weight > 500) {
                Toast.makeText(this, "Please enter a valid weight (1-500 kg)", Toast.LENGTH_SHORT).show();
                binding.etWeight.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Weight must be a number", Toast.LENGTH_SHORT).show();
            binding.etWeight.requestFocus();
            return false;
        }

        // Validate time frame
        if (binding.etTimeFrame.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter the time frame", Toast.LENGTH_SHORT).show();
            binding.etTimeFrame.requestFocus();
            return false;
        }
        try {
            int timeFrame = Integer.parseInt(binding.etTimeFrame.getText().toString());
            if (timeFrame <= 0 || timeFrame > 104) {
                Toast.makeText(this, "Please enter a valid time frame (1-104 weeks)", Toast.LENGTH_SHORT).show();
                binding.etTimeFrame.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Time frame must be a whole number", Toast.LENGTH_SHORT).show();
            binding.etTimeFrame.requestFocus();
            return false;
        }

        // Validate weight goal selection
        if (binding.spinnerWeightGoal.getText().toString().equals("Choose Weight Goal")) {
            Toast.makeText(this, "Please select a weight goal", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate activity level selection
        if (binding.spinnerActivityLevel.getText().toString().equals("Choose Activity Level")) {
            Toast.makeText(this, "Please select an activity level", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void onPlanClicked(NutritionPlan plan) {
        if (plan == null) {
            Toast.makeText(this, "Error: Plan data is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Intent intent = new Intent(this, NutritionPlanDetailsActivity.class);
            intent.putExtra("nutrition_plan", plan);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error starting details activity", e);
            Toast.makeText(this, "Error opening plan details", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViewModel() {
        NutritionPlanFirestoreRepository nutritionPlanRepository = new NutritionPlanFirestoreRepository();
        CalculateNutritionPlanUseCase calculateNutritionUseCase = new CalculateNutritionPlanUseCase();

        ProfileViewModelFactory factory = new ProfileViewModelFactory(
                null,
                calculateNutritionUseCase,
                nutritionPlanRepository
        );

        nutritionViewModel = new ViewModelProvider(this, factory).get(NutritionViewModel.class);
    }

    private void resetInputFields() {
        binding.etAge.setText("");
        binding.etHeight.setText("");
        binding.etWeight.setText("");
        binding.etTimeFrame.setText("");

        binding.rbMale.setChecked(false);
        binding.rbFemale.setChecked(false);

        binding.cbMuscleGain.setChecked(false);

        String[] weightGoals = getResources().getStringArray(R.array.weight_goal_options);
        String[] activityLevels = getResources().getStringArray(R.array.activity_level_options);

        binding.spinnerWeightGoal.setText(weightGoals[0], false);
        binding.spinnerActivityLevel.setText(activityLevels[0], false);
    }

    private void showDeleteConfirmationDialog(NutritionPlan plan) {
        new MaterialAlertDialogBuilder(this, R.style.DeleteDialogTheme)
                .setTitle("Delete Plan")
                .setMessage("Are you sure you want to delete this nutrition plan?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    nutritionViewModel.deleteNutritionPlan(plan);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}