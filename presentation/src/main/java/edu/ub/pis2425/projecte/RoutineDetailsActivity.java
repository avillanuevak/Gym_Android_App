package edu.ub.pis2425.projecte;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2425.projectejady0.data.repositories.firestore.FirestoreRoutineRepository;
import edu.ub.pis2425.projectejady0.domain.Routine;
import edu.ub.pis2425.projectejady0.domain.RoutineExercise;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RoutineDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RoutineExerciseAdapter adapter;
    private TextView tvRoutineTitle;
    private Button saveChangesButton;

    // Checkboxes
    private CheckBox cbMon, cbTue, cbWed, cbThu, cbFri, cbSat, cbSun;

    private final FirestoreRoutineRepository repository = FirestoreRoutineRepository.getInstance();
    private Routine loadedRoutine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_details);

        recyclerView = findViewById(R.id.recyclerViewRoutineExercises);
        tvRoutineTitle = findViewById(R.id.tvRoutineTitle);
        saveChangesButton = findViewById(R.id.saveChangesButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cbMon = findViewById(R.id.checkboxMonday);
        cbTue = findViewById(R.id.checkboxTuesday);
        cbWed = findViewById(R.id.checkboxWednesday);
        cbThu = findViewById(R.id.checkboxThursday);
        cbFri = findViewById(R.id.checkboxFriday);
        cbSat = findViewById(R.id.checkboxSaturday);
        cbSun = findViewById(R.id.checkboxSunday);

        String routineId = getIntent().getStringExtra("routineId");
        if (routineId == null) {
            finish();
            return;
        }

        repository.getRoutineById(routineId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        routine -> {
                            loadedRoutine = routine;
                            if (routine != null && routine.getExercises() != null && !routine.getExercises().isEmpty()) {
                                adapter = new RoutineExerciseAdapter(routine.getExercises());
                                recyclerView.setAdapter(adapter);
                                tvRoutineTitle.setText("Exercises (" + routine.getExercises().size() + ")");
                            } else {
                                Toast.makeText(this, "No exercises found for this routine", Toast.LENGTH_SHORT).show();
                                tvRoutineTitle.setText("No Exercises");
                            }

                            // Inicializar los checkboxes con los días seleccionados
                            setCheckboxStates(routine.getSchedule());
                        },
                        throwable -> Toast.makeText(this, "Error loading routine: " + throwable.getMessage(), Toast.LENGTH_SHORT).show()
                );

        saveChangesButton.setOnClickListener(v -> {
            if (loadedRoutine != null && adapter != null) {
                List<RoutineExercise> updatedExercises = adapter.getCurrentExercises();
                loadedRoutine.setExercises(updatedExercises);

                List<String> updatedSchedule = getSelectedDays();
                loadedRoutine.setSchedule(updatedSchedule);

                repository.updateRoutine(loadedRoutine)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, CalendarActivity.class));
                                    finish();
                                },
                                error -> {
                                    error.printStackTrace();
                                    Toast.makeText(this, "Failed to save changes: " + error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                        );
            }
        });
    }

    private void setCheckboxStates(List<String> schedule) {
        if (schedule == null) return;
        cbMon.setChecked(schedule.contains("Monday"));
        cbTue.setChecked(schedule.contains("Tuesday"));
        cbWed.setChecked(schedule.contains("Wednesday"));
        cbThu.setChecked(schedule.contains("Thursday"));
        cbFri.setChecked(schedule.contains("Friday"));
        cbSat.setChecked(schedule.contains("Saturday"));
        cbSun.setChecked(schedule.contains("Sunday"));
    }

    private List<String> getSelectedDays() {
        List<String> days = new ArrayList<>();
        if (cbMon.isChecked()) days.add("Monday");
        if (cbTue.isChecked()) days.add("Tuesday");
        if (cbWed.isChecked()) days.add("Wednesday");
        if (cbThu.isChecked()) days.add("Thursday");
        if (cbFri.isChecked()) days.add("Friday");
        if (cbSat.isChecked()) days.add("Saturday");
        if (cbSun.isChecked()) days.add("Sunday");
        return days;
    }
}
