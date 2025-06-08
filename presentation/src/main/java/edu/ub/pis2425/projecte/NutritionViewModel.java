package edu.ub.pis2425.projecte;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.MutableLiveData;

import edu.ub.pis2425.projectejady0.domain.ClientId;
import edu.ub.pis2425.projectejady0.domain.NutritionPlan;
import edu.ub.pis2425.projectejady0.domain.NutritionCalculationResult;
import edu.ub.pis2425.projectejady0.domain.NutritionPlanId;
import edu.ub.pis2425.projectejady0.features.UCChangeClientInfo;
import edu.ub.pis2425.projectejady0.features.CalculateNutritionPlanUseCase;
import edu.ub.pis2425.projectejady0.domain.repositories.NutritionPlanRepository;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import androidx.lifecycle.ViewModel;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.ub.pis2425.projectejady0.domain.Client;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NutritionViewModel extends ViewModel {
    private final CalculateNutritionPlanUseCase calculateNutritionUseCase;
    private final NutritionPlanRepository nutritionPlanRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final StateLiveData<Client> userInfo = new StateLiveData<>();
    private final MutableLiveData<String> updateStatus = new MutableLiveData<>();
    private final MutableLiveData<NutritionCalculationResult> nutritionResult = new MutableLiveData<>();
    private final MutableLiveData<List<NutritionPlan>> clientNutritionPlans = new MutableLiveData<>();



    public NutritionViewModel(
            CalculateNutritionPlanUseCase calculateNutritionUseCase,
            NutritionPlanRepository nutritionPlanRepository
    ) {
        this.calculateNutritionUseCase = calculateNutritionUseCase;
        this.nutritionPlanRepository = nutritionPlanRepository;
    }

    public MutableLiveData<String> getUpdateStatus() {
        return updateStatus;
    }
    public StateLiveData<Client> getUserInfoLiveData() {
        return userInfo;
    }

    public MutableLiveData<NutritionCalculationResult> getNutritionResult() {
        return nutritionResult;
    }

    public MutableLiveData<List<NutritionPlan>> getClientNutritionPlans() {
        return clientNutritionPlans;
    }

    public void calculateNutritionPlan(NutritionPlan plan) {
        disposables.add(
                Observable.fromCallable(() -> calculateNutritionUseCase.execute(plan))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                result -> {
                                    nutritionResult.postValue(result);
                                    Log.d(TAG, "Nutrition calculation successful");
                                },
                                error -> {
                                    Log.e(TAG, "Nutrition calculation error: " + error.getMessage());
                                    updateStatus.postValue("Calculation error: " + error.getMessage());
                                }
                        ));
    }

    public void saveNutritionPlan(NutritionPlan plan, NutritionCalculationResult result) {
        // Store calculation results in the plan before saving
        plan.setTargetCalories(result.getTargetCalories());
        plan.setProteinGrams(result.getProteinGrams());
        plan.setCarbsGrams(result.getCarbsGrams());
        plan.setFatsGrams(result.getFatsGrams());

        disposables.add(
                nutritionPlanRepository.save(plan)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    updateStatus.postValue("Nutrition plan saved successfully");
                                    loadClientNutritionPlans(plan.getClientId());
                                },
                                error -> {
                                    Log.e(TAG, "Error saving nutrition plan: " + error.getMessage());
                                    updateStatus.postValue("Error saving plan: " + error.getMessage());
                                }
                        ));
    }

    public void loadClientNutritionPlans(String clientId) {
        Log.d(TAG, "Loading plans for client: " + clientId);
        disposables.add(
                nutritionPlanRepository.getByClientIdReactive(clientId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                plans -> {
                                    if (plans != null) {
                                        Log.d(TAG, "Plans loaded: " + plans.size());
                                        clientNutritionPlans.postValue(plans);
                                    } else {
                                        Log.d(TAG, "No plans found");
                                        clientNutritionPlans.postValue(new ArrayList<>());
                                    }
                                },
                                error -> {
                                    Log.e(TAG, "Error loading nutrition plans: " + error.getMessage());
                                    updateStatus.postValue("Error loading plans");
                                    clientNutritionPlans.postValue(new ArrayList<>());
                                }
                        ));
    }

    public void setActiveNutritionPlan(String clientId, String planId) {
        disposables.add(
                nutritionPlanRepository.setActivePlan(clientId, new NutritionPlanId(planId))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    updateStatus.postValue("Active plan updated");
                                    loadClientNutritionPlans(clientId);
                                },
                                error -> {
                                    Log.e(TAG, "Error setting active plan: " + error.getMessage());
                                    updateStatus.postValue("Error updating active plan");
                                }
                        ));
    }

    public void deleteNutritionPlan(NutritionPlan plan) {
        disposables.add(
                nutritionPlanRepository.delete(plan.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    updateStatus.postValue("Plan deleted successfully");
                                    loadClientNutritionPlans(plan.getClientId());
                                },
                                error -> {
                                    Log.e(TAG, "Error deleting nutrition plan: " + error.getMessage());
                                    updateStatus.postValue("Error deleting plan: " + error.getMessage());
                                }
                        ));
    }
}
