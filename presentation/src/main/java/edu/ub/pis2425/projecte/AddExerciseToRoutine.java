package edu.ub.pis2425.projecte;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import edu.ub.pis2425.projectejady0.data.repositories.firestore.FirestoreRoutineRepository;
import edu.ub.pis2425.projectejady0.domain.Exercise;
import edu.ub.pis2425.projectejady0.domain.ExerciseId;
import edu.ub.pis2425.projectejady0.domain.RoutineExercise;
import edu.ub.pis2425.projectejady0.domain.RoutineId;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddExerciseToRoutine extends BottomSheetDialogFragment {

    private EditText setsField, repsField, weightField;
    private Button saveExerciseButton;
    private final RoutineId routineId;
    private final String exerciseId;
    private final String exerciseName;

    private final String exerciseDescription;
    private final String exerciseImage;
    private final String clientId;
    private final FirestoreRoutineRepository routineRepository = FirestoreRoutineRepository.getInstance();
    private final OnExerciseAddedListener listener;

    public interface OnExerciseAddedListener {
        void onExerciseAdded();
    }
    public AddExerciseToRoutine(RoutineId routineId, String exerciseId, String exerciseName, String exerciseDescription, String exerciseImage, String clientId, OnExerciseAddedListener listener) {
        this.routineId = routineId;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.exerciseDescription = exerciseDescription;
        this.exerciseImage = exerciseImage;
        this.clientId = clientId;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_exercise_to_routine, container, false);

        setsField = view.findViewById(R.id.setsField);
        repsField = view.findViewById(R.id.repsField);
        weightField = view.findViewById(R.id.weightField);
        saveExerciseButton = view.findViewById(R.id.saveExerciseButton);

        saveExerciseButton.setOnClickListener(v -> saveExercise());

        return view;
    }

    private void saveExercise() {
        try {
            int sets = Integer.parseInt(setsField.getText().toString());
            int reps = Integer.parseInt(repsField.getText().toString());
            double weight = Double.parseDouble(weightField.getText().toString());

            Exercise exercise = new Exercise(
                    new ExerciseId(exerciseId),
                    exerciseName,
                    exerciseDescription,
                    exerciseImage
            );

            RoutineExercise routineExercise = new RoutineExercise(
                    null,
                    exercise,
                    sets,
                    reps,
                    weight
            );

            routineRepository.addExerciseToRoutine(clientId, routineId, routineExercise)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            () -> {
                                Toast.makeText(getContext(), "Exercise added to routine!", Toast.LENGTH_SHORT).show();
                                if (listener != null) {
                                    listener.onExerciseAdded();
                                }
                                dismiss();
                            },
                            error -> {
                                error.printStackTrace();
                                Toast.makeText(getContext(), "Error al añadir el ejercicio.", Toast.LENGTH_SHORT).show();
                            }
                    );
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
        }
    }
}
