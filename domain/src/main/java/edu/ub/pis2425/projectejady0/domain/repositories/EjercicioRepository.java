package edu.ub.pis2425.projectejady0.domain.repositories;

import java.util.List;

import edu.ub.pis2425.projectejady0.domain.Exercise;
import edu.ub.pis2425.projectejady0.domain.ExerciseId;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface EjercicioRepository {


    /**
     * Get all available exercises
     * @return Single with list of exercises
     */
    Single<List<Exercise>> getAllEjercicios();

    /**
     * Get an exercise by its ID
     * @param id ID of the exercise to retrieve
     * @return Single with the exercise
     */
    Single<Exercise> getEjercicioById(ExerciseId id);


    Completable addEjerciciotoClient(String clientId, ExerciseId id);
    Completable removeEjerciciotoClient(String clientId, ExerciseId id);

    Completable addExerciceToGym(String name, String description, String imageUrl);
}
