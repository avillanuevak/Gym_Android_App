package edu.ub.pis2425.projecte;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ub.pis2425.projectejady0.data.repositories.firestore.FirestoreRoutineRepository;
import edu.ub.pis2425.projectejady0.domain.CalendarItem;
import edu.ub.pis2425.projectejady0.features.GetUserEnrolledClassesUseCase;
import edu.ub.pis2425.projectejady0.data.repositories.firestore.FirestoreGymClassRepository;
import edu.ub.pis2425.projectejady0.features.GetUserRoutinesUseCase;

/*
 * CalendarFragment is a Fragment that displays a calendar of gym classes and routines.
 * It uses a RecyclerView to show the items, and a ViewModel to manage the data.
 */

public class CalendarFragment extends Fragment {
    private RecyclerView recyclerView;
    private CalendarAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvNoClasses;
    private CalendarViewModel viewModel;
    private String clientId;

    public CalendarFragment() {}

   // Factory method to create a new instance of this fragment using the provided clientId.
    public static CalendarFragment newInstance(String clientId) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString("clientId", clientId);
        fragment.setArguments(args);
        return fragment;
    }

    // This method is called when the fragment is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clientId = getArguments().getString("clientId");
        }
        // Initialize the use cases and ViewModel
        viewModel = new CalendarViewModel(
                new GetUserEnrolledClassesUseCase(FirestoreGymClassRepository.getInstance()),
                new GetUserRoutinesUseCase(FirestoreRoutineRepository.getInstance())
        );
    }

    // This method is called to create the view hierarchy associated with the fragment.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        recyclerView = view.findViewById(R.id.rvCalendar);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoClasses = view.findViewById(R.id.tvNoClasses);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize with empty data
        adapter = new CalendarAdapter(new HashMap<>());
        recyclerView.setAdapter(adapter);

        setupObservers();
        loadUserItems();

        return view;
    }

    // This method is called to set up observers for the ViewModel.
    private void setupObservers() {
        viewModel.getCalendarItems().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                // Sort the items by day using shared utility
                Map<String, List<CalendarItemWithSchedule>> itemsByDay =
                        CalendarUtils.sortItemsByDay(result);

                if (itemsByDay.isEmpty()) {
                    showNoItemsMessage();
                } else {
                    updateUI(itemsByDay);
                }
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                showErrorMessage(error);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) {
                recyclerView.setVisibility(View.GONE);
                tvNoClasses.setVisibility(View.GONE);
            }
        });
    }

    // This method is called to load the user's classes and routines.
    private void loadUserItems() {
        if (clientId != null && !clientId.isEmpty()) {
            viewModel.loadUserCalendarItems(clientId);
        } else {
            showErrorMessage("Client ID is missing");
        }
    }

    // Inner class to store a CalendarItem and its specific schedule entry.
    static class CalendarItemWithSchedule {
        private final CalendarItem item;
        private final String specificSchedule;

        public CalendarItemWithSchedule(CalendarItem item, String specificSchedule) {
            this.item = item;
            this.specificSchedule = specificSchedule;
        }

        public CalendarItem getItem() {
            return item;
        }

        public String getSpecificSchedule() {
            return specificSchedule;
        }
    }

    // This method is called to update the UI with the sorted items.
    private void updateUI(Map<String, List<CalendarItemWithSchedule>> itemsByDay) {
        adapter.updateData(itemsByDay);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        tvNoClasses.setVisibility(View.GONE);
    }

    // This method is called to show a message when there are no items.
    private void showNoItemsMessage() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        tvNoClasses.setVisibility(View.VISIBLE);
    }

    // This method is called to show an error message.
    private void showErrorMessage(String message) {
        progressBar.setVisibility(View.GONE);
        tvNoClasses.setText(getString(R.string.error_loading_classes, message));
        tvNoClasses.setVisibility(View.VISIBLE);
    }
}