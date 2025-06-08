package edu.ub.pis2425.projectejady0.data.repositories.firestore;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2425.projectejady0.data.dtos.firestore.RoutineFirestoreDto;
import edu.ub.pis2425.projectejady0.data.dtos.firestore.RoutineExerciseFirestoreDto;
import edu.ub.pis2425.projectejady0.data.mappers.DTOToDomainMapper;
import edu.ub.pis2425.projectejady0.domain.Routine;
import edu.ub.pis2425.projectejady0.domain.RoutineExercise;
import edu.ub.pis2425.projectejady0.domain.RoutineId;
import edu.ub.pis2425.projectejady0.domain.repositories.RoutineRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class FirestoreRoutineRepository implements RoutineRepository {
    private final CollectionReference routineCollection;
    private final DTOToDomainMapper mapper;
    private static FirestoreRoutineRepository instance;

    private FirestoreRoutineRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.routineCollection = db.collection("routines");
        this.mapper = new DTOToDomainMapper();
    }

    public static synchronized FirestoreRoutineRepository getInstance() {
        if (instance == null) {
            instance = new FirestoreRoutineRepository();
        }
        return instance;
    }

    @Override
    public Single<List<Routine>> getAllRoutinesForClient(String clientId) {
        return Single.create(emitter -> routineCollection.whereEqualTo("clientId", clientId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Routine> routines = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            RoutineFirestoreDto dto = document.toObject(RoutineFirestoreDto.class);
                            dto.setId(document.getId());
                            Routine routine = mapper.map(dto, Routine.class);
                            routines.add(routine);
                        }
                        emitter.onSuccess(routines);
                    } else {
                        emitter.onError(task.getException() != null ? task.getException() :
                                new Exception("Unknown error occurred while fetching routines."));
                    }
                })
                .addOnFailureListener(emitter::onError));
    }

    @Override
    public Single<RoutineId> createRoutine(String clientId, Routine routine) {
        return Single.create(emitter -> {
            RoutineFirestoreDto dto = mapper.map(routine, RoutineFirestoreDto.class);
            dto.setClientId(clientId);

            routineCollection.add(dto)
                    .addOnSuccessListener(documentReference ->
                            emitter.onSuccess(new RoutineId(documentReference.getId())))
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Completable addExerciseToRoutine(String clientId, RoutineId routineId, RoutineExercise exercise) {
        return Completable.create(emitter -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.runTransaction((Transaction.Function<Void>) transaction -> {
                        var docRef = routineCollection.document(routineId.getId());
                        var snapshot = transaction.get(docRef);

                        if (!snapshot.exists()) {
                            throw new FirebaseFirestoreException("Routine does not exist",
                                    FirebaseFirestoreException.Code.NOT_FOUND);
                        }

                        RoutineFirestoreDto dto = snapshot.toObject(RoutineFirestoreDto.class);
                        if (dto == null) {
                            throw new FirebaseFirestoreException("Failed to deserialize routine",
                                    FirebaseFirestoreException.Code.DATA_LOSS);
                        }

                        RoutineExerciseFirestoreDto exerciseDto = mapper.map(exercise, RoutineExerciseFirestoreDto.class);
                        List<RoutineExerciseFirestoreDto> updatedExercises = new ArrayList<>(dto.getExercises());
                        updatedExercises.add(exerciseDto);

                        transaction.update(docRef, "exercises", updatedExercises);
                        return null;
                    })
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Completable updateRoutine(Routine routine) {
        return Completable.create(emitter -> {
            if (routine == null || routine.getId() == null) {
                emitter.onError(new IllegalArgumentException("Routine or Routine ID cannot be null"));
                return;
            }

            RoutineFirestoreDto dto = mapper.map(routine, RoutineFirestoreDto.class);
            routineCollection.document(routine.getId().getId())
                    .set(dto)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Task<Void> deleteRoutine(String routineId) {
        return routineCollection.document(routineId).delete();
    }

    public Single<Routine> getRoutineById(String routineId) {
        return Single.create(emitter -> routineCollection.document(routineId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        RoutineFirestoreDto dto = documentSnapshot.toObject(RoutineFirestoreDto.class);
                        assert dto != null;
                        dto.setId(documentSnapshot.getId());
                        Routine routine = mapper.map(dto, Routine.class);
                        emitter.onSuccess(routine);
                    } else {
                        emitter.onError(new Exception("Routine not found"));
                    }
                })
                .addOnFailureListener(emitter::onError));
    }
}