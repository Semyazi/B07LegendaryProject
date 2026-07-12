package com.professional.b07legendaryproject2026.fragments;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.professional.b07legendaryproject2026.MainActivity;
import com.professional.b07legendaryproject2026.R;
import com.professional.b07legendaryproject2026.adapters.ArtifactAdapter;
import com.professional.b07legendaryproject2026.data.Artifact;
import com.professional.b07legendaryproject2026.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private ArtifactAdapter artifactAdapter;
    private final List<Artifact> allArtifacts = new ArrayList<>();
    private Spinner itemsPerPageSpinner;

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
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    ToastUtils.showToast(getContext(), query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

            // Make the magnifying glass also trigger a search and show feedback
            View searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
            if (searchIcon != null) {
                // Add ripple effect for visual feedback
                searchIcon.setBackgroundResource(R.drawable.search_icon_background);
                searchIcon.setFocusable(true);
                searchIcon.setClickable(true);

                searchIcon.setOnClickListener(v -> {
                    String query = searchView.getQuery().toString();
                    if (!query.isEmpty()) {
                        searchView.setQuery(query, true);
                    }
                });
            }
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

        View collectionsButton = view.findViewById(R.id.button_collections);
        if(collectionsButton != null)
            collectionsButton.setOnClickListener(v -> {
                if(getActivity() instanceof MainActivity) {
                    ToastUtils.showToast(getContext(), "Viewing user collections.");
                }
            });

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_artifacts);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        artifactAdapter = new ArtifactAdapter(a -> ToastUtils.showToast(getContext(), "Lot: "+a.getLotNumber()+", Name: "+a.getName()));
        recyclerView.setAdapter(artifactAdapter);

        itemsPerPageSpinner = view.findViewById(R.id.spinner_items_per_page);
        if (itemsPerPageSpinner != null) {
            // Dismiss Toast when user interacts with the spinner
            itemsPerPageSpinner.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        ToastUtils.cancelToast();
                    }
                    return false; // Let the spinner handle the touch for dropdown
                }
            });

            itemsPerPageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    updateDisplayedArtifacts();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }

        // Generate dummy data
        generateDummyData();
        updateDisplayedArtifacts();

        return view;
    }

    private void generateDummyData() {
        allArtifacts.clear();
        for (int i = 1; i <= 30; i++) {
            Artifact a = new Artifact();
            a.setLotNumber("TAAM-" + String.format("%03d", i));
            a.setName("Artifact " + i);
            a.setPeriodNum(Artifact.PeriodNum.UNKNOWN);
            // Use specific URLs for a few to test loading, leave others for placeholder
            if (i == 1 || i == 2 || i == 4) a.setImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQNo24-TgibHQu-TJ4mons88cFjtyJ1kea_dvoBKt_bEPBwe8Zv8i10l_8&s=10");
            allArtifacts.add(a);
        }
    }

    private void updateDisplayedArtifacts() {
        if (itemsPerPageSpinner == null) {
            return;
        }

        // Capture scroll position logic
        int firstVisiblePos = -1;
        RecyclerView recyclerView = null;
        if (getView() != null) {
            recyclerView = getView().findViewById(R.id.recycler_view_artifacts);
        }

        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                firstVisiblePos = linearManager.findFirstVisibleItemPosition();
            }
        }

        String selectedItem = itemsPerPageSpinner.getSelectedItem().toString();
        int numToShow = Integer.parseInt(selectedItem);

        List<Artifact> limitedList = new ArrayList<>();
        int totalAvailable = allArtifacts.size();
        int endLimit;
        if (numToShow < totalAvailable) {
            endLimit = numToShow;
        } else {
            endLimit = totalAvailable;
        }

        for (int i = 0; i < endLimit; i++) {
            limitedList.add(allArtifacts.get(i));
        }

        artifactAdapter.submitList(limitedList);

        // Smart scroll maintenance
        if (recyclerView != null) {
            if (firstVisiblePos != -1) {
                if (firstVisiblePos >= numToShow) {
                    // If user was scrolled past the new limit, jump to the last item
                    recyclerView.scrollToPosition(numToShow - 1);
                }
            }
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
