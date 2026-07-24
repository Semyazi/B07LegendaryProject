package com.professional.b07legendaryproject2026.managers;

import android.view.View;
import androidx.appcompat.widget.SearchView;
import com.professional.b07legendaryproject2026.R;

public class SearchManager {
    public interface SearchCallback {
        void onSearchSubmitted(String query);
        void onSearchTextChanged(String newText);
    }

    private final SearchView searchView;
    private final SearchCallback callback;

    public SearchManager(SearchView searchView, SearchCallback callback) {
        this.searchView = searchView;
        this.callback = callback;
        setupSearchView();
    }

    private void setupSearchView() {
        if (searchView == null) return;

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                searchView.setIconified(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (callback != null) {
                    callback.onSearchSubmitted(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (callback != null) {
                    callback.onSearchTextChanged(newText);
                }
                return true;
            }
        });

        // Setup magnifying glass icon click
        View searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        if (searchIcon != null) {
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
}