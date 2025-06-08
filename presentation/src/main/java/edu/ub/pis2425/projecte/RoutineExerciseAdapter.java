package edu.ub.pis2425.projecte;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2425.projectejady0.domain.RoutineExercise;

public class RoutineExerciseAdapter extends RecyclerView.Adapter<RoutineExerciseAdapter.ExerciseViewHolder> {

    private List<RoutineExercise> exercises;

    public RoutineExerciseAdapter(List<RoutineExercise> exercises) {
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_routine_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        RoutineExercise exercise = exercises.get(position);

        holder.tvExerciseName.setText(exercise.getExercise().getNombre());
        holder.tvSets.setText(String.valueOf(exercise.getSets()));
        holder.tvReps.setText(String.valueOf(exercise.getRepetitions()));
        holder.tvWeight.setText(exercise.getWeight() + " kg");

        if (exercise.getExercise().getImagen() != null && !exercise.getExercise().getImagen().isEmpty()) {
            Picasso.get()
                    .load(exercise.getExercise().getImagen())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(holder.ivExerciseImage);
        }

        holder.btnIncreaseSets.setOnClickListener(v -> {
            int sets = exercise.getSets() + 1;
            exercise.setSets(sets);
            holder.tvSets.setText(String.valueOf(sets));
        });

        holder.btnDecreaseSets.setOnClickListener(v -> {
            int sets = Math.max(1, exercise.getSets() - 1);
            exercise.setSets(sets);
            holder.tvSets.setText(String.valueOf(sets));
        });

        holder.btnIncreaseReps.setOnClickListener(v -> {
            int reps = exercise.getRepetitions() + 1;
            exercise.setRepetitions(reps);
            holder.tvReps.setText(String.valueOf(reps));
        });

        holder.btnDecreaseReps.setOnClickListener(v -> {
            int reps = Math.max(1, exercise.getRepetitions() - 1);
            exercise.setRepetitions(reps);
            holder.tvReps.setText(String.valueOf(reps));
        });

        holder.btnIncreaseWeight.setOnClickListener(v -> {
            double weight = exercise.getWeight() + 2.5;
            exercise.setWeight(weight);
            holder.tvWeight.setText(weight + " kg");
        });

        holder.btnDecreaseWeight.setOnClickListener(v -> {
            double weight = Math.max(0, exercise.getWeight() - 2.5);
            exercise.setWeight(weight);
            holder.tvWeight.setText(weight + " kg");
        });

        holder.btnRemoveExercise.setOnClickListener(v -> {
            exercises.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, exercises.size());
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        ImageView ivExerciseImage;
        TextView tvExerciseName, tvSets, tvReps, tvWeight;
        MaterialButton btnIncreaseSets, btnDecreaseSets, btnIncreaseReps, btnDecreaseReps, btnIncreaseWeight, btnDecreaseWeight;
        Button btnRemoveExercise;


        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            ivExerciseImage = itemView.findViewById(R.id.ivExerciseImage);
            tvExerciseName = itemView.findViewById(R.id.tvExerciseName);
            tvSets = itemView.findViewById(R.id.tvSets);
            tvReps = itemView.findViewById(R.id.tvReps);
            tvWeight = itemView.findViewById(R.id.tvWeight);
            btnIncreaseSets = itemView.findViewById(R.id.btnIncreaseSets);
            btnDecreaseSets = itemView.findViewById(R.id.btnDecreaseSets);
            btnIncreaseReps = itemView.findViewById(R.id.btnIncreaseReps);
            btnDecreaseReps = itemView.findViewById(R.id.btnDecreaseReps);
            btnIncreaseWeight = itemView.findViewById(R.id.btnIncreaseWeight);
            btnDecreaseWeight = itemView.findViewById(R.id.btnDecreaseWeight);
            btnRemoveExercise = itemView.findViewById(R.id.btnRemoveExercise);
        }
    }
    public List<RoutineExercise> getCurrentExercises() {
        return new ArrayList<>(exercises);
    }
}
