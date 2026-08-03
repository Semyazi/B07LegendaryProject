package com.professional.b07legendaryproject2026.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.professional.b07legendaryproject2026.R;
import com.professional.b07legendaryproject2026.data.Artifact;
import com.professional.b07legendaryproject2026.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class AddArtifactFragment extends BackBtnBaseFragment {

    private static final String ARG_ARTIFACT = "artifact-to-edit";

    private EditText editLotNumber, editName, editDescription;
    private Spinner spinnerCategory, spinnerMaterial, spinnerDynasty;
    private Button buttonBrowse, buttonSubmit;
    private TextView textImagePath;
    private TextView textHeader;

    // Optional fields
    private EditText editOrigin, editDimensions, editCondition, editLocation,
            editAcquisition, editProvenance, editAccession, editNotes;

    private Artifact artifactToEdit;

    public static AddArtifactFragment newInstance(Artifact artifact) {
        AddArtifactFragment fragment = new AddArtifactFragment();
        if (artifact != null) {
            Bundle args = new Bundle();
            args.putSerializable(ARG_ARTIFACT, artifact);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_artifact, container, false);

        if (getArguments() != null) {
            artifactToEdit = (Artifact) getArguments().getSerializable(ARG_ARTIFACT);
        }

        // Initialize UI components
        textHeader = view.findViewById(R.id.text_add_artifact_header);
        editLotNumber = view.findViewById(R.id.edit_lot_number);
        editName = view.findViewById(R.id.edit_artifact_name);
        editDescription = view.findViewById(R.id.edit_description);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        spinnerMaterial = view.findViewById(R.id.spinner_material);
        spinnerDynasty = view.findViewById(R.id.spinner_dynasty);
        buttonBrowse = view.findViewById(R.id.button_browse_gallery);
        buttonSubmit = view.findViewById(R.id.button_submit_artifact);
        textImagePath = view.findViewById(R.id.text_image_path);

        editOrigin = view.findViewById(R.id.edit_origin);
        editDimensions = view.findViewById(R.id.edit_dimensions);
        editCondition = view.findViewById(R.id.edit_condition);
        editLocation = view.findViewById(R.id.edit_location);
        editAcquisition = view.findViewById(R.id.edit_acquisition);
        editProvenance = view.findViewById(R.id.edit_provenance);
        editAccession = view.findViewById(R.id.edit_accession);
        editNotes = view.findViewById(R.id.edit_notes);

        if (artifactToEdit != null) {
            prefillFields();
        }

        buttonBrowse.setOnClickListener(v -> {
            ToastUtils.showToast(getContext(), "gallery browsing not implemented yet");
        });

        buttonSubmit.setOnClickListener(v -> {
            if (artifactToEdit != null) { //todo: this is the edit artifact case
                ToastUtils.showToast(getContext(), "implementation in progress");
            } else { //todo: this is the add new artifact case
                validateAndSubmit();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> navigateBack());
    }

    private void prefillFields() {
        if (textHeader != null) textHeader.setText("Edit Artifact");
        if (buttonSubmit != null) buttonSubmit.setText("Edit Artifact");

        editLotNumber.setText(artifactToEdit.getLotNumber());
        editLotNumber.setEnabled(false);
        editLotNumber.setBackgroundColor(getResources().getColor(R.color.disabled_text_color, null));
        editLotNumber.setAlpha(0.6f);

        editName.setText(artifactToEdit.getName());
        editDescription.setText(artifactToEdit.getDescription());

        spinnerCategory.setSelection(artifactToEdit.getCategoryNum().getId() + 1);
        spinnerMaterial.setSelection(artifactToEdit.getMaterialNum().getId() + 1);
        spinnerDynasty.setSelection(artifactToEdit.getPeriodNum().getId() + 1);

        if (editOrigin != null) editOrigin.setText(artifactToEdit.getCulturalOrigin());
        if (editDimensions != null) editDimensions.setText(artifactToEdit.getDimensions());
        if (editCondition != null) editCondition.setText(artifactToEdit.getConditionReport());
        if (editLocation != null) editLocation.setText(artifactToEdit.getCurrentLocation());
        if (editAcquisition != null) editAcquisition.setText(artifactToEdit.getAcquisitionMethod());
        if (editProvenance != null) editProvenance.setText(artifactToEdit.getProvenance());
        if (editAccession != null) editAccession.setText(artifactToEdit.getAccessionNumber());
        if (editNotes != null) editNotes.setText(artifactToEdit.getNotes());

        if (textImagePath != null && artifactToEdit.getImage() != null) {
            textImagePath.setText(artifactToEdit.getImage());
        }
    }

    private void validateAndSubmit() {
        String lotNumber = editLotNumber.getText().toString().trim();
        List<String> missingFields = new ArrayList<>();

        if (lotNumber.isEmpty()) missingFields.add("Lot number");
        if (editName.getText().toString().trim().isEmpty()) missingFields.add("Artifact name");
        if (editDescription.getText().toString().trim().isEmpty()) missingFields.add("Description");
        if (spinnerCategory.getSelectedItemPosition() == 0) missingFields.add("Category");
        if (spinnerMaterial.getSelectedItemPosition() == 0) missingFields.add("Material");
        if (spinnerDynasty.getSelectedItemPosition() == 0) missingFields.add("Dynasty");

        if (!missingFields.isEmpty()) {
            ToastUtils.showToast(getContext(), missingFields.get(0) + " is required.");
        } else {
            // Check if lot number exists in Firebase
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("artifacts").child(lotNumber);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        ToastUtils.showToast(getContext(), "Lot " + lotNumber + " is already in use.");
                    } else {
                        ToastUtils.showToast(getContext(), "artifact added! no implementation yet tho :(");
                        // Future: implement saveArtifact(lotNumber);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    ToastUtils.showToast(getContext(), "Database error: " + error.getMessage());
                }
            });
        }
    }
}
