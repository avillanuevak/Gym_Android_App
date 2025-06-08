package edu.ub.pis2425.projectejady0.features;

import edu.ub.pis2425.projectejady0.domain.Client;
import edu.ub.pis2425.projectejady0.domain.ClientId;
import edu.ub.pis2425.projectejady0.domain.repositories.ClientRepository;
import io.reactivex.rxjava3.core.Completable;

public class UCSignUp {

    private final ClientRepository clientRepository;
    private final CheckIfClientExistsUseCase checkIfClientExistsUseCase;


    public UCSignUp(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
        this.checkIfClientExistsUseCase = new CheckIfClientExistsUseCaseImpl(clientRepository);
    }
    /**
     * Empty constructor
     */


    /**
     * Sign up a new client.
     *
     * @param username             the username
     * @param email                the email
     * @param password             the password
     * @param passwordConfirmation the password confirmation
     */
    public Completable signUp(
            String username,
            String email,
            String password,
            String passwordConfirmation
    ) {
        return checkIfClientExistsUseCase.execute(new ClientId(username))
                .map(exists -> {
                    if (exists) {
                        throw new IllegalArgumentException("Client already exists! Please log in.");
                    }
                    return new Client(new ClientId(username), username, email, password, (String) null);
                })
                .concatMapCompletable(client -> {
                    return clientRepository.addClientReactive(client);
                });
    }
}
