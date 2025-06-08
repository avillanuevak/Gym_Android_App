// Java
package edu.ub.pis2425.projecte;

import androidx.lifecycle.ViewModel;
import edu.ub.pis2425.projectejady0.data.repositories.firestore.ClientFirestoreRepository;
import edu.ub.pis2425.projectejady0.domain.repositories.ClientRepository;
import edu.ub.pis2425.projectejady0.features.UCLogin;
import edu.ub.pis2425.projectejady0.domain.Client;
import edu.ub.pis2425.projecte.utils.livedata.StateLiveData;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LogInViewModel extends ViewModel {

    /* Model */

    private final UCLogin ucLogin;
    /* LiveData */
    private final StateLiveData<Client> logInState;
    /* Disposable */
    private final CompositeDisposable disposables = new CompositeDisposable();

    /* Constructor */
    public LogInViewModel(UCLogin ucLogin) {
        this.ucLogin = ucLogin;
        logInState = new StateLiveData<>();
    }

    /**
     * Returns the state of the log-in
     * @return the state of the log-in
     */
    public StateLiveData<Client> getLogInState() {
        return logInState;
    }

    /**
     * Logs in the user using reactive approach
     * @param username the username
     * @param password the password
     */
    public void logIn(String username, String password) {
        logInState.postLoading();
        disposables.add(
                ucLogin.logIn(username, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                client -> logInState.postSuccess(client),
                                throwable -> logInState.postError(throwable)
                        )
        );
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}