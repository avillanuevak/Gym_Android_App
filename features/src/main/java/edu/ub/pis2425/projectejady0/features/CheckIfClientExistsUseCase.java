package edu.ub.pis2425.projectejady0.features;

import edu.ub.pis2425.projectejady0.domain.ClientId;
import io.reactivex.rxjava3.core.Observable;

public interface CheckIfClientExistsUseCase {
    /* EXERICICI 1 */
    Observable<Boolean> execute(ClientId clientId);
    // ...
}