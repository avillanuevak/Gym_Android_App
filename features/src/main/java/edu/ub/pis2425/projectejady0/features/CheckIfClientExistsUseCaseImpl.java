package edu.ub.pis2425.projectejady0.features;

import edu.ub.pis2425.projectejady0.domain.repositories.ClientRepository;
import io.reactivex.rxjava3.core.Observable;
import edu.ub.pis2425.projectejady0.domain.ClientId;

public class CheckIfClientExistsUseCaseImpl implements CheckIfClientExistsUseCase {
    /* EXERICICI 1 */
    private final ClientRepository clientRepository;

    public CheckIfClientExistsUseCaseImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Observable<Boolean> execute(ClientId clientId) {
        return clientRepository.getByIdReactive(clientId)
                .map(ignoredClient -> true)
                .onErrorReturnItem(false);
    }
    // ...
}
