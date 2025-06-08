package edu.ub.pis2425.projectejady0.data.repositories.firestore;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2425.projectejady0.data.dtos.firestore.GymClassFirestoreDto;
import edu.ub.pis2425.projectejady0.data.mappers.DTOToDomainMapper;
import edu.ub.pis2425.projectejady0.domain.GymClass;
import edu.ub.pis2425.projectejady0.domain.GymClassId;
import edu.ub.pis2425.projectejady0.domain.repositories.GymClassRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class FirestoreGymClassRepository implements GymClassRepository {
    private final CollectionReference gymClassCollection;
    private final DTOToDomainMapper mapper;
    private static FirestoreGymClassRepository instance;

    // Constructor privado para Singleton
    private FirestoreGymClassRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.gymClassCollection = db.collection("gym_classes_with_users");
        this.mapper = new DTOToDomainMapper();
    }

    // Método público para obtener la única instancia
    public static synchronized FirestoreGymClassRepository getInstance() {
        if (instance == null) {
            instance = new FirestoreGymClassRepository();
        }
        return instance;
    }


    @Override
    public Single<List<GymClass>> getAllGymClasses() {
        return Single.create(emitter -> {
            if (gymClassCollection == null) {
                emitter.onError(new IllegalStateException("gymClassCollection is not initialized."));
                return;
            }

            gymClassCollection.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    List<GymClass> gymClasses = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        try {
                            GymClassFirestoreDto dto = document.toObject(GymClassFirestoreDto.class);
                            GymClass gymClass = mapper.map(dto, GymClass.class);
                            gymClass.setId(new GymClassId(document.getId()));

                            // Ensure registeredUsers is properly set from the Firestore document
                            List<String> registeredUsers = (List<String>) document.get("registeredUsers");
                            if (registeredUsers != null) {
                                gymClass.setRegisteredUsers(registeredUsers);
                            }

                            gymClasses.add(gymClass);
                        } catch (Exception e) {
                            emitter.onError(new Exception("Error deserializing document: " + document.getId(), e));
                            return;
                        }
                    }
                    emitter.onSuccess(gymClasses);
                } else {
                    emitter.onError(task.getException() != null ? task.getException() :
                            new Exception("Unknown error occurred while fetching gym classes."));
                }
            }).addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Completable registerClientForClass(String clientId, GymClassId gymClassId) {
        return Completable.create(emitter -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.runTransaction((Transaction.Function<Void>) transaction -> {
                        var docRef = gymClassCollection.document(gymClassId.getId());
                        var snapshot = transaction.get(docRef);
                        if (!snapshot.exists()) {
                            throw new FirebaseFirestoreException("Gym class does not exist",
                                    FirebaseFirestoreException.Code.NOT_FOUND);
                        }

                        GymClassFirestoreDto dto = snapshot.toObject(GymClassFirestoreDto.class);
                        if (dto == null) {
                            throw new FirebaseFirestoreException("Failed to deserialize gym class",
                                    FirebaseFirestoreException.Code.DATA_LOSS);
                        }

                        GymClass gymClass = mapper.map(dto, GymClass.class);
                        gymClass.setId(gymClassId);

                        // Ensure registeredUsers is properly set from the Firestore document
                        List<String> registeredUsers = (List<String>) snapshot.get("registeredUsers");
                        if (registeredUsers != null) {
                            gymClass.setRegisteredUsers(registeredUsers);
                        }

                        if (gymClass.getCurrentCapacity() >= gymClass.getMaxCapacity()) {
                            throw new FirebaseFirestoreException("Gym class is full",
                                    FirebaseFirestoreException.Code.ABORTED);
                        }

                        // Check if the user is already registered
                        if (gymClass.getRegisteredUsers() != null && gymClass.getRegisteredUsers().contains(clientId)) {
                            throw new FirebaseFirestoreException("Client already registered for this class",
                                    FirebaseFirestoreException.Code.ALREADY_EXISTS);
                        }

                        transaction.update(docRef, "registeredUsers", FieldValue.arrayUnion(clientId));
                        transaction.update(docRef, "currentCapacity", gymClass.getCurrentCapacity() + 1);
                        return null;
                    }).addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Completable dropOutClientFromClass(String clientId, GymClassId gymClassId) {
        return Completable.create(emitter -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.runTransaction((Transaction.Function<Void>) transaction -> {
                        var docRef = gymClassCollection.document(gymClassId.getId());
                        var snapshot = transaction.get(docRef);
                        if (!snapshot.exists()) {
                            throw new FirebaseFirestoreException("Gym class does not exist",
                                    FirebaseFirestoreException.Code.NOT_FOUND);
                        }

                        GymClassFirestoreDto dto = snapshot.toObject(GymClassFirestoreDto.class);
                        if (dto == null) {
                            throw new FirebaseFirestoreException("Failed to deserialize gym class",
                                    FirebaseFirestoreException.Code.DATA_LOSS);
                        }

                        GymClass gymClass = mapper.map(dto, GymClass.class);
                        gymClass.setId(gymClassId);

                        // Ensure registeredUsers is properly set from the Firestore document
                        List<String> registeredUsers = (List<String>) snapshot.get("registeredUsers");
                        if (registeredUsers != null) {
                            gymClass.setRegisteredUsers(registeredUsers);
                        }

                        // Check if the user is registered before dropping out
                        if (gymClass.getRegisteredUsers() == null || !gymClass.getRegisteredUsers().contains(clientId)) {
                            throw new FirebaseFirestoreException("Client is not registered for this class",
                                    FirebaseFirestoreException.Code.FAILED_PRECONDITION);
                        }

                        transaction.update(docRef, "registeredUsers", FieldValue.arrayRemove(clientId));
                        transaction.update(docRef, "currentCapacity", Math.max(0, gymClass.getCurrentCapacity() - 1));
                        return null;
                    }).addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }
}