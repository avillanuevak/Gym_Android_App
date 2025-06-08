package edu.ub.pis2425.projecte;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import edu.ub.pis2425.projectejady0.data.repositories.firestore.ClientFirestoreRepository;
import edu.ub.pis2425.projectejady0.data.repositories.firestore.FirestoreExerciseRepository;
import edu.ub.pis2425.projectejady0.data.repositories.firestore.FirestoreRoutineRepository;
import edu.ub.pis2425.projectejady0.features.AddNewExerciceUseCase;
import edu.ub.pis2425.projectejady0.features.GetAllGymExercicesUseCase;
import edu.ub.pis2425.projectejady0.features.addNewExerciceToGymUseCase;

public class EjerciciosActivity extends AppCompatActivity {

    private EjerciciosViewModel viewModel;
    private String clientId;
    private EjerciciosAdapter adapter;

    private ClientFirestoreRepository clientFirestoreRepository;
    private final FirestoreExerciseRepository firestoreExerciseRepository= FirestoreExerciseRepository.getInstance();
    private final FirestoreRoutineRepository firestoreRoutineRepository = FirestoreRoutineRepository.getInstance();
    private final GetAllGymExercicesUseCase getAllGymExercicesUseCase = new GetAllGymExercicesUseCase(firestoreExerciseRepository);
    private final addNewExerciceToGymUseCase AddNewExerciceToGymUseCase = new addNewExerciceToGymUseCase(firestoreExerciseRepository);
    private final AddNewExerciceUseCase AddNewExerciceUseCase = new AddNewExerciceUseCase(firestoreRoutineRepository);

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicios);

        NavigationHelper navigationHelper = new NavigationHelper(this);
        navigationHelper.setupNavigation();

        // Validar clientId
        validateClientId();

        // Inicializar listeners y ViewModel
        initWidgetListeners();
        initViewModel();
    }

    private void validateClientId() {
        SessionManager sessionManager = SessionManager.getInstance(this);
        clientId = sessionManager.getUserId();

        if (clientId == null || clientId.isEmpty()) {
            Toast.makeText(this, "Error: User ID is missing. Please log in again.", Toast.LENGTH_LONG).show();
            finish(); // Return to previous activity
        }
    }

    private void initWidgetListeners() {
        FloatingActionButton floatingActionButton = findViewById(R.id.fabAddExercise);
        LinearLayout formAddExercise = findViewById(R.id.formAddExercise);
        floatingActionButton.setOnClickListener(view -> {
            if (formAddExercise.getVisibility() == LinearLayout.VISIBLE) {
                formAddExercise.setVisibility(LinearLayout.GONE);
            } else {
                formAddExercise.setVisibility(LinearLayout.VISIBLE);
            }
        });

        Button buttonLoadImage = findViewById(R.id.buttonUploadPhoto);
        buttonLoadImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerViewEjercicios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EjerciciosAdapter(new ArrayList<>(), clientId, clientFirestoreRepository);
        recyclerView.setAdapter(adapter);

        Button buttonSaveExercise = findViewById(R.id.buttonSaveExercise);
        buttonSaveExercise.setOnClickListener(view -> {
            String name = ((EditText) findViewById(R.id.editTextExerciseName)).getText().toString();
            String description = ((EditText) findViewById(R.id.editTextExerciseDescription)).getText().toString();
            viewModel.saveExercise(name, description, selectedImageUri);
        });




    }

    private void initViewModel() {
        viewModel = new EjerciciosViewModel(AddNewExerciceToGymUseCase, getAllGymExercicesUseCase, AddNewExerciceUseCase);
        initObservers();
    }

    private void initObservers() {
        viewModel.getGymExercices().observe(this, ejercicios -> {
            if (ejercicios != null) {
                adapter.updateData(ejercicios);
            } else {
                Toast.makeText(this, "Failed to load gym classes.", Toast.LENGTH_SHORT).show();
            }
            if (viewModel == null) {
                Log.d("EjerciciosActivity", "ViewModel instance is null");
            } else {
                Log.d("EjerciciosActivity", "ViewModel instance: " + viewModel.toString());
            }
        });

        viewModel.fetchGymExercices();

        viewModel.getSaveExerciseResult().observe(this, result -> {
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            if (result.equals("Ejercicio guardado con éxito.")) {
                LinearLayout formAddExercise = findViewById(R.id.formAddExercise);
                formAddExercise.setVisibility(View.GONE);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            // Handle the image URI as needed
            // For example, you can set it to an ImageView or upload it to Firestore
            ImageView imageView = findViewById(R.id.imagePreview);
            imageView.setVisibility(View.VISIBLE); // Asegúrate de que el ImageView sea visible
            imageView.setImageURI(selectedImageUri);
        }
    }
    public EjerciciosViewModel getViewModel() {
        return viewModel;
    }
}

