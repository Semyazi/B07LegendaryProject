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
import androidx.recyclerview.widget.RecyclerView;

import com.professional.b07legendaryproject2026.MainActivity;
import com.professional.b07legendaryproject2026.R;
import com.professional.b07legendaryproject2026.adapters.ArtifactAdapter;
import com.professional.b07legendaryproject2026.data.Artifact;
import com.professional.b07legendaryproject2026.data.ArtifactRepository;
import com.professional.b07legendaryproject2026.managers.PaginationManager;
import com.professional.b07legendaryproject2026.managers.SearchManager;
import com.professional.b07legendaryproject2026.utils.ToastUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String PREFS_NAME = "TAAM_Prefs";
    private static final String KEY_ITEMS_PER_PAGE = "items_per_page";
    private ArtifactAdapter artifactAdapter;
    private final List<Artifact> allArtifacts = new ArrayList<>();
    private final List<Artifact> filteredArtifacts = new ArrayList<>();
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
        showAdminButtonIfAdmin(view);
        setupRecyclerView(view);
        setupPagination(view);
        setupItemsPerPageSpinner(view);

        loadArtifacts();

        return view;
    }

    private void setupSearch(View view) {
        SearchView searchView = view.findViewById(R.id.search_view_home);
        searchManager = new SearchManager(searchView, new SearchManager.SearchCallback() {
            @Override
            public void onSearchSubmitted(String query) {
                performSearch(query);
                searchView.clearFocus();
            }

            @Override
            public void onSearchTextChanged(String newText) {
                performSearch(newText);
            }
        });
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

          private void showAdminButtonIfAdmin(View view){
        View addButton = view.findViewById(R.id.button_add);
        if (addButton == null) return;
        addButton.setVisibility(View.GONE);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();
        FirebaseDatabase.getInstance("https://b07legendaryproject-default-rtdb.firebaseio.com/")
        .getReference("users")
        .child(uid)
        .child("admin")
        .addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Boolean isAdmin = snapshot.getValue(Boolean.class);
            if (Boolean.TRUE.equals(isAdmin)) {
                addButton.setVisibility(View.VISIBLE);
            }
        }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_artifacts);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        artifactAdapter = new ArtifactAdapter(a -> {
            if (getActivity() == null) {
                return;
            }

            ToastUtils.showToast(getContext(), "Lot: " + a.getLotNumber() + ", Name: " + a.getName());

            Bundle args = new Bundle();
            args.putSerializable("clicked-artifact", a);
            DetailedArtifactFragment detailedArtifact = new DetailedArtifactFragment();
            detailedArtifact.setArguments(args);

            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(detailedArtifact, true);
            }
        });
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

    private void loadArtifacts() {
        repository.observeArtifacts(new ArtifactRepository.ArtifactsCallback() {
            @Override
            public void onArtifactsLoaded(List<Artifact> artifacts) {
                if (!isAdded()) return;

                allArtifacts.clear();
                allArtifacts.addAll(artifacts);

                filteredArtifacts.clear();
                filteredArtifacts.addAll(artifacts);

                paginationManager.setCurrentPage(1);
                updateDisplayedArtifacts();
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                ToastUtils.showToast(getContext(), "Could not load artifacts: " + message);
            }
        });
    }

    @Override
    public void onDestroyView() {
        repository.stopObserving();
        recyclerView = null;
        super.onDestroyView();
    }

    private void updateDisplayedArtifacts() {
        if (itemsPerPageSpinner == null || paginationManager == null) return;

        int itemsPerPage = Integer.parseInt(itemsPerPageSpinner.getSelectedItem().toString());

        paginationManager.update(filteredArtifacts.size(), itemsPerPage);

        int start = (paginationManager.getCurrentPage() - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, filteredArtifacts.size());

        List<Artifact> limitedList = filteredArtifacts.subList(start, end);
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

    private void performSearch(String query) {
        filteredArtifacts.clear();

        if (query == null || query.trim().isEmpty()) {
            filteredArtifacts.addAll(allArtifacts);
        } else {
            for (Artifact artifact : allArtifacts) {
                if (artifact.matchesQuery(query)) {
                    filteredArtifacts.add(artifact);
                }
            }
        }

        paginationManager.setCurrentPage(1);
        updateDisplayedArtifacts();
    }
}
