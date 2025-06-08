package edu.ub.pis2425.projecte;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

// En la rama del login, faltaban casos de uso. 

import edu.ub.pis2425.projectejady0.data.repositories.firestore.FirestoreGymClassRepository;
import edu.ub.pis2425.projectejady0.domain.GymClass;
import edu.ub.pis2425.projectejady0.domain.GymClassId;
import edu.ub.pis2425.projectejady0.domain.repositories.GymClassRepository;
import edu.ub.pis2425.projectejady0.features.DropOutFromGymClassUseCase;
import edu.ub.pis2425.projectejady0.features.GetAllGymClassesUseCase;
import edu.ub.pis2425.projectejady0.features.GetAllGymExercicesUseCase;
import edu.ub.pis2425.projectejady0.features.RegisterForGymClassUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GymClassViewModel extends ViewModel {
    private final GetAllGymClassesUseCase getAllGymClassesUseCase;
    private final RegisterForGymClassUseCase registerForGymClassUseCase;
    private final DropOutFromGymClassUseCase dropOutFromGymClassUseCase;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<List<GymClass>> gymClasses = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registerState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> dropOutState = new MutableLiveData<>();

    public GymClassViewModel(GetAllGymClassesUseCase getAllGymClassUseCase,
                             RegisterForGymClassUseCase registerForGymClassUseCase,
                             DropOutFromGymClassUseCase dropOutFromGymClassUseCase) {
        // These should be injected ... but works for now.

        this.getAllGymClassesUseCase = getAllGymClassUseCase;
        this.registerForGymClassUseCase = registerForGymClassUseCase;
        this.dropOutFromGymClassUseCase = dropOutFromGymClassUseCase;
    }

    public LiveData<List<GymClass>> getGymClasses() {
        return gymClasses;
    }

    public LiveData<Boolean> getRegisterState() {
        return registerState;
    }

    public LiveData<Boolean> getDropOutState() {
        return dropOutState;
    }

    public void fetchGymClasses() {
        disposables.add(getAllGymClassesUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> gymClasses.setValue(result),
                        error -> gymClasses.setValue(null)
                )
        );
    }

    public void registerForClass(String clientId, GymClassId gymClassId) {
        disposables.add(registerForGymClassUseCase.execute(clientId, gymClassId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> registerState.setValue(true),
                        error -> registerState.setValue(false)
                )
        );
    }

    public void dropOutFromClass(String clientId, GymClassId gymClassId) {
        disposables.add(dropOutFromGymClassUseCase.execute(clientId, gymClassId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> dropOutState.setValue(true),
                        error -> dropOutState.setValue(false)
                )
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}