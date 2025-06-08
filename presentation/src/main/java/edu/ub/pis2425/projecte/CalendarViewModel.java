package edu.ub.pis2425.projecte;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2425.projectejady0.domain.CalendarItem;
import edu.ub.pis2425.projectejady0.domain.GymClass;
import edu.ub.pis2425.projectejady0.features.GetUserRoutinesUseCase;
import edu.ub.pis2425.projectejady0.features.GetUserEnrolledClassesUseCase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CalendarViewModel extends ViewModel {
    private final GetUserEnrolledClassesUseCase getUserEnrolledClassesUseCase;
    private final GetUserRoutinesUseCase getUserRoutinesUseCase;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<List<CalendarFragment.CalendarItemWithSchedule>> calendarItems = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public CalendarViewModel(GetUserEnrolledClassesUseCase getUserEnrolledClassesUseCase, GetUserRoutinesUseCase getUserRoutinesUseCase) {
        this.getUserEnrolledClassesUseCase = getUserEnrolledClassesUseCase;
        this.getUserRoutinesUseCase = getUserRoutinesUseCase;
    }

    public LiveData<List<CalendarFragment.CalendarItemWithSchedule>> getCalendarItems() {
        return calendarItems;
    }

    public LiveData<String> getError() {
        return error;
    }

    // isLoading is used to show/hide loading indicators in the UI
    // Prevents user interactions while operations are in progress
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadUserCalendarItems(String clientId) {
        isLoading.setValue(true);

        disposables.add(
                Single.zip(
                                getUserEnrolledClassesUseCase.execute(clientId),
                                getUserRoutinesUseCase.execute(clientId),
                                (gymClasses, routines) -> {
                                    List<CalendarFragment.CalendarItemWithSchedule> combined = new ArrayList<>();

                                    for (CalendarItem item : gymClasses) {
                                        for (String day : item.getSchedule()) {
                                            combined.add(new CalendarFragment.CalendarItemWithSchedule(item, day));
                                        }
                                    }
                                    for (CalendarItem item : routines) {
                                        for (String day : item.getSchedule()) {
                                            combined.add(new CalendarFragment.CalendarItemWithSchedule(item, day));
                                        }
                                    }

                                    return combined;
                                }
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                result -> {
                                    calendarItems.setValue(result);
                                    isLoading.setValue(false);
                                },
                                throwable -> {
                                    error.setValue(throwable.getMessage());
                                    isLoading.setValue(false);
                                }
                        )
        );
    }


    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}