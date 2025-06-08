package edu.ub.pis2425.projecte;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import edu.ub.pis2425.projectejady0.domain.repositories.NutritionPlanRepository;
import edu.ub.pis2425.projectejady0.features.CalculateNutritionPlanUseCase;
import edu.ub.pis2425.projectejady0.features.UCChangeClientInfo;

public class ProfileViewModelFactory implements ViewModelProvider.Factory {
    private final UCChangeClientInfo changeClientInfoUseCase;
    private final CalculateNutritionPlanUseCase calculateNutritionUseCase;
    private final NutritionPlanRepository nutritionPlanRepository;

    public ProfileViewModelFactory(UCChangeClientInfo changeClientInfoUseCase,
                                   CalculateNutritionPlanUseCase calculateNutritionUseCase,
                                   NutritionPlanRepository nutritionPlanRepository) {
        this.changeClientInfoUseCase = changeClientInfoUseCase;
        this.calculateNutritionUseCase = calculateNutritionUseCase;
        this.nutritionPlanRepository = nutritionPlanRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(
                    changeClientInfoUseCase,
                    calculateNutritionUseCase,
                    nutritionPlanRepository
            );
        } else if (modelClass.isAssignableFrom(NutritionViewModel.class)) {
            return (T) new NutritionViewModel(
                    calculateNutritionUseCase,
                    nutritionPlanRepository
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
