package com.professional.b07legendaryproject2026;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.professional.b07legendaryproject2026.fragments.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance("https://b07legendaryproject-default-rtdb.firebaseio.com/");
        // loadAnon(savedInstanceState);
        loadAdmin(savedInstanceState);
        
    }

    private void loadAnon(Bundle savedInstanceState){
        // fake auth login for now
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        if (auth.getCurrentUser() == null) {
            auth.signInAnonymously()
                .addOnSuccessListener(authResult -> {
                    Log.d(TAG, "Anonymous authentication successful");
                    if (savedInstanceState == null) {
                        loadFragment(new HomeFragment(), false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Anonymous authentication failed", e);
                });
        } else {
            if (savedInstanceState == null) {
                loadFragment(new HomeFragment(), false);
            }
        }
    }

    private void loadAdmin(Bundle savedInstanceState){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        Log.d(TAG, "Signed out existing session before admin test login");

        auth.signInWithEmailAndPassword("mysticalboom11@gmail.com", "123456")
            .addOnSuccessListener(authResult -> {
                logCurrentUserState(auth, "Admin login success");
                if (savedInstanceState == null) {
                    loadFragment(new HomeFragment(), false);
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Admin login failed", e);
            });
    }

    private void logCurrentUserState(FirebaseAuth auth, String label) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.d(TAG, label + " | currentUser=null");
            return;
        }

        Log.d(TAG, label
            + " | uid=" + user.getUid()
            + " | email=" + user.getEmail()
            + " | isAnonymous=" + user.isAnonymous());
    }



    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}