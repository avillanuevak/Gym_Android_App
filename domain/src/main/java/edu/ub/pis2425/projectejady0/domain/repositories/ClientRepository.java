package edu.ub.pis2425.projectejady0.domain.repositories;

import edu.ub.pis2425.projectejady0.domain.Client;
import edu.ub.pis2425.projectejady0.domain.ClientId;
import edu.ub.pis2425.projectejady0.domain.ExerciseId;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface ClientRepository {

    Observable<Object> getByIdReactive(ClientId clientId);
    public Observable<Boolean> isExerciseRegistered(String clientId, ExerciseId ejercicioId);

    Completable addClientReactive(Client client);



    interface Callback<T> {
        void onSuccess(T result);
        void onError(Throwable error);
    }

    Client getClientByUsername(String username);
    void addClient(Client client, Callback<Void> callback);
    void getClientById(String id, Callback<Client> callback);
    boolean containsKey(String username);
    public Observable<Client> getByIdReactiveString(String clientId);

    public Observable<Client> getClientByUsernameReact(String username);

    /**
     * Updates user information in the repository
     *
     * @param userId The ID of the user to update
     * @param newName New username (or null to keep existing)
     * @param newEmail New email (or null to keep existing)
     * @param newPassword New password (or null to keep existing)
     * @param newUrl New photo URL (or null to remove photo)
     * @return Completable that completes when the update is done
     */
    Completable updateUserInfo(String userId, String newName, String newEmail, String newPassword, String newUrl);
}