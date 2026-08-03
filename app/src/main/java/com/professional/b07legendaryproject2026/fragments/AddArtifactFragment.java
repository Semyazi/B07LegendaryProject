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
import com.professional.b07legendaryproject2026.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class AddArtifactFragment extends BackBtnBaseFragment {

    private EditText editLotNumber, editName, editDescription;
    private Spinner spinnerCategory, spinnerMaterial, spinnerDynasty;
    private Button buttonBrowse, buttonSubmit;
    private TextView textImagePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_artifact, container, false);

        // Initialize UI components
        editLotNumber = view.findViewById(R.id.edit_lot_number);
        editName = view.findViewById(R.id.edit_artifact_name);
        editDescription = view.findViewById(R.id.edit_description);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        spinnerMaterial = view.findViewById(R.id.spinner_material);
        spinnerDynasty = view.findViewById(R.id.spinner_dynasty);
        buttonBrowse = view.findViewById(R.id.button_browse_gallery);
        buttonSubmit = view.findViewById(R.id.button_submit_artifact);
        textImagePath = view.findViewById(R.id.text_image_path);

        buttonBrowse.setOnClickListener(v -> {
            ToastUtils.showToast(getContext(), "gallery browsing not implemented yet");
        });

        buttonSubmit.setOnClickListener(v -> {
            validateAndSubmit();
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
