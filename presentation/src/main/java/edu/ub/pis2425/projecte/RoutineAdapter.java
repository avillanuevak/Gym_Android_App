package edu.ub.pis2425.projecte;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.ub.pis2425.projectejady0.domain.Routine;

public class RoutineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_CALENDAR = 0;
    private static final int VIEW_TYPE_SELECT = 1;

    private List<Routine> routines;
    private Context context;
    private OnRoutineActionListener listener;
    private boolean isCalendarMode;

    public interface OnRoutineActionListener {
        void onRoutineSelected(Routine routine);
        void onRoutineDeleted(Routine routine);
    }

    public RoutineAdapter(Context context, List<Routine> routines, OnRoutineActionListener listener, boolean isCalendarMode) {
        this.context = context;
        this.routines = routines;
        this.listener = listener;
        this.isCalendarMode = isCalendarMode;
    }

    @Override
    public int getItemViewType(int position) {
        return isCalendarMode ? VIEW_TYPE_CALENDAR : VIEW_TYPE_SELECT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CALENDAR) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_routine, parent, false);
            return new CalendarRoutineViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_routine, parent, false);
            return new SelectRoutineViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Routine routine = routines.get(position);
        if (holder instanceof CalendarRoutineViewHolder) {
            ((CalendarRoutineViewHolder) holder).bind(routine);
        } else if (holder instanceof SelectRoutineViewHolder) {
            ((SelectRoutineViewHolder) holder).bind(routine);
        }
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }

    // ViewHolder para el modo calendario
    class CalendarRoutineViewHolder extends RecyclerView.ViewHolder {
        TextView routineName, exerciseCount;
        Button btnView, btnDelete;
        ImageView ivRoutineImage;

        public CalendarRoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            routineName = itemView.findViewById(R.id.tvRoutineName);
            exerciseCount = itemView.findViewById(R.id.tvExerciseCount);
            btnView = itemView.findViewById(R.id.btnView);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            ivRoutineImage = itemView.findViewById(R.id.ivRoutineImage);
        }

        public void bind(Routine routine) {
            routineName.setText(routine.getName());
            exerciseCount.setText("Exercises: " + (routine.getExercises() != null ? routine.getExercises().size() : 0));
            ivRoutineImage.setImageResource(R.drawable.rutinas_dumbell);

            btnView.setOnClickListener(v -> {
                Intent intent = new Intent(context, RoutineDetailsActivity.class);
                intent.putExtra("routineId", routine.getId().getId());
                context.startActivity(intent);
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onRoutineDeleted(routine);
            });
        }
    }

    // ViewHolder para modo selección (ej: en BottomSheet)
    class SelectRoutineViewHolder extends RecyclerView.ViewHolder {
        TextView routineName, exerciseCount;
        Button btnAdd;
        ImageView ivRoutineImage;

        public SelectRoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            routineName = itemView.findViewById(R.id.tvRoutineName);
            exerciseCount = itemView.findViewById(R.id.tvExerciseCount);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            ivRoutineImage = itemView.findViewById(R.id.ivRoutineImage);
        }

        public void bind(Routine routine) {
            routineName.setText(routine.getName());
            exerciseCount.setText("Exercises: " + (routine.getExercises() != null ? routine.getExercises().size() : 0));
            ivRoutineImage.setImageResource(R.drawable.rutinas_dumbell);

            btnAdd.setOnClickListener(v -> {
                if (listener != null) listener.onRoutineSelected(routine);
            });
        }
    }
}
