package com.professional.b07legendaryproject2026.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.professional.b07legendaryproject2026.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class ArtifactRepository {
    private final DatabaseReference artifactsRef = FirebaseDatabase.getInstance().getReference("artifacts");

    public interface RepositoryCallback {
        void onDataLoaded(List<Artifact> artifacts);
        void onError(String message);
    }

    public void getArtifacts(Context c, RepositoryCallback callback) {
        artifactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Artifact> artifacts = new ArrayList<>();
                for (DataSnapshot artifactSnapshot : snapshot.getChildren()) {
                    Artifact a = new Artifact();

                    // Lot number is the key
                    a.setLotNumber(artifactSnapshot.getKey());
                    
                    // Access nested details
                    DataSnapshot details = artifactSnapshot.child("details");
                    if (details.exists()) {
                        a.setName(details.child("name").getValue(String.class));
                        a.setDescription(details.child("description").getValue(String.class));
                        Integer categoryId = details.child("category").getValue(Integer.class);
                        if(categoryId != null) {
                            a.setCategoryNum(Artifact.CategoryNum.fromId(categoryId));
                        }
                        Integer materialId = details.child("material").getValue(Integer.class);
                        if(materialId != null) {
                            a.setCategoryNum(Artifact.CategoryNum.fromId(materialId));
                        }
                        Integer periodId = details.child("dynastyPeriod").getValue(Integer.class);
                        if (periodId != null) {
                            a.setPeriodNum(Artifact.PeriodNum.fromId(periodId));
                        }
                        a.setCulturalOrigin(details.child("culturalOrigin").getValue(String.class));
                        a.setDimensions(details.child("dimensions").getValue(String.class));
                        a.setConditionReport(details.child("conditionReport").getValue(String.class));
                        a.setCurrentLocation(details.child("currentLocation").getValue(String.class));
                        a.setAcquisitionMethod(details.child("acquisitionMethod").getValue(String.class));
                        a.setProvenance(details.child("provenance").getValue(String.class));
                        a.setAccessionNumber(details.child("accessionNumber").getValue(String.class));
                        a.setNotes(details.child("notes").getValue(String.class));
                        a.setImage(details.child("imageUrl").getValue(String.class));
                    }
                    
                    artifacts.add(a);
                }
                callback.onDataLoaded(artifacts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ArtifactRepository.java","Firebase load cancelled", error.toException());
                if (c != null) {
                    ToastUtils.showToast(c, "Failed to load artifacts from database.");
                }
                callback.onError(error.getMessage());
            }
        });
    }
}
