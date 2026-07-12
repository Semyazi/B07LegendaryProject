package com.professional.b07legendaryproject2026.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
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
    private static final String PREFS_NAME = "TAAM_Prefs";
    private static final String KEY_ITEMS_PER_PAGE = "items_per_page";

    private ArtifactAdapter artifactAdapter;
    private final List<Artifact> allArtifacts = new ArrayList<>();
    private Spinner itemsPerPageSpinner;
    private LinearLayout pageNumbersContainer;
    private Button prevButton;
    private Button nextButton;
    private int currentPage = 1;
    private int totalPages = 1;

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

        pageNumbersContainer = view.findViewById(R.id.layout_page_numbers);
        prevButton = view.findViewById(R.id.button_prev_page);
        nextButton = view.findViewById(R.id.button_next_page);

        if (prevButton != null) {
            prevButton.setOnClickListener(v -> {
                if (currentPage > 1) {
                    currentPage--;
                    updateDisplayedArtifacts();
                }
            });
        }

        if (nextButton != null) {
            nextButton.setOnClickListener(v -> {
                if (currentPage < totalPages) {
                    currentPage++;
                    updateDisplayedArtifacts();
                }
            });
        }

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
                    // Save the user's preference
                    String selected = parent.getItemAtPosition(position).toString();
                    if (getContext() != null) {
                        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(KEY_ITEMS_PER_PAGE, selected);
                        editor.apply();
                    }

                    currentPage = 1;
                    updateDisplayedArtifacts();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            // Load saved preference
            if (getContext() != null) {
                SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String savedItemsPerPage = prefs.getString(KEY_ITEMS_PER_PAGE, "12"); // 12 is the defaultValue btw
                // iter. through selection options to set accordingly
                for (int i = 0; i < itemsPerPageSpinner.getCount(); i++) {
                    String itemValue = itemsPerPageSpinner.getItemAtPosition(i).toString();
                    if (itemValue.equals(savedItemsPerPage)) {
                        itemsPerPageSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }

        // Generate dummy data
        generateDummyData(45);
        updateDisplayedArtifacts();

        return view;
    }

    private void generateDummyData(int n) {
        allArtifacts.clear();
        for (int i = 1; i <= n; i++) {
            Artifact a = new Artifact();
            a.setLotNumber("rawad-" + String.format("%03d", i));
            a.setName("rawad " + i);
            a.setPeriodNum(Artifact.PeriodNum.FIVE_DYNASTIES_AND_TEN_KINGDOMS);
            if (i == 1) a.setImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQNo24-TgibHQu-TJ4mons88cFjtyJ1kea_dvoBKt_bEPBwe8Zv8i10l_8&s=10");
            allArtifacts.add(a);
        }
    }

    private void updateDisplayedArtifacts() {
        if (itemsPerPageSpinner == null) {
            return;
        }

        // Save scroll pos. per page when swapping # displayed
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


        // Get page data: items per page and total pages
        int itemsPerPage = Integer.parseInt(itemsPerPageSpinner.getSelectedItem().toString());

        int totalAvailable = allArtifacts.size();
        totalPages = (int) Math.ceil((double) totalAvailable / itemsPerPage);

        // Ensure current page is valid
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }
        if (currentPage < 1) {
            currentPage = 1;
        }


        int startIndex = (currentPage - 1) * itemsPerPage; // how many cards were on all the previous pages? we start there
        int endIndex = Math.min(startIndex + itemsPerPage, totalAvailable);

        List<Artifact> limitedList = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            limitedList.add(allArtifacts.get(i));
        }

        artifactAdapter.submitList(limitedList);

        // Smart scroll maintenance
        if (recyclerView != null) {
            if (firstVisiblePos != -1) {
                int numOnThisPage = limitedList.size();
                if (firstVisiblePos >= numOnThisPage) {
                    // If user was scrolled past the new limit, jump to the last item
                    recyclerView.scrollToPosition(numOnThisPage - 1);
                }
            }
        }

        updatePaginationUI();
    }

    private void updatePaginationUI() {
        if (pageNumbersContainer == null) return;

        pageNumbersContainer.removeAllViews();

        if (totalPages <= 5) {
            // Show all pages
            for (int i = 1; i <= totalPages; i++) {
                addPageNumberToUI(i);
            }
        } else {
            // ellipsis logic: [1 ... <totalPages>]
            addPageNumberToUI(1);
            
            TextView ellipsis = new TextView(requireContext());
            ellipsis.setText("...");
            ellipsis.setTextSize(16);
            ellipsis.setPadding(16, 8, 16, 8);
            ellipsis.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
            ellipsis.setGravity(Gravity.CENTER);
            ellipsis.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
            ellipsis.setOnClickListener(v -> showPageJumpDialog());
            pageNumbersContainer.addView(ellipsis);

            addPageNumberToUI(totalPages);
        }

        // Disable buttons if at boundaries
        if (prevButton != null) {
            prevButton.setEnabled(currentPage > 1);
            prevButton.setAlpha(currentPage > 1 ? 1.0f : 0.5f);
        }
        if (nextButton != null) {
            nextButton.setEnabled(currentPage < totalPages);
            nextButton.setAlpha(currentPage < totalPages ? 1.0f : 0.5f);
        }
    }

    private void addPageNumberToUI(int page) {
        if (getContext() == null) return;
        TextView tv = new TextView(requireContext());
        tv.setText(String.valueOf(page));
        tv.setTextSize(16);
        tv.setPadding(16, 8, 16, 8);
        tv.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        tv.setLayoutParams(params);

        if (page == currentPage) {
            tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.taam_gold));
            tv.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
        }

        tv.setOnClickListener(v -> {
            currentPage = page;
            updateDisplayedArtifacts();
        });

        pageNumbersContainer.addView(tv);
    }

    private void showPageJumpDialog() {
        if (getContext() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Jump to Page");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Enter page (1-" + totalPages + ")");
        builder.setView(input);

        builder.setPositiveButton("Go", (dialog, which) -> {
            String val = input.getText().toString().trim();
            if (!val.isEmpty()) {
                int targetPage = Integer.parseInt(val);
                if (targetPage >= 1 && targetPage <= totalPages) {
                    currentPage = targetPage;
                    updateDisplayedArtifacts();
                } else {
                    ToastUtils.showToast(getContext(), "Invalid page number.");
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
