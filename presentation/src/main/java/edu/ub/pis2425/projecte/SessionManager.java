package edu.ub.pis2425.projecte;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "GymAppSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private static SessionManager instance;

    // Android recomendaba usar SharedPreferences para almacenar datos simples entre activities.
    // https://developer.android.com/training/data-storage/shared-preferences?hl=es-419#java
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    private SessionManager(Context context) {
        pref = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // Usamos Singleton para que solo haya una instancia.
    // Synchronized evita problemas de concurrencia.
    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void createLoginSession(String userId) {
        editor.putString(KEY_USER_ID, userId);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }

    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }
}