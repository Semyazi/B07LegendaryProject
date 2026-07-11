package com.professional.b07legendaryproject2026.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.professional.b07legendaryproject2026.MainActivity;
import com.professional.b07legendaryproject2026.R;
import com.professional.b07legendaryproject2026.utils.ToastUtils;


public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);

        SearchView searchView = view.findViewById(R.id.search_view_home);
        if (searchView != null) {
            searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    searchView.setIconified(false);
                }
            });
        }

        View buttonProfile = view.findViewById(R.id.button_profile);
        if (buttonProfile != null)
            buttonProfile.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).loadFragment(new ProfileFragment(), true);
                }
            });

        View logoutButton = view.findViewById(R.id.button_logout);
        if(logoutButton != null)
            logoutButton.setOnClickListener(v -> {
                if(getActivity() instanceof MainActivity) {
                    //((MainActivity) getActivity()).loadFragment(new LoginFragment(), false); TODO: implement the login page to be shown after user logs out.
                    ToastUtils.showToast(getContext(), "You have successfully logged out.");
                }
            });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
