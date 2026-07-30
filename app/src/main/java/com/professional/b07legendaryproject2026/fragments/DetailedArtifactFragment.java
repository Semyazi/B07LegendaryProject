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

public class DetailedArtifactFragment extends Fragment {

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
        }

        if (artifact != null) {
            loadCommentSection(artifact);
        }

        setupActionButtons(view);

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
    private void setupActionButtons(View view) {
        Button btnSave = view.findViewById(R.id.button_save_to_collection);
        Button btnEdit = view.findViewById(R.id.button_edit);
        Button btnDelete = view.findViewById(R.id.button_delete);
        View space1 = view.findViewById(R.id.admin_action_space);
        View space2 = view.findViewById(R.id.admin_action_space_2);

        btnSave.setOnClickListener(v -> ToastUtils.showToast(getContext(), "Save to collection"));

        if (UserSession.getInstance().isAdmin()) {
            btnSave.setText("Save"); // Shorten to fit all 3 buttons
            btnEdit.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            if (space1 != null) space1.setVisibility(View.VISIBLE);
            if (space2 != null) space2.setVisibility(View.VISIBLE);

            btnEdit.setOnClickListener(v -> ToastUtils.showToast(getContext(), "Edit"));
            btnDelete.setOnClickListener(v -> ToastUtils.showToast(getContext(), "Delete"));
        } else {
            btnSave.setText("Save to Collection"); // Full text for regular users
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            if (space1 != null) space1.setVisibility(View.GONE);
            if (space2 != null) space2.setVisibility(View.GONE);
        }
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
