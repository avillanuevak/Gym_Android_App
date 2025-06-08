package edu.ub.pis2425.projectejady0.features;

import edu.ub.pis2425.projectejady0.domain.GymClass;
import edu.ub.pis2425.projectejady0.domain.GymClassId;
import edu.ub.pis2425.projectejady0.domain.repositories.GymClassRepository;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class DropOutFromGymClassUseCase {
    private final GymClassRepository repository;

    public DropOutFromGymClassUseCase(GymClassRepository repository) {
        this.repository = repository;
    }

    public Completable execute(String clientId, GymClassId gymClassId) {
        return repository.dropOutClientFromClass(clientId, gymClassId);
    }
}