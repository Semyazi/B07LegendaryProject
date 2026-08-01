package com.professional.b07legendaryproject2026.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.professional.b07legendaryproject2026.MainActivity;
import com.professional.b07legendaryproject2026.R;
import com.professional.b07legendaryproject2026.data.Artifact;
import com.professional.b07legendaryproject2026.data.ArtifactRepository;
import com.professional.b07legendaryproject2026.managers.ArtifactGridManager;

public abstract class BaseArtifactGridFragment extends Fragment {
    protected final ArtifactRepository repository = new ArtifactRepository();
    protected ArtifactGridManager gridManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);

        View gridContainer = view.findViewById(R.id.artifact_grid_component);

        gridManager = new ArtifactGridManager(
                gridContainer,
                this::openDetailedArtifact,
                this::openAddArtifact
        );

        loadArtifactsData();

        return view;
    }

    // Subclasses specify how data is fetched (e.g., all artifacts vs. user collection)
    protected abstract void loadArtifactsData();

    protected void openDetailedArtifact(Artifact artifact) {
        if (getActivity() instanceof MainActivity) {
            Bundle args = new Bundle();
            args.putSerializable("clicked-artifact", artifact);
            DetailedArtifactFragment detailedArtifact = new DetailedArtifactFragment();
            detailedArtifact.setArguments(args);
            ((MainActivity) getActivity()).loadFragment(detailedArtifact, true);
        }
    }

    protected void openAddArtifact() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).loadFragment(new AddArtifactFragment(), true);
        }
    }

    public void stopObservers() {
        if (repository != null) {
            repository.stopObserving();
        }
    }

    @Override
    public void onDestroyView() {
        stopObservers();
        super.onDestroyView();
    }
}