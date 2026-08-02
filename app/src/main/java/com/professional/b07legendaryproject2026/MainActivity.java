package com.professional.b07legendaryproject2026;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.professional.b07legendaryproject2026.fragments.BaseArtifactGridFragment;
import com.professional.b07legendaryproject2026.fragments.HomeFragment;
import com.professional.b07legendaryproject2026.fragments.ProfileFragment;
import com.professional.b07legendaryproject2026.managers.UserSession;
import com.professional.b07legendaryproject2026.utils.ToastUtils;
import com.professional.b07legendaryproject2026.fragments.CollectionsFragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    private View topNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserSession.getInstance().loadFromDisk(this);
        if (!UserSession.getInstance().isLoggedIn()) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_main);

        topNavigationBar = findViewById(R.id.top_navigation_bar);

        setupTopTabListeners();

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
            if (backStackCount > 0) {
                topNavigationBar.setVisibility(View.GONE);
            } else {
                topNavigationBar.setVisibility(View.VISIBLE);
            }
        });

        if (savedInstanceState == null) {
            loadTabFragment(new HomeFragment());
        }
    }

    private void setupTopTabListeners() {
        findViewById(R.id.button_tab_home).setOnClickListener(v -> loadTabFragment(new HomeFragment()));

        findViewById(R.id.button_tab_collections).setOnClickListener(v -> loadTabFragment(new CollectionsFragment()));

        findViewById(R.id.button_tab_profile).setOnClickListener(v -> loadTabFragment(new ProfileFragment()));

        findViewById(R.id.button_tab_logout).setOnClickListener(v -> handleLogout());
    }

    public void loadTabFragment(Fragment fragment) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void handleLogout() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof BaseArtifactGridFragment) {
            ((BaseArtifactGridFragment) currentFragment).stopObservers();
        }

        UserSession.getInstance().clearSession(this);
        ToastUtils.showToast(this, "You have successfully logged out.");
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}