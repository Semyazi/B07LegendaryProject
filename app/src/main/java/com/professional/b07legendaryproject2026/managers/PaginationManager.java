package com.professional.b07legendaryproject2026.managers;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.professional.b07legendaryproject2026.R;
import com.professional.b07legendaryproject2026.utils.ToastUtils;

public class PaginationManager {
    public interface PaginationCallback {
        void onPageChanged(int newPage);
    }

    private final Context context;
    private final LinearLayout container;
    private final Button prevButton;
    private final Button nextButton;
    private final PaginationCallback callback;

    private int currentPage = 1;
    private int totalPages = 1;

    public PaginationManager(Context context, LinearLayout container, Button prev, Button next, PaginationCallback callback) {
        this.context = context;
        this.container = container;
        this.prevButton = prev;
        this.nextButton = next;
        this.callback = callback;
        setupButtons();
    }

    public void update(int totalItems, int itemsPerPage) {
        this.totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        if (currentPage > totalPages) currentPage = Math.max(1, totalPages);
        updateUI();
    }

    public int getCurrentPage() { return currentPage; }
    public int getTotalPages() { return totalPages; }
    public void setCurrentPage(int page) { 
        this.currentPage = page;
        updateUI();
    }

    private void setupButtons() {
        if (prevButton != null) {
            prevButton.setOnClickListener(v -> {
                if (currentPage > 1) {
                    currentPage--;
                    callback.onPageChanged(currentPage);
                }
            });
        }
        if (nextButton != null) {
            nextButton.setOnClickListener(v -> {
                if (currentPage < totalPages) {
                    currentPage++;
                    callback.onPageChanged(currentPage);
                }
            });
        }
    }

    private void updateUI() {
        if (container == null) {
            return;
        }
        container.removeAllViews();

        if (totalPages <= 5) {
            for (int i = 1; i <= totalPages; i++) {
                addPageNumber(i);
            }
        } else {
            // case 1: near start of pagelist: want 1, 2, 3, ..., lastpage
            if (currentPage <= 3) {
                addPageNumber(1);
                addPageNumber(2);
                addPageNumber(3);
                addEllipsis();
                addPageNumber(totalPages);
            }
            // case 2: near end of pageList: want 1, ..., lastpage-2, lastpage-1, lastpage
            else if (currentPage >= totalPages - 2) {
                addPageNumber(1);
                addEllipsis();
                addPageNumber(totalPages - 2);
                addPageNumber(totalPages - 1);
                addPageNumber(totalPages);
            }
            // case 3: in the middle: want 1, ..., curpage, ..., lastpage
            else {
                addPageNumber(1);
                addEllipsis();
                addPageNumber(currentPage);
                addEllipsis();
                addPageNumber(totalPages);
            }
        }

        if (prevButton != null) {
            prevButton.setEnabled(currentPage > 1);
        }

        if (nextButton != null) {
            nextButton.setEnabled(currentPage < totalPages);
        }
    }

    private void addPageNumber(int page) {
        TextView tv = new TextView(context);
        tv.setText(String.valueOf(page));
        tv.setTextSize(16);
        tv.setPadding(16, 8, 16, 8);
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        if (page == currentPage) {
            tv.setTextColor(ContextCompat.getColor(context, R.color.taam_gold));
            tv.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            tv.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
        }

        tv.setOnClickListener(v -> {
            currentPage = page;
            callback.onPageChanged(currentPage);
        });
        container.addView(tv);
    }

    private void addEllipsis() {
        TextView ellipsis = new TextView(context);
        ellipsis.setText("…");
        ellipsis.setTextSize(16);
        ellipsis.setPadding(16, 8, 16, 8);
        ellipsis.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
        ellipsis.setGravity(Gravity.CENTER);
        ellipsis.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        ellipsis.setOnClickListener(v -> showJumpDialog());
        container.addView(ellipsis);
    }

    private void showJumpDialog() {
        android.view.View dialogView = android.view.View.inflate(context, R.layout.dialog_jump_to_page, null);
        EditText input = dialogView.findViewById(R.id.edit_page_number);
        input.setHint("Enter page (1-" + totalPages + ")");

        com.google.android.material.dialog.MaterialAlertDialogBuilder builder =
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(context);

        builder.setView(dialogView);
        builder.setBackground(androidx.core.content.ContextCompat.getDrawable(context, R.drawable.dialog_background));

        builder.setPositiveButton("Go", (dialog, which) -> {
            String val = input.getText().toString().trim();
            if (!val.isEmpty()) {
                int targetPage = Integer.parseInt(val);
                if (targetPage >= 1 && targetPage <= totalPages) {
                    currentPage = targetPage;
                    callback.onPageChanged(currentPage);
                } else {
                    ToastUtils.showToast(context, "Invalid page number.");
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
