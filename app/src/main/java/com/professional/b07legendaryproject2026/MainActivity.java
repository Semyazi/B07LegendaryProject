package com.professional.b07legendaryproject2026;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.professional.b07legendaryproject2026.fragments.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance("https://b07legendaryproject-default-rtdb.firebaseio.com/");

        if (savedInstanceState == null) {
            authenticateAndLoadHome();
        }
    }

    private void authenticateAndLoadHome() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            loadFragment(new HomeFragment(), false);
            return;
        }

        // Artifact reads require auth != null in the supplied Realtime Database rules.
        // Existing signed-in users are kept; anonymous auth is only the guest fallback.
        auth.signInAnonymously().addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(this,
                        "Firebase authentication failed. Enable Anonymous sign-in in Firebase Authentication.",
                        Toast.LENGTH_LONG).show();
            }
            loadFragment(new HomeFragment(), false);
        });
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
