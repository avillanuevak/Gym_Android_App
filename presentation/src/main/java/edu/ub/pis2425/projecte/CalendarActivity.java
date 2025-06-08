package edu.ub.pis2425.projecte;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.ub.pis2425.projectejady0.data.repositories.firestore.FirestoreGymClassRepository;
import edu.ub.pis2425.projectejady0.data.repositories.firestore.FirestoreRoutineRepository;
import edu.ub.pis2425.projectejady0.domain.CalendarItem;
import edu.ub.pis2425.projectejady0.domain.GymClass;
import edu.ub.pis2425.projectejady0.domain.Routine;
import edu.ub.pis2425.projectejady0.features.GetUserEnrolledClassesUseCase;
import edu.ub.pis2425.projectejady0.features.GetUserRoutinesUseCase;

/**
 * CalendarActivity displays a calendar of gym classes and routines for the logged-in user.
 * It uses a RecyclerView to show the items grouped by day of the week.
 */
public class CalendarActivity extends AppCompatActivity {
    private CalendarViewModel viewModel;
    private CalendarAdapter adapter;
    private String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Set up the toolbar and navigation
        NavigationHelper navigationHelper = new NavigationHelper(this);
        navigationHelper.setupNavigation();

        // Get client ID from session
        SessionManager sessionManager = SessionManager.getInstance(this);
        clientId = sessionManager.getUserId();

        if (clientId == null || clientId.isEmpty()) {
            Toast.makeText(this, "Error: User ID is missing. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CalendarAdapter(new HashMap<>());
        recyclerView.setAdapter(adapter);

        GetUserEnrolledClassesUseCase getUserEnrolledClassesUseCase = new GetUserEnrolledClassesUseCase(FirestoreGymClassRepository.getInstance());
        GetUserRoutinesUseCase getUserRoutinesUseCase = new GetUserRoutinesUseCase(FirestoreRoutineRepository.getInstance());
        CalendarViewModelFactory factory = new CalendarViewModelFactory(getUserEnrolledClassesUseCase, getUserRoutinesUseCase);

        viewModel = new ViewModelProvider(this, factory).get(CalendarViewModel.class);


        // Observe gym classes and update adapter with classes grouped by day and schedule.
        viewModel.getCalendarItems().observe(this, gymItems -> {
            if (gymItems != null) {
                Map<String, List<CalendarFragment.CalendarItemWithSchedule>> filteredItems = sortItemsByDay(gymItems);
                adapter.updateData(filteredItems);
            } else {
                Toast.makeText(this, "Failed to load gym classes.", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.loadUserCalendarItems(clientId);
    }

    public void reloadCalendar() {
        if (viewModel != null) {
            viewModel.loadUserCalendarItems(clientId);
        }
    }

    private Map<String, List<CalendarFragment.CalendarItemWithSchedule>> sortItemsByDay(List<CalendarFragment.CalendarItemWithSchedule> items) {
        Map<String, List<CalendarFragment.CalendarItemWithSchedule>> itemsByDay = new TreeMap<>(CalendarUtils::compareDays);

        for (CalendarFragment.CalendarItemWithSchedule itemWithSchedule : items) {
            String scheduleEntry = itemWithSchedule.getSpecificSchedule();
            if (scheduleEntry == null || scheduleEntry.trim().isEmpty()) {
                scheduleEntry = "Unknown";
            }

            String[] parts = scheduleEntry.trim().split(" ", 2);
            String day = parts.length > 0 ? CalendarUtils.capitalizeFirstLetter(parts[0]) : "Unknown";

            if (!itemsByDay.containsKey(day)) {
                itemsByDay.put(day, new ArrayList<>());
            }
            itemsByDay.get(day).add(itemWithSchedule);
        }

        return itemsByDay;
    }
}