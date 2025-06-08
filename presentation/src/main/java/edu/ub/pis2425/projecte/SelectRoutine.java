package edu.ub.pis2425.projecte;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import edu.ub.pis2425.projectejady0.data.repositories.firestore.FirestoreRoutineRepository;
import edu.ub.pis2425.projectejady0.domain.Routine;
import edu.ub.pis2425.projectejady0.domain.RoutineExercise;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SelectRoutine extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private Button btnCreateNewRoutine;
    private final FirestoreRoutineRepository routineRepository = FirestoreRoutineRepository.getInstance();
    private final String clientId;
    private final RoutineExercise exercise;
    private final OnRoutineSelectedListener listener;

    public interface OnRoutineSelectedListener {
        void onRoutineSelected(Routine routine);
        void onCreateNewRoutine(Routine newRoutine);
    }

    public SelectRoutine(String clientId, RoutineExercise exercise, OnRoutineSelectedListener listener) {
        this.clientId = clientId;
        this.exercise = exercise;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_routine, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewRoutines);
        btnCreateNewRoutine = view.findViewById(R.id.buttonCreateRoutine);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RoutineAdapter(requireContext(), new ArrayList<>(), new RoutineAdapter.OnRoutineActionListener() {
            @Override
            public void onRoutineSelected(Routine routine) {
                if (listener != null) {
                    listener.onRoutineSelected(routine);
                }
                dismiss();
            }

            @Override
            public void onRoutineDeleted(Routine routine) {
                // No eliminamos rutinas aquí
            }
        }, false)); // <-- false = no es calendario

        btnCreateNewRoutine.setOnClickListener(v -> {
            if (listener != null) {
                CreateRoutineActivity createRoutineBottomSheet = new CreateRoutineActivity(clientId, newRoutine -> {
                    listener.onCreateNewRoutine(newRoutine);
                });
                createRoutineBottomSheet.show(getParentFragmentManager(), createRoutineBottomSheet.getTag());
            }
        });

        loadRoutines();

        return view;
    }

    private void loadRoutines() {
        routineRepository.getAllRoutinesForClient(clientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        routines -> {
                            if (routines.isEmpty()) {
                                Toast.makeText(getContext(), "No routines yet, create one!", Toast.LENGTH_SHORT).show();
                            }
                            recyclerView.setAdapter(new RoutineAdapter(requireContext(), routines, new RoutineAdapter.OnRoutineActionListener() {
                                @Override
                                public void onRoutineSelected(Routine routine) {
                                    if (listener != null) {
                                        listener.onRoutineSelected(routine);
                                    }
                                    dismiss();
                                }

                                @Override
                                public void onRoutineDeleted(Routine routine) {
                                    // No eliminamos rutinas aquí
                                }
                            }, false)); // <-- false = modo selección
                        },
                        error -> Toast.makeText(requireContext(), "Error loading routines", Toast.LENGTH_SHORT).show()
                );
    }
}
