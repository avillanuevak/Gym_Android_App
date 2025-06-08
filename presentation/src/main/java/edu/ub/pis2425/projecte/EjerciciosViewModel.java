package edu.ub.pis2425.projecte;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import edu.ub.pis2425.projectejady0.data.repositories.firestore.FirestoreExerciseRepository;
import edu.ub.pis2425.projectejady0.domain.Exercise;
import edu.ub.pis2425.projectejady0.domain.ExerciseId;
import edu.ub.pis2425.projectejady0.domain.RoutineExercise;
import edu.ub.pis2425.projectejady0.domain.RoutineId;
import edu.ub.pis2425.projectejady0.domain.repositories.EjercicioRepository;
import edu.ub.pis2425.projectejady0.features.AddNewExerciceUseCase;
import edu.ub.pis2425.projectejady0.features.DropOutExerciceUseCase;
import edu.ub.pis2425.projectejady0.features.GetAllGymExercicesUseCase;
import edu.ub.pis2425.projectejady0.features.addNewExerciceToGymUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EjerciciosViewModel extends ViewModel {

    private final AddNewExerciceUseCase addNewExerciseUseCase;
    private final GetAllGymExercicesUseCase getAllGymExercicesUseCase;

    private final MutableLiveData<String> saveExerciseResult = new MutableLiveData<>();

    private final DropOutExerciceUseCase dropOutExerciceUseCase;

    private final addNewExerciceToGymUseCase addNewExerciceToGymUseCase;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData <List<Exercise>> gymExercices = new MutableLiveData<>();

    public EjerciciosViewModel(addNewExerciceToGymUseCase addNewExerciceToGymUseCase, GetAllGymExercicesUseCase getAllGymExercicesUseCase, AddNewExerciceUseCase addNewExerciseUseCase) {
        this.addNewExerciseUseCase = addNewExerciseUseCase;
        this.getAllGymExercicesUseCase = getAllGymExercicesUseCase;
        this.dropOutExerciceUseCase = new DropOutExerciceUseCase();
        this.addNewExerciceToGymUseCase = addNewExerciceToGymUseCase;

    }

    public LiveData<List<Exercise>> getGymExercices() {
        return gymExercices;
    }
    public void fetchGymExercices() {
        disposables.add(getAllGymExercicesUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> gymExercices.setValue(result),
                        error -> {
                            gymExercices.setValue(null);
                            Log.e("EjerciciosViewModel", "Error fetching exercises: ", error);
                        }
                )
        );
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }

    public void DropOutExerciceUseCase(ExerciseId ejercicioId, String clientId) {
    }

    public LiveData<String> getSaveExerciseResult() {
        return saveExerciseResult;
    }


    public void saveExercise(String name, String description, Uri selectedImageUri) {
        if (name == null || name.isEmpty() || description == null || description.isEmpty()) {
            saveExerciseResult.setValue("Error: El nombre o la descripción están vacíos.");
            return;
        }

        disposables.add(
                addNewExerciceToGymUseCase.execute(name, description, String.valueOf(selectedImageUri))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> saveExerciseResult.setValue("Ejercicio guardado con éxito."),
                                error -> {
                                    Log.e("EjerciciosViewModel", "Error al guardar el ejercicio");
                                    saveExerciseResult.setValue("Error al guardar el ejercicio.");
                                }
                        )
        );
    }
}
