package com.professional.b07legendaryproject2026.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.professional.b07legendaryproject2026.MainActivity;
import com.professional.b07legendaryproject2026.R;
import com.professional.b07legendaryproject2026.adapters.ArtifactAdapter;
import com.professional.b07legendaryproject2026.data.Artifact;
import com.professional.b07legendaryproject2026.data.ArtifactRepository;
import com.professional.b07legendaryproject2026.managers.PaginationManager;
import com.professional.b07legendaryproject2026.managers.SearchManager;
import com.professional.b07legendaryproject2026.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String PREFS_NAME = "TAAM_Prefs";
    private static final String KEY_ITEMS_PER_PAGE = "items_per_page";
    private ArtifactAdapter artifactAdapter;
    private final List<Artifact> allArtifacts = new ArrayList<>();
    private final ArtifactRepository repository = new ArtifactRepository();
    private Spinner itemsPerPageSpinner;
    private PaginationManager paginationManager;
    private SearchManager searchManager;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);

        setupSearch(view);
        setupNavigationButtons(view);
        setupRecyclerView(view);
        setupPagination(view);
        setupItemsPerPageSpinner(view);

        loadInitialData();

        return view;
    }

    private void setupSearch(View view) {
        SearchView searchView = view.findViewById(R.id.search_view_home);
        searchManager = new SearchManager(searchView, query -> ToastUtils.showToast(getContext(), query));
    }

    private void setupNavigationButtons(View view) {
        View buttonProfile = view.findViewById(R.id.button_profile);
        if (buttonProfile != null) {
            buttonProfile.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).loadFragment(new ProfileFragment(), true);
                }
            });
        }

        View logoutButton = view.findViewById(R.id.button_logout);
        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> ToastUtils.showToast(getContext(), "You have successfully logged out."));
        }

        View collectionsButton = view.findViewById(R.id.button_collections);
        if (collectionsButton != null) {
            collectionsButton.setOnClickListener(v -> ToastUtils.showToast(getContext(), "Viewing user collections."));
        }
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_artifacts);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        artifactAdapter = new ArtifactAdapter(a -> ToastUtils.showToast(getContext(), "Lot: " + a.getLotNumber() + ", Name: " + a.getName()));
        recyclerView.setAdapter(artifactAdapter);
    }

    private void setupPagination(View view) {
        LinearLayout container = view.findViewById(R.id.layout_page_numbers);
        Button prev = view.findViewById(R.id.button_prev_page);
        Button next = view.findViewById(R.id.button_next_page);

        paginationManager = new PaginationManager(getContext(), container, prev, next, page -> {
            updateDisplayedArtifacts();
            scrollToTop();
        });
    }

    private void setupItemsPerPageSpinner(View view) {
        itemsPerPageSpinner = view.findViewById(R.id.spinner_items_per_page);
        if (itemsPerPageSpinner == null) return;

        itemsPerPageSpinner.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ToastUtils.cancelToast();
                }
                return false;
            }
        });

        itemsPerPageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveItemsPerPagePreference(parent.getItemAtPosition(position).toString());
                paginationManager.setCurrentPage(1);
                updateDisplayedArtifacts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        loadItemsPerPagePreference();
    }

    private void loadInitialData() {
        allArtifacts.clear();
        allArtifacts.addAll(repository.getArtifacts(600));
        updateDisplayedArtifacts();
    }

    private void updateDisplayedArtifacts() {
        if (itemsPerPageSpinner == null || paginationManager == null) return;

        int itemsPerPage = Integer.parseInt(itemsPerPageSpinner.getSelectedItem().toString());

        paginationManager.update(allArtifacts.size(), itemsPerPage);

        int start = (paginationManager.getCurrentPage() - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, allArtifacts.size());

        List<Artifact> limitedList = allArtifacts.subList(start, end);
        artifactAdapter.submitList(new ArrayList<>(limitedList));
    }

    private void saveItemsPerPagePreference(String value) {
        if (getContext() != null) {
            SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY_ITEMS_PER_PAGE, value).apply();
        }
    }

    private void loadItemsPerPagePreference() {
        if (getContext() != null) {
            SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String savedValue = prefs.getString(KEY_ITEMS_PER_PAGE, "12");
            for (int i = 0; i < itemsPerPageSpinner.getCount(); i++) {
                if (itemsPerPageSpinner.getItemAtPosition(i).toString().equals(savedValue)) {
                    itemsPerPageSpinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void scrollToTop(){
        if(recyclerView == null)
            return;
        recyclerView.scrollToPosition(0);
    }
}
