package edu.ub.pis2425.projectejady0.features;

import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2425.projectejady0.domain.Routine;
import edu.ub.pis2425.projectejady0.domain.repositories.RoutineRepository;
import io.reactivex.rxjava3.core.Single;

public class GetUserRoutinesUseCase {
    private final RoutineRepository repository;

    public GetUserRoutinesUseCase(RoutineRepository repository) {
        this.repository = repository;
    }

    /// This method retrieves all routines for a given client ID.
    public Single<List<Routine>> execute(String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            return Single.error(new IllegalArgumentException("Client ID cannot be null or empty"));
        }

        return repository.getAllRoutinesForClient(clientId)
                .map(routines -> {
                    List<Routine> validRoutines = new ArrayList<>();
                    for (Routine routine : routines) {
                        if (routine.getClientId() != null && clientId.equals(routine.getClientId().getId())) {
                            validRoutines.add(routine);
                        }
                    }
                    return validRoutines;
                });
    }
}
