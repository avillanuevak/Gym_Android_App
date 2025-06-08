package edu.ub.pis2425.projectejady0.domain.repositories;

import edu.ub.pis2425.projectejady0.domain.GymClass;
import edu.ub.pis2425.projectejady0.domain.GymClassId;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface GymClassRepository {
    /**
     * Get all available gym classes
     * @return Single with list of gym classes
     */
    Single<List<GymClass>> getAllGymClasses();

    /**
     * Register a client for a specific gym class
     * @param clientId ID of the client to register
     * @param gymClassId ID of the gym class to register for
     * @return Completable that completes when registration is successful
     */
    Completable registerClientForClass(String clientId, GymClassId gymClassId);

    /**
     * Remove a client from a specific gym class
     * @param clientId ID of the client to remove
     * @param gymClassId ID of the gym class to remove from
     * @return Completable that completes when removal is successful
     */
    Completable dropOutClientFromClass(String clientId, GymClassId gymClassId);

}