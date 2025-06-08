package edu.ub.pis2425.projecte;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import edu.ub.pis2425.projectejady0.features.GetUserEnrolledClassesUseCase;
import edu.ub.pis2425.projectejady0.features.GetUserRoutinesUseCase;

public class CalendarViewModelFactory implements ViewModelProvider.Factory {
    private final GetUserEnrolledClassesUseCase getUserEnrolledClassesUseCase;
    private final GetUserRoutinesUseCase getUserRoutinesUseCase;

    public CalendarViewModelFactory(GetUserEnrolledClassesUseCase getUserEnrolledClassesUseCase, GetUserRoutinesUseCase getUserRoutinesUseCase) {
        this.getUserEnrolledClassesUseCase = getUserEnrolledClassesUseCase;
        this.getUserRoutinesUseCase = getUserRoutinesUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CalendarViewModel.class)) {
            return (T) new CalendarViewModel(getUserEnrolledClassesUseCase, getUserRoutinesUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
