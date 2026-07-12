package com.professional.b07legendaryproject2026.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.professional.b07legendaryproject2026.R;
import com.professional.b07legendaryproject2026.data.Artifact;

import java.util.ArrayList;
import java.util.List;

public class ArtifactAdapter extends RecyclerView.Adapter<ArtifactViewHolder> {
    private final List<Artifact> artifacts = new ArrayList<>();
    private final OnArtifactClickListener listener;

    @SuppressLint("NotifyDataSetChanged")
    public void submitList(List <Artifact> newArtifacts) {
        artifacts.clear();
        artifacts.addAll(newArtifacts);
        notifyDataSetChanged(); // refresh UI, essentially. tells RecyclerView that the entire data set needs to be re-rendered.
    }

    public ArtifactAdapter(OnArtifactClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArtifactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArtifactViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artifact_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArtifactViewHolder holder, int position) {
        holder.bind(artifacts.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return artifacts.size();
    }
}
