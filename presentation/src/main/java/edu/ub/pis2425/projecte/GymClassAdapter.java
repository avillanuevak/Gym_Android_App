package edu.ub.pis2425.projecte;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.ub.pis2425.projectejady0.domain.GymClass;
import edu.ub.pis2425.projectejady0.domain.GymClassId;

public class GymClassAdapter extends RecyclerView.Adapter<GymClassAdapter.GymClassViewHolder> {
    private List<GymClass> gymClasses;
    private final String clientId;

    public GymClassAdapter(List<GymClass> gymClasses, String clientId) {
        this.gymClasses = gymClasses;
        this.clientId = clientId;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<GymClass> newGymClasses) {
        this.gymClasses = newGymClasses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GymClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gym_class, parent, false);
        return new GymClassViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GymClassViewHolder holder, @SuppressLint("RecyclerView") int position) {
        GymClass gymClass = gymClasses.get(position);
        GymClassId gymClassId = gymClass.getId();

        holder.tvClassName.setText(gymClass.getName());
        holder.tvClassInfo.setText("Instructor: " + gymClass.getInstructor() +
                ", Capacity: " + gymClass.getCurrentCapacity() + "/" + gymClass.getMaxCapacity());
        holder.tvClassTime.setText(String.join(", ", gymClass.getSchedule()));
        holder.tvClassLocation.setText(gymClass.getLocation());

        // Safety check for client ID
        if (clientId == null || clientId.isEmpty()) {
            // Get rid of buttons if clientId is not valid
            holder.btnRegister.setVisibility(View.GONE);
            holder.btnDropOut.setVisibility(View.GONE);
            return;
        }

        // Check if user is registered
        boolean isRegistered = gymClass.getRegisteredUsers() != null &&
                gymClass.getRegisteredUsers().contains(clientId);

        // Set button visibility based on registration status
        holder.btnRegister.setVisibility(isRegistered ? View.GONE : View.VISIBLE);
        holder.btnDropOut.setVisibility(isRegistered ? View.VISIBLE : View.GONE);

        // Register button
        holder.btnRegister.setOnClickListener(v -> {
            // Get ViewModel from context
            GymClassViewModel viewModel = ((GymClassActivity)holder.itemView.getContext())
                .getViewModel();

            // Use ViewModel to register
            viewModel.registerForClass(clientId, gymClassId);
        });

        // Drop Out button
        holder.btnDropOut.setOnClickListener(v -> {
            // Get ViewModel from context
            GymClassViewModel viewModel = ((GymClassActivity)holder.itemView.getContext())
                .getViewModel();

            // Use ViewModel to drop out
            viewModel.dropOutFromClass(clientId, gymClassId);
        });
    }

    @Override
    public int getItemCount() {

        int count = gymClasses != null ? gymClasses.size() : 0;
        return count;
    }

    static class GymClassViewHolder extends RecyclerView.ViewHolder {
        TextView tvClassName, tvClassInfo, tvClassTime, tvClassLocation;
        Button btnRegister, btnDropOut;

        public GymClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvClassInfo = itemView.findViewById(R.id.tvClassInfo);
            tvClassTime = itemView.findViewById(R.id.tvClassTime);
            tvClassLocation = itemView.findViewById(R.id.tvClassLocation);
            btnRegister = itemView.findViewById(R.id.btnRegister);
            btnDropOut = itemView.findViewById(R.id.btnDropOut);
        }
    }
}