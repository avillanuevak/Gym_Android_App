package edu.ub.pis2425.projecte;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class NavigationHelper {

    private final Activity activity;

    public NavigationHelper(Activity activity) {
        this.activity = activity;
    }

    public void setupNavigation() {
        // Configure navigation TextViews
        setupNavigationItem(R.id.nav_class_registration, GymClassActivity.class);
        setupNavigationItem(R.id.nav_profile, ProfileActivity.class);
        setupNavigationItem(R.id.nav_nutrition, NutritionActivity.class);
        setupNavigationItem(R.id.nav_calendar, CalendarActivity.class);
        setupNavigationItem(R.id.nav_exercises, EjerciciosActivity.class);


        // Configure logout button
        TextView navLogout = activity.findViewById(R.id.nav_logout);
        if (navLogout != null) {
            navLogout.setOnClickListener(v -> {
                // Limpiamos los datos de la SessionManager.
                SessionManager sessionManager = SessionManager.getInstance(activity);
                sessionManager.logoutUser();

                Toast.makeText(activity, "Logged out successfully", Toast.LENGTH_SHORT).show();

                // Usamos *Flags* para limpiar la pila de actividades
                Intent intent = new Intent(activity, LogInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
                activity.finish();
            });
        }
    }

    private void setupNavigationItem(int viewId, Class<?> targetActivity) {
        TextView navItem = activity.findViewById(viewId);
        if (navItem != null) {
            navItem.setOnClickListener(v -> activity.startActivity(new Intent(activity, targetActivity)));
        }
    }
}