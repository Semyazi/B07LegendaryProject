package com.professional.b07legendaryproject2026.fragments;

import android.os.Bundle;
import android.net.Uri;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.professional.b07legendaryproject2026.R;
import com.professional.b07legendaryproject2026.data.ArtifactRepository;
import com.professional.b07legendaryproject2026.utils.SupabaseImageUploader;
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
    private final ArtifactRepository repository = new ArtifactRepository();
    private SupabaseImageUploader imageUploader;
    private Uri selectedImageUri;
    private final ActivityResultLauncher<String> imagePicker = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                selectedImageUri = uri;
                if (uri != null && textImagePath != null) textImagePath.setText(uri.toString());
            });

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
        imageUploader = new SupabaseImageUploader(requireContext());

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
        } else {
            prefillDefaultMetadata();
        }

        buttonBrowse.setOnClickListener(v -> imagePicker.launch("image/*"));
        buttonSubmit.setOnClickListener(v -> validateAndSubmit());

        return view;
    }

    private void prefillDefaultMetadata() {
        editDimensions.setText(R.string.default_artifact_dimensions);
        editCondition.setText(R.string.default_artifact_condition);
        editLocation.setText(R.string.default_artifact_location);
        editAcquisition.setText(R.string.default_artifact_acquisition);
        editProvenance.setText(R.string.default_artifact_provenance);
        editAccession.setText(R.string.default_artifact_accession_number);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> navigateBack());
        }
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
        } else persistArtifact(buildArtifact(lotNumber));
    }

    private Artifact buildArtifact(String lotNumber) {
        Artifact artifact = new Artifact(lotNumber,
                editName.getText().toString().trim(),
                editDescription.getText().toString().trim(),
                spinnerCategory.getSelectedItemPosition() - 1,
                spinnerMaterial.getSelectedItemPosition() - 1,
                spinnerDynasty.getSelectedItemPosition() - 1);
        artifact.setCulturalOrigin(text(editOrigin));
        artifact.setDimensions(text(editDimensions));
        artifact.setConditionReport(text(editCondition));
        artifact.setCurrentLocation(text(editLocation));
        artifact.setAcquisitionMethod(text(editAcquisition));
        artifact.setProvenance(text(editProvenance));
        artifact.setAccessionNumber(text(editAccession));
        artifact.setNotes(text(editNotes));
        artifact.setImage(artifactToEdit == null ? "" : artifactToEdit.getImage());
        return artifact;
    }

    private String text(EditText field) {
        return field == null ? "" : field.getText().toString().trim();
    }

    private void persistArtifact(Artifact artifact) {
        buttonSubmit.setEnabled(false);
        if(artifactToEdit != null && selectedImageUri != null){
            repository.replaceArtifactImage(artifact, selectedImageUri, imageUploader,
                    new ArtifactRepository.ReplaceCallback(){
                @Override
                public void onSuccess(String newImageUrl){
                    if(!isAdded())
                        return;
                    buttonSubmit.setEnabled(true);
                    ToastUtils.showToast(getContext(), "Artifact updated.");
                    navigateBack();
                }
                @Override
                public void onError(String message){
                    if(!isAdded())
                        return;
                    buttonSubmit.setEnabled(true);
                    ToastUtils.showToast(getContext(), message);
                }
                    });
            return;
        }

        if (selectedImageUri == null) {
            saveMetadata(artifact, null);
            return;
        }
        imageUploader.uploadImage(selectedImageUri, artifact.getLotNumber(),
                new SupabaseImageUploader.UploadCallback() {
                    @Override public void onSuccess(String publicUrl) {
                        artifact.setImage(publicUrl);
                        saveMetadata(artifact, publicUrl);
                    }
                    @Override public void onError(String message) {
                        if(!isAdded())
                            return;
                        buttonSubmit.setEnabled(true);
                        ToastUtils.showToast(getContext(), message);
                    }
                });
    }

    private void saveMetadata(Artifact artifact, String newlyUploadedUrl) {
        ArtifactRepository.MutationCallback callback = new ArtifactRepository.MutationCallback() {
            @Override public void onSuccess() {
                if (!isAdded()) return;
                ToastUtils.showToast(getContext(), artifactToEdit == null
                        ? "Artifact added." : "Artifact updated.");
                navigateBack();
            }

            @Override public void onError(String message) {
                if (!isAdded()) return;
                buttonSubmit.setEnabled(true);
                if (newlyUploadedUrl != null) {
                    imageUploader.deleteImage(newlyUploadedUrl, new SupabaseImageUploader.DeleteCallback() {
                        @Override public void onSuccess() { }
                        @Override public void onError(String ignored) { }
                    });
                }
                ToastUtils.showToast(getContext(), message);
            }
        };

        if (artifactToEdit == null) repository.addArtifact(artifact, callback);
        else repository.updateArtifact(artifact, callback);
    }
}
