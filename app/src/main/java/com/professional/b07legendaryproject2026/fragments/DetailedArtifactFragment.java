package com.professional.b07legendaryproject2026.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.professional.b07legendaryproject2026.R;
import com.professional.b07legendaryproject2026.adapters.ArtifactViewHolder;
import com.professional.b07legendaryproject2026.data.Artifact;

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
            ArtifactViewHolder holder = new ArtifactViewHolder(view);
            holder.bind(artifact, artifactItem -> {});



//            ShapeableImageView img = view.findViewById(R.id.image_artifact_thumbnail);
//            TextView name = view.findViewById(R.id.text_artifact_name);
//            TextView lotNumber = view.findViewById(R.id.text_artifact_lot_number);
//            TextView secondaryInfo = view.findViewById(R.id.text_artifact_secondary_info);
//
//            name.setText(artifact.getName());
//            lotNumber.setText(artifact.getLotNumber());
//            secondaryInfo.setText(artifact.getPeriodDescription());
//
//            Glide.with(img.getContext())
//                    .load(artifact.getImage())
//                    .placeholder(R.drawable.rawad_placeholder)
//                    .error(R.drawable.rawad_placeholder)
//                    .centerCrop()
//                    .into(img);
//
           TextView Description = view.findViewById(R.id.text_artifact_description);
           Description.setText(artifact.getDescription());

           TextView category = view.findViewById(R.id.text_artifact_category);
           //category.setText(); TODO

            TextView material = view.findViewById(R.id.text_artifact_material);
            //material.setText(); TODO

            TextView origin = view.findViewById(R.id.text_artifact_cultural_origin);
            origin.setText(artifact.getCulturalOrigin());

            TextView dimensions = view.findViewById(R.id.text_artifact_dimensions);
            dimensions.setText(artifact.getDimensions());

            TextView condition = view.findViewById(R.id.text_artifact_condition);
            condition.setText(artifact.getConditionReport());

            TextView location = view.findViewById(R.id.text_artifact_location);
            location.setText(artifact.getCurrentLocation());

            TextView acquisitionMethod = view.findViewById(R.id.text_artifact_acq_method);
            acquisitionMethod.setText(artifact.getAcquisitionMethod());

            TextView provenance = view.findViewById(R.id.text_artifact_provenance);
            provenance.setText(artifact.getProvenance());

            TextView accessionNum = view.findViewById(R.id.text_artifact_accession_num);
            accessionNum.setText(artifact.getAccessionNumber());
        }
        return view;
    }
}


