package edu.ub.pis2425.projecte;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import edu.ub.pis2425.projecte.databinding.ActivityLogInBinding;
import edu.ub.pis2425.projectejady0.data.repositories.firestore.ClientFirestoreRepository;
import edu.ub.pis2425.projectejady0.domain.repositories.ClientRepository;
import edu.ub.pis2425.projectejady0.features.UCLogin;

public class LogInActivity extends AppCompatActivity {
    /* Attributes */
    private LogInViewModel logInViewModel;

    private final ClientRepository clientRepository = ClientFirestoreRepository.getInstance();
    private final UCLogin ucLogIn = new UCLogin(clientRepository);
    /* View binding */
    private ActivityLogInBinding binding;

    /**
     * Called when the activity is being created.
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Set view binding */
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* Initializations */
        initWidgetListeners();
        initViewModel();
    }

    /**
     * Initialize the listeners of the widgets.
     */
    private void initWidgetListeners() {
        binding.btnLogIn.setOnClickListener(ignoredView -> {
            // Delegate the log-in logic to the viewmodel
            logInViewModel.logIn(
                    String.valueOf(binding.etLoginUsername.getText()),
                    String.valueOf(binding.etLoginPassword.getText())
            );
        });

        binding.btnSignUp.setOnClickListener(ignoredView -> {
            // Start the sign-up activity
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
        binding.btnHelp.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Contacto Gimnasio")
                    .setMessage("Correo: jayd0@gmail.com\nTeléfono: 948765532\nUbicación: Carrer Mas d'en Serra, 45")
                    .setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    /**
     * Initialize the viewmodel and its observers.
     */
    private void initViewModel() {
        /* Init viewmodel */
        //logInViewModel = new ViewModelProvider(this).get(LogInViewModel.class);
        logInViewModel = new LogInViewModel(ucLogIn);

        initObservers();
    }

    /**
     * Initialize the observers of the viewmodel.
     */
    private void initObservers() {
        /* Observe the login state */
        logInViewModel.getLogInState().observe(this, logInState -> {
            // Whenever there's a change in the login state of the viewmodel
            switch (logInState.getStatus()) {
                case LOADING:
                    binding.btnLogIn.setEnabled(false);
                    break;
                case SUCCESS:
                    assert logInState.getData() != null;
                    String userId = logInState.getData().getId().toString();

                    // Mejor guardar el id en el SessionManager que pasarlo por el Bundle.
                    // Pasarlo de una activity a otra daba problemas con el ciclo de vida.
                    SessionManager sessionManager = SessionManager.getInstance(this);
                    sessionManager.createLoginSession(userId);

                    Intent intent = new Intent(this, GymClassActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case ERROR:
                    assert logInState.getError() != null;
                    String errorMessage = logInState.getError().getMessage();
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    binding.btnLogIn.setEnabled(true);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + logInState.getStatus());
            }
        });
    }
}
