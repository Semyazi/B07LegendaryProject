package com.professional.b07legendaryproject2026.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.professional.b07legendaryproject2026.R;
import com.professional.b07legendaryproject2026.data.Artifact;

public class ArtifactViewHolder extends RecyclerView.ViewHolder {
    private final ShapeableImageView img;
    private final TextView name;
    private final TextView lotNumber;
    private final TextView secondaryInfo;

    public ArtifactViewHolder(@NonNull View itemView) {
        super(itemView);
        img = itemView.findViewById(R.id.image_artifact_thumbnail);
        name = itemView.findViewById(R.id.text_artifact_name);
        lotNumber = itemView.findViewById(R.id.text_artifact_lot_number);
        secondaryInfo = itemView.findViewById(R.id.text_artifact_secondary_info);
    }

    public void bind(Artifact artifact, OnArtifactClickListener listener) {
        name.setText(artifact.getName());
        lotNumber.setText(artifact.getLotNumber());
        secondaryInfo.setText(artifact.getPeriodNum().name());
        Glide.with(img.getContext())
                .load(artifact.getImage())
                .placeholder(R.drawable.ic_launcher_foreground_scaled)
                .error(R.drawable.ic_launcher_foreground_scaled)
                .centerCrop()
                .into(img);
        itemView.setOnClickListener(v -> listener.onArtifactClick(artifact));

    }
}
