package edu.ub.pis2425.projectejady0.features;

import java.util.List;

import edu.ub.pis2425.projectejady0.domain.Exercise;
import edu.ub.pis2425.projectejady0.domain.repositories.EjercicioRepository;
import io.reactivex.rxjava3.core.Single;

public class GetAllGymExercicesUseCase {
    private final EjercicioRepository repository;

    public GetAllGymExercicesUseCase(EjercicioRepository repository) {
        this.repository = repository;
    }

    public Single<List<Exercise>> execute() {
        return repository.getAllEjercicios();
    }
}
