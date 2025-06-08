package edu.ub.pis2425.projectejady0.features;

import edu.ub.pis2425.projectejady0.domain.repositories.EjercicioRepository;
import io.reactivex.rxjava3.core.Completable;

public class addNewExerciceToGymUseCase {

    private final EjercicioRepository repository;

    public addNewExerciceToGymUseCase(EjercicioRepository repository) {
        this.repository = repository;
    }

    public Completable execute(String name, String description, String imageUrl) {
        return repository.addExerciceToGym(name, description, imageUrl);
    }

}
