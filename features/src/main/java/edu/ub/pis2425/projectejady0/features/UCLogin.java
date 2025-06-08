package edu.ub.pis2425.projectejady0.features;

import edu.ub.pis2425.projectejady0.domain.Client;
import edu.ub.pis2425.projectejady0.domain.repositories.ClientRepository;
import io.reactivex.rxjava3.core.Observable;

public class UCLogin {

    private final ClientRepository clientRepository;

    public UCLogin(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Realiza el login del cliente devolviendo un Observable.
     *
     * @param username        el nombre de usuario
     * @param enteredPassword la contraseña ingresada
     * @return Observable que emite el cliente autenticado
     */
    // Java
    public Observable<Client> logIn(String username, String enteredPassword) {
        if (username.isEmpty()) {
            return Observable.error(new Throwable("Username cannot be empty"));
        } else if (enteredPassword.isEmpty()) {
            return Observable.error(new Throwable("Password cannot be empty"));
        } else {

            return clientRepository.getClientByUsernameReact(username)
                    .flatMap(loggedClient -> {
                        if (!loggedClient.getPassword().equals(enteredPassword)) {
                            return Observable.error(new Throwable("Invalid password"));
                        } else {
                            return Observable.just(loggedClient);
                        }
                    });
        }
    }
}