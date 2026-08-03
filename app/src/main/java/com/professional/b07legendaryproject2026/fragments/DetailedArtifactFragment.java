package com.professional.b07legendaryproject2026.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.professional.b07legendaryproject2026.MainActivity;
import com.professional.b07legendaryproject2026.R;
import com.professional.b07legendaryproject2026.data.Artifact;
import com.professional.b07legendaryproject2026.managers.UserSession;
import com.professional.b07legendaryproject2026.utils.ToastUtils;
import com.professional.b07legendaryproject2026.data.ArtifactRepository;
import androidx.core.content.ContextCompat;
import com.professional.b07legendaryproject2026.data.ArtifactRepository;

public class DetailedArtifactFragment extends BackBtnBaseFragment {

    private final ArtifactRepository repository = new ArtifactRepository();
    private boolean isSaved = false;
    private boolean isLiked = false;

    private static final String ARTIFACT = "clicked-artifact";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_artifact, container, false);

        Artifact artifact = null;
        if (getArguments() != null) {
            artifact = (Artifact) getArguments().getSerializable(ARTIFACT);
        }

        if (artifact != null) {
            bindArtifactData(view, artifact);
            setupActionButtons(view, artifact);
            updateIsSaved(view, artifact);
            updateIsLiked(view, artifact);
            loadCommentSection(artifact);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> navigateBack());
    }

    private void bindArtifactData(View view, Artifact artifact) {
        ImageView img = view.findViewById(R.id.image_artifact_thumbnail);
        TextView name = view.findViewById(R.id.text_artifact_name);
        TextView lotNumber = view.findViewById(R.id.text_artifact_lot_number);
        TextView secondaryInfo = view.findViewById(R.id.text_artifact_secondary_info);
        TextView description = view.findViewById(R.id.text_artifact_description);
        TextView category = view.findViewById(R.id.text_artifact_category);
        TextView material = view.findViewById(R.id.text_artifact_material);
        TextView origin = view.findViewById(R.id.text_artifact_cultural_origin);
        TextView dimensions = view.findViewById(R.id.text_artifact_dimensions);
        TextView condition = view.findViewById(R.id.text_artifact_condition);
        TextView location = view.findViewById(R.id.text_artifact_location);
        TextView acquisitionMethod = view.findViewById(R.id.text_artifact_acq_method);
        TextView provenance = view.findViewById(R.id.text_artifact_provenance);
        TextView accessionNum = view.findViewById(R.id.text_artifact_accession_num);
        TextView notes = view.findViewById(R.id.text_artifact_notes);

        name.setText(artifact.getName());
        lotNumber.setText(artifact.getLotNumber());
        secondaryInfo.setText(artifact.getPeriodDescription());
        description.setText(artifact.getDescription());

        if (artifact.getCategoryNum() != null) {
            category.setText(artifact.getCategoryNum().getDisplayName());
        }
        if (artifact.getMaterialNum() != null) {
            material.setText(artifact.getMaterialNum().getDisplayName());
        }

        origin.setText(artifact.getCulturalOrigin());
        dimensions.setText(artifact.getDimensions());
        condition.setText(artifact.getConditionReport());
        location.setText(artifact.getCurrentLocation());
        acquisitionMethod.setText(artifact.getAcquisitionMethod());
        provenance.setText(artifact.getProvenance());
        accessionNum.setText(artifact.getAccessionNumber());
        notes.setText(artifact.getNotes());

        Glide.with(this)
                .load(artifact.getImage())
                .placeholder(R.drawable.skeleton_placeholder)
                .error(R.drawable.skeleton_placeholder)
                .into(img);
    }

    @SuppressLint("SetTextI18n")
    private void setupActionButtons(View view, Artifact artifact) {
        Button btnSave = view.findViewById(R.id.button_save_to_collection);
        Button btnEdit = view.findViewById(R.id.button_edit);
        Button btnDelete = view.findViewById(R.id.button_delete);
        Button btnLike = view.findViewById(R.id.button_like);
        View space1 = view.findViewById(R.id.admin_action_space);
        View space2 = view.findViewById(R.id.admin_action_space_2);

        btnSave.setOnClickListener(v -> handleSaveToggle(view, artifact));
        btnLike.setOnClickListener(v -> handleLikeToggle(view, artifact));

        btnLike.setText(""+ artifact.getLikes());

        if (UserSession.getInstance().isAdmin()) {
            btnEdit.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            if (space1 != null) space1.setVisibility(View.VISIBLE);
            if (space2 != null) space2.setVisibility(View.VISIBLE);

            btnEdit.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    AddArtifactFragment editFragment = AddArtifactFragment.newInstance(artifact);
                    ((MainActivity) getActivity()).loadFragment(editFragment, true);
                }
            });
            btnDelete.setOnClickListener(v -> showDeleteConfirmation(artifact));
        }
        else {
            btnSave.setText("Save to Collection"); // Full text for regular users
            btnDelete.setOnClickListener(v -> ToastUtils.showToast(getContext(), "Delete"));
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            if (space1 != null) space1.setVisibility(View.GONE);
            if (space2 != null) space2.setVisibility(View.GONE);
        }

        updateSaveButtonUI(view);
    }

    private void showDeleteConfirmation(Artifact artifact) {
        if (artifact == null) return;

        com.google.android.material.dialog.MaterialAlertDialogBuilder builder =
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext());

        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete Artifact " + artifact.getLotNumber() + "?");
        builder.setBackground(androidx.core.content.ContextCompat.getDrawable(requireContext(), R.drawable.dialog_background));

        builder.setPositiveButton("YES", (dialog, which) -> {
            ToastUtils.showToast(getContext(), "implementation in progress");
        });

        builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void updateIsSaved(View view, Artifact artifact) {
        String uid = UserSession.getInstance().getUid();
        if (uid == null || artifact == null || artifact.getLotNumber() == null) return;

        repository.isArtifactSaved(uid, artifact.getLotNumber(), saved -> {
            if (!isAdded()) return;
            isSaved = saved;
            updateSaveButtonUI(view);
        });
    }

    private void updateIsLiked(View view, Artifact artifact){
        String uid = UserSession.getInstance().getUid();
        if (uid == null || artifact == null || artifact.getLotNumber() == null) return;

        repository.isArtifactLiked(uid, artifact.getLotNumber(), liked -> {
            if (!isAdded()) return;
            isLiked = liked;
            updateLikeButtonUI(view, artifact);
        });
    }

    private void updateSaveButtonUI(View view) {
        Button btnSave = view.findViewById(R.id.button_save_to_collection);
        if (btnSave == null || getContext() == null) return;

        boolean isAdmin = UserSession.getInstance().isAdmin();

        if (isSaved) {
            btnSave.setText(isAdmin ? "Remove" : "Remove from Collection");
            btnSave.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.imperial_red));
        } else {
            btnSave.setText(isAdmin ? "Save" : "Save to Collection");
            btnSave.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.button_primary));
        }
    }

    private void updateLikeButtonUI(View view, Artifact artifact) {
        Button btnLike = view.findViewById(R.id.button_like);
        if (btnLike == null || getContext() == null) return;

        if (isLiked) {
            btnLike.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.imperial_red));
        } else {
            btnLike.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.button_primary));
        }

    }

    private void handleSaveToggle(View view, Artifact artifact) {
        String uid = UserSession.getInstance().getUid();
        if (uid == null || artifact == null || artifact.getLotNumber() == null) {
            ToastUtils.showToast(getContext(), "Unable to update collection.");
            return;
        }

        Button btnSave = view.findViewById(R.id.button_save_to_collection);
        if (btnSave != null) btnSave.setEnabled(false);

        boolean currentSavedState = isSaved;

        repository.toggleSaveArtifact(uid, artifact.getLotNumber(), currentSavedState, () -> {
            if (!isAdded()) return;
            isSaved = !currentSavedState;
            updateSaveButtonUI(view);
            if (btnSave != null) btnSave.setEnabled(true);

            String message = isSaved ? "Added to your collection." : "Removed from your collection.";
            ToastUtils.showToast(getContext(), message);
        }, () -> {
            if (!isAdded()) return;
            if (btnSave != null) btnSave.setEnabled(true);
            ToastUtils.showToast(getContext(), "Failed to update collection.");
        });
    }

    private void handleLikeToggle(View view, Artifact artifact){
        String uid = UserSession.getInstance().getUid();
        if (uid == null || artifact == null || artifact.getLotNumber() == null) {
            ToastUtils.showToast(getContext(), "Unable to like.");
            return;
        }

        Button btnLike = view.findViewById(R.id.button_like);
        if (btnLike != null) btnLike.setEnabled(false);

        boolean currentLikedState = isLiked;

        repository.toggleLikeArtifact(uid, artifact.getLotNumber(), currentLikedState, () -> {
            if (!isAdded()) return;
            isLiked = !currentLikedState;
            updateLikeButtonUI(view, artifact);
            if (btnLike != null) btnLike.setEnabled(true);

            String message;
            if (isLiked) {
                message = "Liked.";
                artifact.setLikes(artifact.getLikes() + 1);
            } else {
                message = "Unliked.";
                artifact.setLikes(Math.max(0, artifact.getLikes() - 1));
            }
            btnLike.setText(""+artifact.getLikes());

            ToastUtils.showToast(getContext(), message);
        }, () -> {
            if (!isAdded()) return;
            if (btnLike != null) btnLike.setEnabled(true);
            ToastUtils.showToast(getContext(), "Failed to like.");
        });
    }


    private void loadCommentSection(Artifact artifact) {
        if (getChildFragmentManager().findFragmentById(R.id.fragment_container_comments) == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_comments, CommentFragment.newInstance(artifact.getLotNumber()))
                    .commit();
        }
    }
}