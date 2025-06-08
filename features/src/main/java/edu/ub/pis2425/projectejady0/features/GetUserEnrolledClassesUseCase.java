package edu.ub.pis2425.projectejady0.features;

import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2425.projectejady0.domain.GymClass;
import edu.ub.pis2425.projectejady0.domain.repositories.GymClassRepository;
import io.reactivex.rxjava3.core.Single;

public class GetUserEnrolledClassesUseCase {
    private final GymClassRepository repository;

    public GetUserEnrolledClassesUseCase(GymClassRepository repository) {
        this.repository = repository;
    }

    public Single<List<GymClass>> execute(String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            return Single.error(new IllegalArgumentException("Client ID cannot be null or empty"));
        }

        return repository.getAllGymClasses()
                .map(gymClasses -> {
                    List<GymClass> enrolledClasses = new ArrayList<>();
                    for (GymClass gymClass : gymClasses) {
                        if (gymClass.getRegisteredUsers() != null &&
                                gymClass.getRegisteredUsers().contains(clientId)) {
                            enrolledClasses.add(gymClass);
                        }
                    }
                    return enrolledClasses;
                });
    }
}