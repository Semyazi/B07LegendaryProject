package com.professional.b07legendaryproject2026.data;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

/** Reads artifact metadata from Firebase. Images themselves remain in Supabase. */
public class ArtifactRepository {
    private static final String DATABASE_URL =
            "https://b07legendaryproject-default-rtdb.firebaseio.com/";

    public interface ArtifactsCallback {
        void onArtifactsLoaded(List<Artifact> artifacts);
        void onError(String message);
    }

    private final DatabaseReference artifactsReference;
    private ValueEventListener artifactsListener;

    private DatabaseReference savedReference;
    private ValueEventListener savedListener;

    public ArtifactRepository() {
        artifactsReference = FirebaseDatabase.getInstance(DATABASE_URL)
                .getReference("artifacts");
    }

    private Artifact parseArtifact(DataSnapshot artifactSnapshot, DataSnapshot details) {
        Artifact artifact = new Artifact();
        artifact.setLotNumber(artifactSnapshot.getKey());
        artifact.setName(stringValue(details, "name"));
        artifact.setDescription(stringValue(details, "description"));
        artifact.setCategoryNum(CategoryNum.fromId(intValue(details, "category", -1)));
        artifact.setMaterialNum(MaterialNum.fromId(intValue(details, "material", -1)));
        artifact.setPeriodNum(PeriodNum.fromId(intValue(details, "dynastyPeriod", -1)));
        artifact.setCulturalOrigin(stringValue(details, "culturalOrigin"));
        artifact.setDimensions(stringValue(details, "dimensions"));
        artifact.setConditionReport(stringValue(details, "conditionReport"));
        artifact.setCurrentLocation(stringValue(details, "currentLocation"));
        artifact.setAcquisitionMethod(stringValue(details, "acquisitionMethod"));
        artifact.setProvenance(stringValue(details, "provenance"));
        artifact.setAccessionNumber(stringValue(details, "accessionNumber"));
        artifact.setNotes(stringValue(details, "notes"));
        artifact.setImage(stringValue(details, "imageUrl"));
        return artifact;
    }

    /**
     * Observes artifacts so the grid refreshes whenever Firebase data changes.
     * The Supabase public URL is read from details/imageUrl.
     */
    public void observeArtifacts(ArtifactsCallback callback) {
        stopObserving();
        artifactsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Artifact> artifacts = new ArrayList<>();
                for (DataSnapshot artifactSnapshot : snapshot.getChildren()) {
                    DataSnapshot details = artifactSnapshot.child("details");
                    if (!details.exists()) {
                        continue;
                    }
                    artifacts.add(parseArtifact(artifactSnapshot, details));
                }
                callback.onArtifactsLoaded(artifacts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        };
        artifactsReference.addValueEventListener(artifactsListener);
    }

    /**
     * Observes only the artifacts saved in the current user's collection (users/$uid/savedArtifacts).
     */
    public void observeSavedArtifacts(String uid, ArtifactsCallback callback) {
        stopObserving();
        if (uid == null || uid.isEmpty()) {
            callback.onArtifactsLoaded(new ArrayList<>());
            return;
        }

        savedReference = FirebaseDatabase.getInstance(DATABASE_URL)
                .getReference("users").child(uid).child("savedArtifacts");

        savedListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot savedSnapshot) {
                Set<String> savedLotNumbers = new HashSet<>();
                for (DataSnapshot child : savedSnapshot.getChildren()) {
                    if (Boolean.TRUE.equals(child.getValue(Boolean.class))) {
                        savedLotNumbers.add(child.getKey());
                    }
                }

                artifactsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot artifactsSnapshot) {
                        List<Artifact> savedArtifacts = new ArrayList<>();
                        for (DataSnapshot artifactSnapshot : artifactsSnapshot.getChildren()) {
                            String lotNumber = artifactSnapshot.getKey();
                            if (savedLotNumbers.contains(lotNumber)) {
                                DataSnapshot details = artifactSnapshot.child("details");
                                if (!details.exists()) continue;

                                savedArtifacts.add(parseArtifact(artifactSnapshot, details));
                            }
                        }
                        callback.onArtifactsLoaded(savedArtifacts);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        };

        savedReference.addValueEventListener(savedListener);
    }

    public void stopObserving() {
        if (artifactsListener != null) {
            artifactsReference.removeEventListener(artifactsListener);
            artifactsListener = null;
        }
        if (savedListener != null && savedReference != null) {
            savedReference.removeEventListener(savedListener);
            savedListener = null;
            savedReference = null;
        }
    }

    private static String stringValue(DataSnapshot parent, String childName) {
        String value = parent.child(childName).getValue(String.class);
        return value == null ? "" : value;
    }

    private static int intValue(DataSnapshot parent, String childName, int fallback) {
        Object value = parent.child(childName).getValue();
        return value instanceof Number ? ((Number) value).intValue() : fallback;
    }
}