package com.professional.b07legendaryproject2026.fragments;

import com.professional.b07legendaryproject2026.data.Artifact;
import com.professional.b07legendaryproject2026.data.ArtifactRepository;
import com.professional.b07legendaryproject2026.managers.UserSession;
import com.professional.b07legendaryproject2026.utils.ToastUtils;

import java.util.List;

public class CollectionsFragment extends BaseArtifactGridFragment {

    @Override
    protected void loadArtifactsData() {
        String uid = UserSession.getInstance().getUid();
        repository.observeSavedArtifacts(uid, new ArtifactRepository.ArtifactsCallback() {
            @Override
            public void onArtifactsLoaded(List<Artifact> artifacts) {
                if (!isAdded()) return;
                gridManager.setArtifacts(artifacts);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                ToastUtils.showToast(getContext(), "Failed to load collections: " + message);
            }
        });
    }
}