package edu.ub.pis2425.projecte;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.ub.pis2425.projectejady0.data.repositories.firestore.ClientFirestoreRepository;
import edu.ub.pis2425.projectejady0.domain.Exercise;
import edu.ub.pis2425.projectejady0.domain.ExerciseId;
import edu.ub.pis2425.projectejady0.domain.RoutineExercise;

public class EjerciciosAdapter extends RecyclerView.Adapter<EjerciciosAdapter.EjerciciosViewHolder> {

    private List<Exercise> ejerciciosList;
    private final String clientId;
    private final ClientFirestoreRepository clientFirestoreRepository;

    public EjerciciosAdapter(List<Exercise> ejerciciosList, String clientId, ClientFirestoreRepository clientFirestoreRepository) {
        this.ejerciciosList = ejerciciosList;
        this.clientId = clientId;
        this.clientFirestoreRepository = clientFirestoreRepository;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Exercise> newEjerciciosList) {
        this.ejerciciosList = newEjerciciosList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EjerciciosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercice, parent, false);
        return new EjerciciosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EjerciciosViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Exercise ejercicio = ejerciciosList.get(position);

        holder.tvExName.setText(ejercicio.getNombre());
        holder.tvExDescription.setText(ejercicio.getDescripcion());

        if (ejercicio.getImagen() != null && !ejercicio.getImagen().isEmpty()) {
            Log.e("EjerciciosAdapter", "URL de la imagen: " + ejercicio.getImagen());
            Picasso.get()
                    .load(ejercicio.getImagen())
                    .into(holder.ivExImage);
        }

        if (clientId == null || clientId.isEmpty()) {
            holder.btnAddToRoutine.setVisibility(View.GONE);
            holder.btnRemoveFromRoutine.setVisibility(View.GONE);
            return;
        }
        // Check if the exercise is already registered
        holder.btnAddToRoutine.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            if (context instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) context;

                Exercise fullExercise = new Exercise(
                        new ExerciseId(ejercicio.getId().getId()),
                        ejercicio.getNombre(),
                        ejercicio.getDescripcion(),
                        ejercicio.getImagen()
                );

                RoutineExercise routineExercise = new RoutineExercise(
                        null,
                        fullExercise,
                        0,
                        0,
                        0.0
                );

                SelectRoutine selectRoutineBottomSheet = new SelectRoutine(clientId, routineExercise, new SelectRoutine.OnRoutineSelectedListener() {
                    @Override
                    public void onRoutineSelected(edu.ub.pis2425.projectejady0.domain.Routine routine) {
                        AddExerciseToRoutine addExerciseBottomSheet = new AddExerciseToRoutine(
                                routine.getId(),
                                fullExercise.getId().getId(),
                                fullExercise.getNombre(),
                                fullExercise.getDescripcion(),
                                fullExercise.getImagen(),
                                clientId,
                                () -> {
                                    if (activity instanceof CalendarActivity) {
                                        ((CalendarActivity) activity).reloadCalendar();
                                    }
                                }
                        );
                        addExerciseBottomSheet.show(activity.getSupportFragmentManager(), addExerciseBottomSheet.getTag());
                    }

                    @Override
                    public void onCreateNewRoutine(edu.ub.pis2425.projectejady0.domain.Routine newRoutine) {
                        if (newRoutine != null) {
                            AddExerciseToRoutine addExerciseBottomSheet = new AddExerciseToRoutine(
                                    newRoutine.getId(),
                                    fullExercise.getId().getId(),
                                    fullExercise.getNombre(),
                                    fullExercise.getDescripcion(),
                                    fullExercise.getImagen(),
                                    clientId,
                                    () -> {
                                        if (activity instanceof CalendarActivity) {
                                            ((CalendarActivity) activity).reloadCalendar();
                                        }
                                    }
                            );
                            addExerciseBottomSheet.show(activity.getSupportFragmentManager(), addExerciseBottomSheet.getTag());
                        }
                    }
                });

                selectRoutineBottomSheet.show(activity.getSupportFragmentManager(), selectRoutineBottomSheet.getTag());
            }
        });

        holder.btnRemoveFromRoutine.setOnClickListener(v -> {
            if (holder.itemView.getContext() instanceof EjerciciosActivity) {
                EjerciciosViewModel ejerciciosViewModel = ((EjerciciosActivity) holder.itemView.getContext()).getViewModel();
                ejerciciosViewModel.DropOutExerciceUseCase(ejercicio.getId(), clientId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ejerciciosList != null ? ejerciciosList.size() : 0;
    }

    static class EjerciciosViewHolder extends RecyclerView.ViewHolder {
        TextView tvExName, tvExDescription;
        Button btnAddToRoutine, btnRemoveFromRoutine;
        ImageView ivExImage;

        public EjerciciosViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExName = itemView.findViewById(R.id.tvEjercicioName);
            tvExDescription = itemView.findViewById(R.id.tvDescriptionExercice);
            btnAddToRoutine = itemView.findViewById(R.id.btnAddExericise);
            btnRemoveFromRoutine = itemView.findViewById(R.id.btnRemoveExericise);
            ivExImage = itemView.findViewById(R.id.imagenExercice);
        }
    }
}
