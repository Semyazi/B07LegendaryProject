package com.professional.b07legendaryproject2026.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.professional.b07legendaryproject2026.R;
import com.professional.b07legendaryproject2026.adapters.ArtifactAdapter;
import com.professional.b07legendaryproject2026.data.Artifact;
import com.professional.b07legendaryproject2026.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class ArtifactGridManager {
    private static final String PREFS_NAME = "TAAM_Prefs";
    private static final String KEY_ITEMS_PER_PAGE = "items_per_page";

    private final View rootGridContainer;
    private final OnArtifactClickListener clickListener;
    private final OnAddClickListener addClickListener;

    private RecyclerView recyclerView;
    private ArtifactAdapter artifactAdapter;
    private TextView emptyStateText;
    private Spinner itemsPerPageSpinner;

    private SearchManager searchManager; // <-- USING YOUR SEARCHMANAGER HERE
    private PaginationManager paginationManager;

    private final List<Artifact> masterList = new ArrayList<>();
    private final List<Artifact> filteredArtifacts = new ArrayList<>();
    private String currentQuery = "";

    public interface OnArtifactClickListener {
        void onArtifactClick(Artifact artifact);
    }

    public interface OnAddClickListener {
        void onAddClick();
    }

    public ArtifactGridManager(View rootGridContainer, OnArtifactClickListener clickListener, OnAddClickListener addClickListener) {
        this.rootGridContainer = rootGridContainer;
        this.clickListener = clickListener;
        this.addClickListener = addClickListener;

        initViews();
        setupSearch();
        setupRecyclerView();
        setupPagination();
        setupItemsPerPageSpinner();
        setupAdminAddButton();
    }

    public void setArtifacts(List<Artifact> artifacts) {
        masterList.clear();
        if (artifacts != null) {
            masterList.addAll(artifacts);
        }
        performSearch(currentQuery);
    }

    private void initViews() {
        recyclerView = rootGridContainer.findViewById(R.id.recycler_view_artifacts);
        emptyStateText = rootGridContainer.findViewById(R.id.text_home_title);
        itemsPerPageSpinner = rootGridContainer.findViewById(R.id.spinner_items_per_page);
    }

    private void setupSearch() {
        SearchView searchView = rootGridContainer.findViewById(R.id.search_view_home);
        if (searchView == null) return;

        searchManager = new SearchManager(searchView, new SearchManager.SearchCallback() {
            @Override
            public void onSearchSubmitted(String query) {
                currentQuery = query;
                performSearch(query);
                searchView.clearFocus();
            }

            @Override
            public void onSearchTextChanged(String newText) {
                currentQuery = newText;
                performSearch(newText);
            }
        });

        if (currentQuery != null && !currentQuery.isEmpty()) {
            searchManager.setQuery(currentQuery, false);
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(rootGridContainer.getContext(), 2));
        artifactAdapter = new ArtifactAdapter(a -> {
            if (clickListener != null) clickListener.onArtifactClick(a);
        });
        recyclerView.setAdapter(artifactAdapter);
    }

    private void setupPagination() {
        LinearLayout container = rootGridContainer.findViewById(R.id.layout_page_numbers);
        Button prev = rootGridContainer.findViewById(R.id.button_prev_page);
        Button next = rootGridContainer.findViewById(R.id.button_next_page);

        paginationManager = new PaginationManager(rootGridContainer.getContext(), container, prev, next, page -> {
            updateDisplayedArtifacts();
            scrollToTop();
        });
    }

    private void setupItemsPerPageSpinner() {
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
                if (paginationManager != null) {
                    paginationManager.setCurrentPage(1);
                }
                updateDisplayedArtifacts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        loadItemsPerPagePreference();
    }

    private void setupAdminAddButton() {
        View addButton = rootGridContainer.findViewById(R.id.button_add);
        if (addButton != null) {
            if (UserSession.getInstance().isAdmin() && addClickListener != null) {
                addButton.setVisibility(View.VISIBLE);
                addButton.setOnClickListener(v -> addClickListener.onAddClick());
            } else {
                addButton.setVisibility(View.GONE);
            }
        }
    }

    private void performSearch(String query) {
        filteredArtifacts.clear();

        if (query == null || query.trim().isEmpty()) {
            filteredArtifacts.addAll(masterList);
        } else {
            for (Artifact artifact : masterList) {
                if (artifact.matchesQuery(query)) {
                    filteredArtifacts.add(artifact);
                }
            }
        }

        if (paginationManager != null) {
            paginationManager.setCurrentPage(1);
        }
        updateDisplayedArtifacts();
    }

    private void updateDisplayedArtifacts() {
        if (itemsPerPageSpinner == null || paginationManager == null) return;

        int itemsPerPage = Integer.parseInt(itemsPerPageSpinner.getSelectedItem().toString());
        paginationManager.update(filteredArtifacts.size(), itemsPerPage);

        if (filteredArtifacts.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            artifactAdapter.submitList(new ArrayList<>());
            return;
        }

        emptyStateText.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        int start = (paginationManager.getCurrentPage() - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, filteredArtifacts.size());

        List<Artifact> limitedList = filteredArtifacts.subList(start, end);
        artifactAdapter.submitList(new ArrayList<>(limitedList));
    }

    private void scrollToTop() {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(0);
        }
    }

    private void saveItemsPerPagePreference(String value) {
        Context context = rootGridContainer.getContext();
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY_ITEMS_PER_PAGE, value).apply();
        }
    }

    private void loadItemsPerPagePreference() {
        Context context = rootGridContainer.getContext();
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String savedValue = prefs.getString(KEY_ITEMS_PER_PAGE, "12");
            for (int i = 0; i < itemsPerPageSpinner.getCount(); i++) {
                if (itemsPerPageSpinner.getItemAtPosition(i).toString().equals(savedValue)) {
                    itemsPerPageSpinner.setSelection(i);
                    break;
                }
            }
        }
    }
}