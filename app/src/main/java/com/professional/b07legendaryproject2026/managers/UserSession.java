package com.professional.b07legendaryproject2026.managers;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserSession {
    private static UserSession instance;

    private String username;
    private boolean isAdmin;

    private static final String PREF_NAME = "LegendaryProjectUserPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_ADMIN = "admin";

    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // Login with Firebase Auth, then fetch the user's data from the Realtime DB before calling the onSuccess callback
    public void login(Context context, String email, String password, Runnable onSuccess, Runnable onFailure) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fetchUserSetup(context, onSuccess, onFailure);
                    } else {
                        if (onFailure != null) onFailure.run();
                    }
                });
    }

    // Signup with Firebase Auth then store the user's data in the Realtime DB before calling the onSuccess callback
    public void signup(Context context, String email, String username, String password, Runnable onSuccess, Runnable onFailure) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || task.getResult().getUser() == null){
                        if (onFailure != null) onFailure.run();
                        return;
                    }

                    String newUid = task.getResult().getUser().getUid();

                    java.util.Map<String, Object> userData = new java.util.HashMap<>();
                    userData.put("username", username);
                    userData.put("admin", false);

                    FirebaseDatabase.getInstance().getReference("users").child(newUid)
                            .setValue(userData)
                            .addOnCompleteListener(dbTask -> {
                                if (dbTask.isSuccessful()) {
                                    setSession(username, false, context);
                                    if (onSuccess != null) onSuccess.run();
                                } else {
                                    if (onFailure != null) onFailure.run();
                                }
                            });
                });
    }

    // Fetches the user information from the Firebase Realtime DB, we must first login using Firebase Auth to get a UID
    public void fetchUserSetup(Context context, Runnable onSuccess, Runnable onFailure) {
        String uid = getUid();
        if (uid == null) {
            if (onFailure != null) onFailure.run();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    if (onFailure != null) onFailure.run();
                    return;
                }

                Boolean adminFlag = snapshot.child("admin").getValue(Boolean.class);
                String uName = snapshot.child("username").getValue(String.class);

                boolean finalAdmin = adminFlag != null ? adminFlag : false;
                String finalUsername = uName != null ? uName : "Unknown";

                setSession(finalUsername, finalAdmin, context);
                if (onSuccess != null) onSuccess.run();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (onFailure != null) onFailure.run();
            }
        });
    }

    // Save a session to memory and to SharedPreferences
    public void setSession(String username, boolean isAdmin, Context context) {
        this.username = username;
        this.isAdmin = isAdmin;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_USERNAME, username)
                .putBoolean(KEY_IS_ADMIN, isAdmin)
                .apply();
    }

    // Load our SharedPreferences data (username, isAdmin). Should be called once in the MainActivity's onCreate() method
    public void loadFromDisk(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.username = prefs.getString(KEY_USERNAME, null);
        this.isAdmin = prefs.getBoolean(KEY_IS_ADMIN, false);
    }

    // Reset the session (i.e. logout) and sign out of Firebase Auth
    public void clearSession(Context context) {
        this.username = null;
        this.isAdmin = false;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
        }
    }

    // Getters
    public String getUsername() { return username; }
    public boolean isAdmin() { return isLoggedIn() && isAdmin; }
    // Check if we're logged in with Firebase and this object
    public boolean isLoggedIn() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser != null && this.username != null;
    }

    // Grab the UID from Firebase
    public String getUid() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return (currentUser != null) ? currentUser.getUid() : null;
    }
}