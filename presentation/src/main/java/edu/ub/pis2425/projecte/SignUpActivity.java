package edu.ub.pis2425.projecte;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;

import edu.ub.pis2425.projecte.databinding.ActivitySignUpBinding;
import edu.ub.pis2425.projectejady0.data.repositories.firestore.ClientFirestoreRepository;
import edu.ub.pis2425.projectejady0.domain.repositories.ClientRepository;
import edu.ub.pis2425.projectejady0.features.UCSignUp;

public class SignUpActivity extends AppCompatActivity {
    /* Attributes */

    /* ViewModel */
    private SignUpViewModel signUpViewModel;

    private final ClientRepository clientRepository = ClientFirestoreRepository.getInstance();
    private final UCSignUp ucSignUp = new UCSignUp(clientRepository);

    /* View binding */
    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initWidgetListeners();
        initViewModel();
        String imageUrl = "https://xfit.com.es/wp-content/uploads/2020/04/Los_beneficios_de_los_gimnasios_de_pesas.jpg";
        ImageView backgroundImage = findViewById(R.id.backgroundImage);
        Picasso.get().load(imageUrl).into(backgroundImage);

    }
    /**
     * Initialize the listeners of the widgets.
     */
    private void initWidgetListeners() {
        binding.btnSignUp.setOnClickListener(ignoredView -> {
            signUpViewModel.signUp(
                    String.valueOf(binding.etUsername.getText()),
                    String.valueOf(binding.etEmail.getText()),
                    String.valueOf(binding.etPassword.getText()),
                    String.valueOf(binding.etConfirmPassword.getText())
            );
        });
    }


    /**
     * Initialize the viewmodel and its observers.
     */
    private void initViewModel() {
        /*
        signUpViewModel = new ViewModelProvider(
                this
        ).get(SignUpViewModel.class);
        */
        signUpViewModel = new SignUpViewModel(ucSignUp);

        initObservers();
    }
    /**
     * Initialize the observers of the viewmodel.
     */
    private void initObservers() {
        signUpViewModel.getSignUpState().observe(this, state -> {
            switch (state.getStatus()) {
                case COMPLETE:
                    finish();
                    break;
                case ERROR:
                    Toast.makeText(this, state.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}