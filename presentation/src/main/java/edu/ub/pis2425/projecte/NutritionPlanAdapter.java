package edu.ub.pis2425.projecte;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import edu.ub.pis2425.projectejady0.domain.NutritionPlan;

public class NutritionPlanAdapter extends RecyclerView.Adapter<NutritionPlanAdapter.PlanViewHolder> {
    private final List<NutritionPlan> plans;
    private final PlanClickListener clickListener;
    private final DeleteClickListener deleteClickListener;

    public interface PlanClickListener {
        void onPlanClick(NutritionPlan plan);
    }

    public interface DeleteClickListener {
        void onDeleteClick(NutritionPlan plan);
    }

    public NutritionPlanAdapter(List<NutritionPlan> plans, PlanClickListener clickListener, DeleteClickListener deleteClickListener) {
        this.plans = plans;
        this.clickListener = clickListener;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nutrition_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        NutritionPlan plan = plans.get(position);
        holder.tvPlanName.setText(plan.getName());
        holder.tvPlanDetails.setText(String.format(Locale.getDefault(),
                "%.0f kcal • %.0fg protein",
                plan.getTargetCalories(),
                plan.getProteinGrams()));

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onPlanClick(plan);
            }
        });
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(plan);
            }
        });
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlanName, tvPlanDetails;
        ImageView deleteButton;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlanName = itemView.findViewById(R.id.tv_plan_name);
            tvPlanDetails = itemView.findViewById(R.id.tv_plan_details);
            deleteButton = itemView.findViewById(R.id.iv_delete);
        }
    }
}