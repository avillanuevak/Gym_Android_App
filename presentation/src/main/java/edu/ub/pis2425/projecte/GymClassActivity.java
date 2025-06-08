package edu.ub.pis2425.projecte;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.ub.pis2425.projectejady0.data.repositories.firestore.FirestoreGymClassRepository;
import edu.ub.pis2425.projectejady0.features.DropOutFromGymClassUseCase;
import edu.ub.pis2425.projectejady0.features.GetAllGymClassesUseCase;
import edu.ub.pis2425.projectejady0.features.RegisterForGymClassUseCase;

//Hola
public class GymClassActivity extends AppCompatActivity {
    private GymClassViewModel viewModel;
    private GymClassAdapter adapter;

    private final FirestoreGymClassRepository gymClassRepository = FirestoreGymClassRepository.getInstance();
    private final GetAllGymClassesUseCase getAllGymClassesUseCase = new GetAllGymClassesUseCase(gymClassRepository);
    private final RegisterForGymClassUseCase registerForGymClassUseCase = new RegisterForGymClassUseCase(gymClassRepository);
    private final DropOutFromGymClassUseCase dropOutFromGymClassUseCase = new DropOutFromGymClassUseCase(gymClassRepository);
    private String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_class);

        NavigationHelper navigationHelper = new NavigationHelper(this);
        navigationHelper.setupNavigation();

        SessionManager sessionManager = SessionManager.getInstance(this);
        clientId = sessionManager.getUserId();

        // Validate clientId
        if (clientId == null || clientId.isEmpty()) {
            Toast.makeText(this, "Error: User ID is missing. Please log in again.", Toast.LENGTH_LONG).show();
            finish(); // Return to previous activity
            return;
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with empty data
        adapter = new GymClassAdapter(new ArrayList<>(), clientId);
        recyclerView.setAdapter(adapter);

        //viewModel = new ViewModelProvider(this).get(GymClassViewModel.class);
        viewModel = new GymClassViewModel(getAllGymClassesUseCase, registerForGymClassUseCase, dropOutFromGymClassUseCase);

        // Observe gym classes and update the adapter
        viewModel.getGymClasses().observe(this, gymClasses -> {
            if (gymClasses != null) {
                adapter.updateData(gymClasses);
            } else {
                Toast.makeText(this, "Failed to load gym classes.", Toast.LENGTH_SHORT).show();
            }
        });

        // Observe registration state
        viewModel.getRegisterState().observe(this, success -> {
            if (success != null && success) {
                // Toast.makeText(this, "Successfully registered for the class!", Toast.LENGTH_SHORT).show();
                viewModel.fetchGymClasses();
            }
        });

        // Observe drop-out state
        viewModel.getDropOutState().observe(this, success -> {
            if (success != null && success) {
                // Toast.makeText(this, "Successfully dropped out from the class.", Toast.LENGTH_SHORT).show();
                viewModel.fetchGymClasses();
            }
        });

        // Fetch gym classes initially
        viewModel.fetchGymClasses();
    }

    // Pasandole el ViewModel directamente a la activity,
    // usamos directamente los LiveData de la activity
    // ... mejora el uso del patrón MVVM!
    public GymClassViewModel getViewModel() {
        return viewModel;
    }
}