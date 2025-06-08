package edu.ub.pis2425.projectejady0.features;


import edu.ub.pis2425.projectejady0.domain.GymClass;
import edu.ub.pis2425.projectejady0.domain.GymClassId;
import edu.ub.pis2425.projectejady0.domain.repositories.GymClassRepository;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
public class RegisterForGymClassUseCase {
    private final GymClassRepository repository;

    public RegisterForGymClassUseCase(GymClassRepository repository) {
        this.repository = repository;
    }

    public Completable execute(String clientId, GymClassId gymClassId) {
        return repository.registerClientForClass(clientId, gymClassId);
    }
}