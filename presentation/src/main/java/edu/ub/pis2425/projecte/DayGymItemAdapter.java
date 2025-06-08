package edu.ub.pis2425.projecte;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.ub.pis2425.projectejady0.data.repositories.firestore.FirestoreRoutineRepository;
import edu.ub.pis2425.projectejady0.domain.CalendarItem;
import edu.ub.pis2425.projectejady0.domain.GymClass;
import edu.ub.pis2425.projectejady0.domain.Routine;

public class DayGymItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<CalendarFragment.CalendarItemWithSchedule> items;
    private static final int TYPE_CLASS = 0;
    private static final int TYPE_ROUTINE = 1;

    public DayGymItemAdapter(List<CalendarFragment.CalendarItemWithSchedule> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        CalendarItem item = items.get(position).getItem();
        if (item instanceof GymClass) {
            return TYPE_CLASS;
        } else if (item instanceof Routine) {
            return TYPE_ROUTINE;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CLASS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_class, parent, false);
            return new GymClassViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_routine, parent, false);
            return new RoutineViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CalendarFragment.CalendarItemWithSchedule itemWithSchedule = items.get(position);
        CalendarItem item = itemWithSchedule.getItem();

        if (holder instanceof GymClassViewHolder && item instanceof GymClass) {
            ((GymClassViewHolder) holder).bind(itemWithSchedule);
        } else if (holder instanceof RoutineViewHolder && item instanceof Routine) {
            ((RoutineViewHolder) holder).bind((Routine) item);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // --- ViewHolder para Clases ---
    static class GymClassViewHolder extends RecyclerView.ViewHolder {
        TextView tvClassName, tvClassTime, tvClassInstructor, tvClassLocation;

        public GymClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvClassTime = itemView.findViewById(R.id.tvClassTime);
            tvClassInstructor = itemView.findViewById(R.id.tvClassInstructor);
            tvClassLocation = itemView.findViewById(R.id.tvClassLocation);
        }

        public void bind(CalendarFragment.CalendarItemWithSchedule itemWithSchedule) {
            GymClass gymClass = (GymClass) itemWithSchedule.getItem();

            String[] parts = itemWithSchedule.getSpecificSchedule().split(" ", 2);
            String timeOnly = parts.length > 1 ? parts[1] : "No time available";

            tvClassName.setText(gymClass.getName());
            tvClassTime.setText("Time: " + timeOnly);
            tvClassInstructor.setText("Instructor: " + gymClass.getInstructor());
            tvClassLocation.setText("Location: " + gymClass.getLocation());
        }
    }

    // --- ViewHolder para Rutinas ---
    static class RoutineViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoutineName, tvExerciseCount;
        ImageView ivRoutineImage;
        Button btnView, btnDelete;

        public RoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoutineName = itemView.findViewById(R.id.tvRoutineName);
            tvExerciseCount = itemView.findViewById(R.id.tvExerciseCount);
            ivRoutineImage = itemView.findViewById(R.id.ivRoutineImage);
            btnView = itemView.findViewById(R.id.btnView);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(Routine routine) {
            Context context = itemView.getContext();
            tvRoutineName.setText(routine.getName());
            tvExerciseCount.setText("Exercises: " + (routine.getExercises() != null ? routine.getExercises().size() : 0));
            ivRoutineImage.setImageResource(R.drawable.rutinas_dumbell);

            btnView.setOnClickListener(v -> {
                Intent intent = new Intent(context, RoutineDetailsActivity.class);
                intent.putExtra("routineId", routine.getId().getId());
                context.startActivity(intent);
            });

            btnDelete.setOnClickListener(v -> {
                FirestoreRoutineRepository repository = FirestoreRoutineRepository.getInstance();
                repository.deleteRoutine(routine.getId().getId())
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Routine deleted successfully!", Toast.LENGTH_SHORT).show();
                            if (context instanceof CalendarActivity) {
                                ((CalendarActivity) context).reloadCalendar();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to delete routine", Toast.LENGTH_SHORT).show();
                        });
            });
        }
    }
}
