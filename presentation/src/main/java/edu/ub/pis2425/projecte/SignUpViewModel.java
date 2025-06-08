package edu.ub.pis2425.projecte;

import androidx.lifecycle.ViewModel;

import edu.ub.pis2425.projectejady0.data.repositories.firestore.ClientFirestoreRepository;
import edu.ub.pis2425.projectejady0.domain.repositories.ClientRepository;
import edu.ub.pis2425.projectejady0.features.UCSignUp;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SignUpViewModel extends ViewModel {

    private final UCSignUp ucSignUp;


    private final CompositeDisposable disposables;

    private final StateLiveData<Void> signUpState;

    public SignUpViewModel(UCSignUp ucSignUp) {
        super();
        this.ucSignUp = ucSignUp;
        signUpState = new StateLiveData<>();
        disposables = new CompositeDisposable();
    }
    public StateLiveData<Void> getSignUpState() {
        return signUpState;
    }

    /**
     * Signs up the user
     * @param username the username
     * @param email the email
     * @param password the password
     * @param passwordConfirmation the password confirmation
     */
    public void signUp(String username, String email, String password, String passwordConfirmation) {
        Disposable d = ucSignUp
                .signUp(username, email, password, passwordConfirmation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            signUpState.postComplete();
                        },
                        throwable -> {
                            signUpState.postError(throwable);
                        }
                );

        disposables.add(d);
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}


