package edu.ub.pis2425.projecte;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2425.projectejady0.data.repositories.firestore.FirestoreRoutineRepository;
import edu.ub.pis2425.projectejady0.domain.ClientId;
import edu.ub.pis2425.projectejady0.domain.Routine;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CreateRoutineActivity extends BottomSheetDialogFragment {

    private EditText routineNameField;
    private Button saveRoutineButton;

    private CheckBox checkboxMonday, checkboxTuesday, checkboxWednesday,
            checkboxThursday, checkboxFriday, checkboxSaturday, checkboxSunday;

    private final FirestoreRoutineRepository routineRepository = FirestoreRoutineRepository.getInstance();
    private final String clientId;
    private OnRoutineCreatedListener listener;

    public interface OnRoutineCreatedListener {
        void onRoutineCreated(Routine newRoutine);
    }

    public CreateRoutineActivity(String clientId, OnRoutineCreatedListener listener) {
        this.clientId = clientId;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_routine, container, false);

        routineNameField = view.findViewById(R.id.routineNameField);
        saveRoutineButton = view.findViewById(R.id.saveRoutineButton);

        checkboxMonday = view.findViewById(R.id.checkboxMonday);
        checkboxTuesday = view.findViewById(R.id.checkboxTuesday);
        checkboxWednesday = view.findViewById(R.id.checkboxWednesday);
        checkboxThursday = view.findViewById(R.id.checkboxThursday);
        checkboxFriday = view.findViewById(R.id.checkboxFriday);
        checkboxSaturday = view.findViewById(R.id.checkboxSaturday);
        checkboxSunday = view.findViewById(R.id.checkboxSunday);

        saveRoutineButton.setOnClickListener(v -> saveRoutine(view));

        return view;
    }

    private void saveRoutine(View view) {
        String routineName = routineNameField.getText().toString().trim();
        if (routineName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a routine name", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> schedule = new ArrayList<>();
        if (checkboxMonday.isChecked()) schedule.add("Monday");
        if (checkboxTuesday.isChecked()) schedule.add("Tuesday");
        if (checkboxWednesday.isChecked()) schedule.add("Wednesday");
        if (checkboxThursday.isChecked()) schedule.add("Thursday");
        if (checkboxFriday.isChecked()) schedule.add("Friday");
        if (checkboxSaturday.isChecked()) schedule.add("Saturday");
        if (checkboxSunday.isChecked()) schedule.add("Sunday");

        if (schedule.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one day", Toast.LENGTH_SHORT).show();
            return;
        }

        Routine newRoutine = new Routine(
                null,
                routineName,
                new ArrayList<>(),
                schedule,
                new ClientId(clientId)
        );

        routineRepository.createRoutine(clientId, newRoutine)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        routineId -> {
                            newRoutine.setId(routineId); // Establecer el ID nuevo en la rutina
                            Toast.makeText(getContext(), "Routine created!", Toast.LENGTH_SHORT).show();
                            if (listener != null) {
                                listener.onRoutineCreated(newRoutine);
                            }
                            dismiss();
                        },
                        error -> Toast.makeText(getContext(), "Error creating routine", Toast.LENGTH_SHORT).show()
                );

    }
}