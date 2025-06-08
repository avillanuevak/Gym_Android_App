package edu.ub.pis2425.projectejady0.features;

import edu.ub.pis2425.projectejady0.domain.repositories.ClientRepository;
import io.reactivex.rxjava3.core.Completable;

public class UCChangeClientInfo {
    private final ClientRepository clientRepository;

    public UCChangeClientInfo(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Completable execute(String userId, String newName, String newEmail, String newPassword, String newUrl) {
        return clientRepository.updateUserInfo(userId, newName, newEmail, newPassword, newUrl);
    }
    public ClientRepository getClientRepository() { return clientRepository; }
}