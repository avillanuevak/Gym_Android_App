package edu.ub.pis2425.projecte;

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


public class ProfileViewModel extends ViewModel {
    private static final String TAG = "ProfileViewModel";
    private final UCChangeClientInfo changeUserInfoUseCase;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final StateLiveData<Client> userInfo = new StateLiveData<>();
    private final MutableLiveData<String> updateStatus = new MutableLiveData<>();

    public ProfileViewModel(UCChangeClientInfo changeUserInfoUseCase,
                            CalculateNutritionPlanUseCase calculateNutritionUseCase,
                            NutritionPlanRepository nutritionPlanRepository) {
        this.changeUserInfoUseCase = changeUserInfoUseCase;
    }

    public void getUserInfo(String userId) {
        Log.d(TAG, "Getting user info for ID: " + userId);
        userInfo.postLoading();

        // Clear any existing disposable for this operation
        disposables.clear();

        disposables.add(
                changeUserInfoUseCase.getClientRepository()
                        .getByIdReactive(new ClientId(userId))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                user -> {
                                    if (user instanceof Client) {
                                        Client client = (Client) user;
                                        Log.d(TAG, "Retrieved user info - Name: " + client.getUsername() +
                                                ", Photo URL: " + client.getPhotoUrl());

                                        userInfo.postSuccess(client);
                                    } else {
                                        Log.e(TAG, "Invalid user type received");
                                        userInfo.postError(new Throwable("Invalid user type"));
                                    }
                                },
                                throwable -> {
                                    Log.e(TAG, "Error retrieving user info: " + throwable.getMessage());
                                    userInfo.postError(throwable);
                                }
                        )
        );
    }

    public MutableLiveData<String> getUpdateStatus() {
        return updateStatus;
    }

    public StateLiveData<Client> getUserInfoLiveData() {
        return userInfo;
    }

    public void saveProfile(String userId, String newName, String newEmail, String newPassword, String newUrl) {
        Log.d(TAG, "Saving profile - ID: " + userId +
                ", Name: " + (newName.isEmpty() ? "(empty)" : newName) +
                ", Email: " + (newEmail.isEmpty() ? "(empty)" : newEmail) +
                ", Password: " + (newPassword.isEmpty() ? "(empty)" : "provided") +
                ", Photo URL: " + (newUrl == null ? "null" : newUrl));

        Client currentUser = userInfo.getValue() != null ? userInfo.getValue().getData() : null;

        if (currentUser != null) {
            // Use current values if new ones are empty
            String updatedName = newName.isEmpty() ? currentUser.getUsername() : newName;
            String updatedEmail = newEmail.isEmpty() ? currentUser.getEmail() : newEmail;
            String updatedPassword = newPassword.isEmpty() ? currentUser.getPassword() : newPassword;

            // IMPORTANT: DON'T modify the photoUrl here - pass it as is to the repository

            disposables.add(
                    changeUserInfoUseCase.execute(userId, updatedName, updatedEmail, updatedPassword, newUrl)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> {
                                        Log.d(TAG, "Profile updated successfully with photo URL: " + newUrl);
                                        updateStatus.postValue("Profile updated successfully");
                                    },
                                    throwable -> {
                                        Log.e(TAG, "Error updating profile: " + throwable.getMessage());
                                        updateStatus.postValue("Error: " + throwable.getMessage());
                                    }
                            )
            );
        } else {
            Log.e(TAG, "Cannot update profile: User data not loaded");
            updateStatus.postValue("Error: User data not loaded");
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}