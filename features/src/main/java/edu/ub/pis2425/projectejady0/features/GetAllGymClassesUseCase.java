package edu.ub.pis2425.projectejady0.features;

import edu.ub.pis2425.projectejady0.domain.GymClass;
import edu.ub.pis2425.projectejady0.domain.GymClassId;
import edu.ub.pis2425.projectejady0.domain.repositories.GymClassRepository;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class GetAllGymClassesUseCase {
    private final GymClassRepository repository;

    public GetAllGymClassesUseCase(GymClassRepository repository) {
        this.repository = repository;
    }

    public Single<List<GymClass>> execute() {
        return repository.getAllGymClasses();
    }
}