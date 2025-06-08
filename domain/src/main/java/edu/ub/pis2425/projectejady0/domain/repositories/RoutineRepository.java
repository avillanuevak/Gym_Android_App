package edu.ub.pis2425.projectejady0.domain.repositories;

import java.util.List;

import edu.ub.pis2425.projectejady0.domain.Routine;
import edu.ub.pis2425.projectejady0.domain.RoutineExercise;
import edu.ub.pis2425.projectejady0.domain.RoutineId;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

/**
 * Interface for managing routine-related data operations.
 * This repository defines the contract for interacting with routines,
 * abstracting the underlying data source (e.g., Firestore, local DB).
 */
public interface RoutineRepository {

    /**
     * Fetches all routines associated with a specific client.
     *
     * @param clientId Unique identifier of the client.
     * @return A Single emitting a list of routines for the given client.
     */
    Single<List<Routine>> getAllRoutinesForClient(String clientId);

    /**
     * Creates a new routine for a client.
     *
     * @param clientId ID of the client to whom the routine will belong.
     * @param routine The Routine object to be saved.
     * @return A Single emitting the ID of the newly created routine.
     */
    Single<RoutineId> createRoutine(String clientId, Routine routine);

    /**
     * Adds an exercise to an existing routine.
     *
     * @param clientId ID of the client.
     * @param routineId ID of the routine where the exercise will be added.
     * @param exercise The RoutineExercise to be added.
     * @return A Completable that completes when the operation is successful.
     */
    Completable addExerciseToRoutine(String clientId, RoutineId routineId, RoutineExercise exercise);

    /**
     * Updates an existing routine, including its metadata or exercises.
     *
     * @param routine The Routine object with updated fields.
     * @return A Completable that completes when the update is successful.
     */
    Completable updateRoutine(Routine routine);
}
