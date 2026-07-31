package com.professional.b07legendaryproject2026.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.professional.b07legendaryproject2026.R;
import com.professional.b07legendaryproject2026.data.Artifact;
import com.professional.b07legendaryproject2026.managers.UserSession;
import com.professional.b07legendaryproject2026.utils.ToastUtils;
import androidx.core.content.ContextCompat;
import com.professional.b07legendaryproject2026.data.ArtifactRepository;

public class DetailedArtifactFragment extends Fragment {

    private final ArtifactRepository repository = new ArtifactRepository();
    private boolean isSaved = false;

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
            checkIfSaved(view, artifact);
        }

        return view;
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
        View space1 = view.findViewById(R.id.admin_action_space);
        View space2 = view.findViewById(R.id.admin_action_space_2);

        btnSave.setOnClickListener(v -> handleSaveToggle(view, artifact));

        if (UserSession.getInstance().isAdmin()) {
            btnEdit.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            if (space1 != null) space1.setVisibility(View.VISIBLE);
            if (space2 != null) space2.setVisibility(View.VISIBLE);

            btnEdit.setOnClickListener(v -> ToastUtils.showToast(getContext(), "Edit"));
            btnDelete.setOnClickListener(v -> ToastUtils.showToast(getContext(), "Delete"));
        } else {
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            if (space1 != null) space1.setVisibility(View.GONE);
            if (space2 != null) space2.setVisibility(View.GONE);
        }

        updateSaveButtonUI(view);
    }

    private void checkIfSaved(View view, Artifact artifact) {
        String uid = UserSession.getInstance().getUid();
        if (uid == null || artifact == null || artifact.getLotNumber() == null) return;

        repository.isArtifactSaved(uid, artifact.getLotNumber(), saved -> {
            if (!isAdded()) return;
            isSaved = saved;
            updateSaveButtonUI(view);
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
}