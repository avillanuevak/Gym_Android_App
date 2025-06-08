package edu.ub.pis2425.projectejady0.data.repositories.firestore;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.*;
import edu.ub.pis2425.projectejady0.data.dtos.firestore.NutritionPlanFirestoreDto;
import edu.ub.pis2425.projectejady0.data.mappers.DTOToDomainMapper;
import edu.ub.pis2425.projectejady0.domain.NutritionPlan;
import edu.ub.pis2425.projectejady0.domain.NutritionPlanId;
import edu.ub.pis2425.projectejady0.domain.repositories.NutritionPlanRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class NutritionPlanFirestoreRepository implements NutritionPlanRepository {
    private static final String COLLECTION_NAME = "NutritionPlans";
    private static final int MAX_PLANS_PER_CLIENT = 3;
    private final FirebaseFirestore db;
    private final CollectionReference plansCollection;
    private final DTOToDomainMapper mapper;

    public NutritionPlanFirestoreRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.plansCollection = db.collection(COLLECTION_NAME);
        this.mapper = new DTOToDomainMapper();
    }

    @Override
    public Observable<NutritionPlan> getByIdReactive(NutritionPlanId planId) {
        return Observable.create(emitter -> {
            plansCollection.document(planId.toString())
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            NutritionPlanFirestoreDto dto = document.toObject(NutritionPlanFirestoreDto.class);
                            emitter.onNext(mapper.map(dto, NutritionPlan.class));
                            emitter.onComplete();
                        } else {
                            emitter.onError(new Throwable("Nutrition plan not found"));
                        }
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }


    @Override
    public Observable<List<NutritionPlan>> getByClientIdReactive(String clientId) {
        return Observable.create(emitter -> {
            plansCollection.whereEqualTo("clientId", clientId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(MAX_PLANS_PER_CLIENT)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<NutritionPlan> plans = new ArrayList<>();
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            NutritionPlanFirestoreDto dto = document.toObject(NutritionPlanFirestoreDto.class);
                            plans.add(mapper.map(dto, NutritionPlan.class));
                        }
                        emitter.onNext(plans);
                        emitter.onComplete();
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Single<NutritionPlan> getActivePlanForClient(String clientId) {
        return Single.create(emitter -> {
            plansCollection.whereEqualTo("clientId", clientId)
                    .whereEqualTo("isActive", true)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            NutritionPlanFirestoreDto dto = document.toObject(NutritionPlanFirestoreDto.class);
                            emitter.onSuccess(mapper.map(dto, NutritionPlan.class));
                        } else {
                            emitter.onError(new Throwable("No active plan found"));
                        }
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Completable save(NutritionPlan plan) {
        return Completable.create(emitter -> {
            // First check if client already has MAX_PLANS_PER_CLIENT
            plansCollection.whereEqualTo("clientId", plan.getClientId())
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (querySnapshot.size() >= MAX_PLANS_PER_CLIENT) {
                            // Delete the oldest plan
                            QueryDocumentSnapshot oldestDoc = null;
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                if (oldestDoc == null ||
                                        doc.getDate("createdAt").before(oldestDoc.getDate("createdAt"))) {
                                    oldestDoc = (QueryDocumentSnapshot) doc;
                                }
                            }

                            if (oldestDoc != null) {
                                oldestDoc.getReference().delete();
                            }
                        }

                        // Now save the new plan
                        NutritionPlanFirestoreDto dto = mapper.map(plan, NutritionPlanFirestoreDto.class);
                        if (plan.getId() == null) {
                            // New plan
                            plansCollection.add(dto)
                                    .addOnSuccessListener(ref -> emitter.onComplete())
                                    .addOnFailureListener(emitter::onError);
                        } else {
                            // Update existing plan
                            plansCollection.document(plan.getId().toString())
                                    .set(dto)
                                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                                    .addOnFailureListener(emitter::onError);
                        }
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Completable delete(NutritionPlanId planId) {
        return Completable.create(emitter -> {
            plansCollection.document(planId.toString())
                    .delete()
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Completable setActivePlan(String clientId, NutritionPlanId planId) {
        return Completable.create(emitter -> {
            // First deactivate all other plans for this client
            db.runTransaction(transaction -> {
                        // Get all plans for client
                        Query query = plansCollection.whereEqualTo("clientId", clientId);
                        QuerySnapshot querySnapshot = null;
                        try {
                            querySnapshot = Tasks.await(query.get());
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        // Update all to inactive
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            transaction.update(document.getReference(), "isActive", false);
                        }

                        // Set the specified plan to active
                        DocumentReference planRef = plansCollection.document(planId.toString());
                        transaction.update(planRef, "isActive", true);

                        return null;
                    }).addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }
}