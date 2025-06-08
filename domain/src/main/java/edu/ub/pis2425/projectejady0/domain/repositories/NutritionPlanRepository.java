package edu.ub.pis2425.projectejady0.domain.repositories;

import edu.ub.pis2425.projectejady0.domain.NutritionPlan;
import edu.ub.pis2425.projectejady0.domain.NutritionPlanId;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

public interface NutritionPlanRepository {
    Observable<NutritionPlan> getByIdReactive(NutritionPlanId planId);
    Observable<List<NutritionPlan>> getByClientIdReactive(String clientId);
    Single<NutritionPlan> getActivePlanForClient(String clientId);
    Completable save(NutritionPlan plan);
    Completable delete(NutritionPlanId planId);
    Completable setActivePlan(String clientId, NutritionPlanId planId);

    interface Callback<T> {
        void onSuccess(T result);
        void onError(Throwable error);
    }
}