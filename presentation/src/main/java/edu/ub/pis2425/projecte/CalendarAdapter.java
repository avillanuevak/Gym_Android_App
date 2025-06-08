package edu.ub.pis2425.projecte;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* CalendarAdapter is a RecyclerView adapter that displays a list of gym classes and routines grouped by day.
 * Each day has its own inner RecyclerView to display the classes scheduled for that day.
 */
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private Map<String, List<CalendarFragment.CalendarItemWithSchedule>> itemsByDay;
    private List<String> days;

    public CalendarAdapter(Map<String, List<CalendarFragment.CalendarItemWithSchedule>> itemsByDay) {
        this.itemsByDay = itemsByDay;
        this.days = new ArrayList<>(itemsByDay.keySet());
        this.days.sort(CalendarUtils::compareDays);
    }

    // This method updates the data in the adapter and notifies the RecyclerView to refresh its views.
    public void updateData(Map<String, List<CalendarFragment.CalendarItemWithSchedule>> newClassesByDay) {
        this.itemsByDay = newClassesByDay;
        this.days = new ArrayList<>(newClassesByDay.keySet());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String day = days.get(position);
        holder.tvDay.setText(day);
        List<CalendarFragment.CalendarItemWithSchedule> gymClasses = itemsByDay.get(day);

        // Use inner recycler view to display gym classes and routines for this day
        DayGymItemAdapter innerAdapter = new DayGymItemAdapter(gymClasses);
        holder.rvClasses.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rvClasses.setAdapter(innerAdapter);
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    // ViewHolder for a day entry in the calendar.
    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        RecyclerView rvClasses;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            rvClasses = itemView.findViewById(R.id.rvClasses);
        }
    }
}