package edu.ub.pis2425.projectejady0.data.repositories.firestore;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.ub.pis2425.projectejady0.domain.Exercise;
import edu.ub.pis2425.projectejady0.domain.ExerciseId;
import edu.ub.pis2425.projectejady0.domain.repositories.EjercicioRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class FirestoreExerciseRepository implements EjercicioRepository {
    private final CollectionReference collectionReference;
    private static FirestoreExerciseRepository instance;

    // Constructor privado para Singleton
    private FirestoreExerciseRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.collectionReference = db.collection("Exercises");
    }

    // Método público para obtener la única instancia
    public static synchronized FirestoreExerciseRepository getInstance() {
        if (instance == null) {
            instance = new FirestoreExerciseRepository();
        }
        return instance;
    }


    @Override
    public Single<List<Exercise>> getAllEjercicios() {
        return Single.create(emitter -> {
            if (collectionReference == null) {
                emitter.onError(new IllegalStateException("collectionReference is not initialized."));
                return;
            }

            collectionReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    List<Exercise> ejercicios = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        try {
                            // Verifica si el documento tiene los campos necesarios
                            if (document.contains("nombre") && document.contains("descripcion") && document.contains("imagen")) {
                                Exercise ejercicio = document.toObject(Exercise.class);
                                ejercicio.setId(new ExerciseId(document.getId()));
                                ejercicios.add(ejercicio);
                            } else {
                                Log.w("FirestoreExerciseRepository", "Documento omitido por campos faltantes: " + document.getId());
                            }
                        } catch (Exception e) {
                            Log.e("FirestoreExerciseRepository", "Error deserializando documento: " + document.getId(), e);
                            emitter.onError(new Exception("Error deserializing document: " + document.getId(), e));
                            return;
                        }
                    }
                    emitter.onSuccess(ejercicios);
                } else {
                    emitter.onError(task.getException() != null ? task.getException() :
                            new Exception("Unknown error occurred while fetching exercises."));
                }
            }).addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Single<Exercise> getEjercicioById(ExerciseId id) {
        return Single.create(emitter -> {
            if (collectionReference == null) {
                emitter.onError(new IllegalStateException("collectionReference is not initialized."));
                return;
            }

            collectionReference.document(id.toString()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Exercise ejercicio = task.getResult().toObject(Exercise.class);
                    if (ejercicio != null) {
                        ejercicio.setId(id);
                        emitter.onSuccess(ejercicio);
                    } else {
                        emitter.onError(new Exception("Exercise not found"));
                    }
                } else {
                    emitter.onError(task.getException() != null ? task.getException() :
                            new Exception("Unknown error occurred while fetching exercise."));
                }
            }).addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Completable addEjerciciotoClient(String clientId, ExerciseId id) {
        return Completable.create(emitter -> {
            if (collectionReference == null) {
                emitter.onError(new IllegalStateException("collectionReference is not initialized."));
                return;
            }

            collectionReference.document(clientId).update("ejercicios", FieldValue.arrayUnion(id.toString()))
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Completable removeEjerciciotoClient(String clientId, ExerciseId id) {
        return Completable.create(emitter -> {
            if (collectionReference == null) {
                emitter.onError(new IllegalStateException("collectionReference is not initialized."));
                return;
            }

            collectionReference.document(clientId).update("ejercicios", FieldValue.arrayRemove(id.toString()))
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Completable addExerciceToGym(String name, String description, String imageUrl) {
        return Completable.create(emitter -> {
            if (collectionReference == null) {
                emitter.onError(new IllegalStateException("collectionReference is not initialized."));
                return;
            }
            String randomId = UUID.randomUUID().toString();
            Exercise ejercicio = new Exercise(new ExerciseId(randomId), name, description, imageUrl);
            collectionReference.add(ejercicio)
                    .addOnSuccessListener(documentReference -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }
}
