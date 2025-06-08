package edu.ub.pis2425.projectejady0.features;

import edu.ub.pis2425.projectejady0.domain.RoutineExercise;
import edu.ub.pis2425.projectejady0.domain.RoutineId;
import edu.ub.pis2425.projectejady0.domain.repositories.EjercicioRepository;
import edu.ub.pis2425.projectejady0.domain.repositories.RoutineRepository;
import io.reactivex.rxjava3.core.Completable;

public class AddNewExerciceUseCase {
    private final RoutineRepository repository;

    public AddNewExerciceUseCase(RoutineRepository repository) {
        this.repository = repository;
    }

    public Completable execute(String clientId, RoutineId routineId, RoutineExercise routineExercise) {
        return repository.addExerciseToRoutine(clientId, routineId, routineExercise);
    }
}
