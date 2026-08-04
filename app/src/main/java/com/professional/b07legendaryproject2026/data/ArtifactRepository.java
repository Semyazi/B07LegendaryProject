package com.professional.b07legendaryproject2026.data;

import androidx.annotation.NonNull;
import android.net.Uri;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import com.professional.b07legendaryproject2026.utils.SupabaseImageUploader;

/** Reads artifact metadata from Firebase. Images themselves remain in Supabase. */
public class ArtifactRepository {
    private static final String DATABASE_URL =
            "https://b07legendaryproject-default-rtdb.firebaseio.com/";

    public interface ArtifactsCallback {
        void onArtifactsLoaded(List<Artifact> artifacts);
        void onError(String message);
    }
    public interface SaveStatusCallback {
        void onStatusChecked(boolean isSaved);
    }

    public interface DeleteCallback{
        void onSuccess();
        void onError(String message);
    }

    public interface ReplaceImageCallback{
        void onSuccess(String newImageUrl);
        void onError(String message);
    }

    public interface MutationCallback {
        void onSuccess();
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

    /** Creates a new artifact, refusing to overwrite an existing lot number. */
    public void addArtifact(Artifact artifact, MutationCallback callback) {
        String validationError = validateArtifact(artifact);
        if (validationError != null) {
            callback.onError(validationError);
            return;
        }

        DatabaseReference artifactReference = artifactsReference.child(artifact.getLotNumber().trim());
        artifactReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    callback.onError("Lot " + artifact.getLotNumber().trim() + " is already in use.");
                    return;
                }
                writeDetails(artifactReference, artifact, "Failed to add artifact.", callback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    /** Replaces the editable details for an existing artifact. Likes remain untouched. */
    public void updateArtifact(Artifact artifact, MutationCallback callback) {
        String validationError = validateArtifact(artifact);
        if (validationError != null) {
            callback.onError(validationError);
            return;
        }
        DatabaseReference artifactReference = artifactsReference.child(artifact.getLotNumber().trim());
        writeDetails(artifactReference, artifact, "Failed to update artifact.", callback);
    }

    private void writeDetails(DatabaseReference artifactReference, Artifact artifact,
                              String fallbackError, MutationCallback callback) {
        artifactReference.child("details")
                .setValue(toDetailsMap(artifact))
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(error -> callback.onError(
                        error.getMessage() == null ? fallbackError : error.getMessage()));
    }

    static Map<String, Object> toDetailsMap(Artifact artifact) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("name", artifact.getName().trim());
        details.put("description", artifact.getDescription().trim());
        details.put("category", artifact.getCategoryNum().getId());
        details.put("material", artifact.getMaterialNum().getId());
        details.put("dynastyPeriod", artifact.getPeriodNum().getId());
        details.put("culturalOrigin", safeString(artifact.getCulturalOrigin()));
        details.put("dimensions", safeString(artifact.getDimensions()));
        details.put("conditionReport", safeString(artifact.getConditionReport()));
        details.put("currentLocation", safeString(artifact.getCurrentLocation()));
        details.put("acquisitionMethod", safeString(artifact.getAcquisitionMethod()));
        details.put("provenance", safeString(artifact.getProvenance()));
        details.put("accessionNumber", safeString(artifact.getAccessionNumber()));
        details.put("notes", safeString(artifact.getNotes()));
        details.put("imageUrl", safeString(artifact.getImage()));
        return details;
    }

    private static String validateArtifact(Artifact artifact) {
        if (artifact == null || isBlank(artifact.getLotNumber())) return "Lot number is required.";
        if (isBlank(artifact.getName())) return "Artifact name is required.";
        if (isBlank(artifact.getDescription())) return "Description is required.";
        if (artifact.getCategoryNum() == null || artifact.getCategoryNum() == CategoryNum.UNKNOWN)
            return "Category is required.";
        if (artifact.getMaterialNum() == null || artifact.getMaterialNum() == MaterialNum.UNKNOWN)
            return "Material is required.";
        if (artifact.getPeriodNum() == null || artifact.getPeriodNum() == PeriodNum.UNKNOWN)
            return "Dynasty is required.";
        return null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String safeString(String value) {
        return value == null ? "" : value.trim();
    }

    private Artifact parseArtifact(DataSnapshot artifactSnapshot, DataSnapshot details, int likes) {
        Artifact artifact = new Artifact();
        artifact.setLotNumber(artifactSnapshot.getKey());
        artifact.setName(stringValue(details, "name"));
        artifact.setDescription(stringValue(details, "description"));
        artifact.setCategoryNum(CategoryNum.fromId(intValue(details, "category", -1)));
        artifact.setMaterialNum(MaterialNum.fromId(intValue(details, "material", -1)));
        artifact.setPeriodNum(PeriodNum.fromId(intValue(details, "dynastyPeriod", -1)));
        artifact.setLikes(likes);
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
                    if (details == null || !details.exists()) {
                        continue;
                    }
                    DataSnapshot likesSnapshot = artifactSnapshot.child("likes");
                    int likesNumber;
                    if (likesSnapshot == null || !likesSnapshot.exists()) {
                        likesNumber =0;
                    }else
                        likesNumber =(int) likesSnapshot.getChildrenCount();
                    
                    artifacts.add(parseArtifact(artifactSnapshot, details, likesNumber));
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

                                DataSnapshot likesSnapshot = artifactSnapshot.child("likes");
                                int likesNumber;
                                if (!likesSnapshot.exists()) {
                                    likesNumber =0;
                                }else
                                    likesNumber =(int) likesSnapshot.getChildrenCount();
                                savedArtifacts.add(parseArtifact(artifactSnapshot, details, likesNumber));
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

    public void isArtifactSaved(String uid, String lotNumber, SaveStatusCallback callback) {
        if (uid == null || lotNumber == null) {
            callback.onStatusChecked(false);
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance(DATABASE_URL)
                .getReference("users").child(uid).child("savedArtifacts").child(lotNumber);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isSaved = snapshot.exists() && Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                callback.onStatusChecked(isSaved);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onStatusChecked(false);
            }
        });
    }

    public void isArtifactLiked(String uid, String lotNumber, SaveStatusCallback callback) {
    if (uid == null || lotNumber == null) {
        callback.onStatusChecked(false);
        return;
    }

    DatabaseReference ref = FirebaseDatabase.getInstance(DATABASE_URL)
            .getReference("artifacts")
            .child(lotNumber)
            .child("likes")
            .child(uid);

    ref.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            boolean isLiked = snapshot.exists() && Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
            callback.onStatusChecked(isLiked);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            callback.onStatusChecked(false);
        }
    });
}

    public void toggleSaveArtifact(String uid, String lotNumber, boolean currentSavedState, Runnable onSuccess, Runnable onFailure) {
        if (uid == null || lotNumber == null) {
            if (onFailure != null) onFailure.run();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance(DATABASE_URL)
                .getReference("users").child(uid).child("savedArtifacts").child(lotNumber);

        if (currentSavedState) {
            ref.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful() && onSuccess != null) onSuccess.run();
                else if (!task.isSuccessful() && onFailure != null) onFailure.run();
            });
        } else {
            ref.setValue(true).addOnCompleteListener(task -> {
                if (task.isSuccessful() && onSuccess != null) onSuccess.run();
                else if (!task.isSuccessful() && onFailure != null) onFailure.run();
            });
        }
    }

    public void toggleLikeArtifact(String uid, String lotNumber, boolean currentLikedState, Runnable onSuccess, Runnable onFailure) {
        if (uid == null || lotNumber == null) {
            if (onFailure != null) onFailure.run();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance(DATABASE_URL)
                .getReference("artifacts").child(lotNumber).child("likes").child(uid);

        if (currentLikedState) {
            ref.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful() && onSuccess != null) onSuccess.run();
                else if (!task.isSuccessful() && onFailure != null) onFailure.run();
            });
        } else {
            ref.setValue(true).addOnCompleteListener(task -> {
                if (task.isSuccessful() && onSuccess != null) onSuccess.run();
                else if (!task.isSuccessful() && onFailure != null) onFailure.run();
            });
        }
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

    public void deleteArtifact(Artifact artifact, SupabaseImageUploader imageuploader, DeleteCallback callback){
        if(artifact == null || artifact.getLotNumber() == null || artifact.getLotNumber().trim().isEmpty()){
            callback.onError("Invalid artifact.");
            return;
        }

        String lotNumber = artifact.getLotNumber();
        String imageUrl = artifact.getImage();

        // Delete Firebase first. If permission is denied, the artifact's image must remain usable.
        deleteArtifactRecord(lotNumber, new DeleteCallback() {
            @Override
            public void onSuccess() {
                if (imageUrl == null || imageUrl.trim().isEmpty()) {
                    callback.onSuccess();
                    return;
                }
                // The database is the source of truth. A storage cleanup failure should not
                // turn a completed artifact deletion into a failed UI operation.
                imageuploader.deleteImage(imageUrl, new SupabaseImageUploader.DeleteCallback() {
                    @Override public void onSuccess() { callback.onSuccess(); }
                    @Override public void onError(String message) { callback.onSuccess(); }
                });
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void deleteArtifactRecord(String lotNumber, DeleteCallback callback){
        artifactsReference.child(lotNumber)
                .removeValue()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(
                        e.getMessage() == null ? "Failed to delete artifact." : e.getMessage())
                );
    }

    public void replaceArtifactImage(Artifact artifact, Uri newImageUri, SupabaseImageUploader imageUploader, ReplaceImageCallback callback){
        if (artifact == null || artifact.getLotNumber() == null || artifact.getLotNumber().trim().isEmpty()){
            callback.onError("Invalid artifact.");
            return;
        }
        if (newImageUri == null){
            callback.onError("No new image selected.");
            return;
        }
        String lotNumber = artifact.getLotNumber();
        String oldImageUrl = artifact.getImage();
        imageUploader.uploadImage(newImageUri, lotNumber, new SupabaseImageUploader.UploadCallback(){
            @Override
            public void onSuccess(String newImageUrl){
                artifactsReference.child(lotNumber)
                        .child("details")
                        .child("imageUrl")
                        .setValue(newImageUrl)
                        .addOnSuccessListener(unused -> {
                            artifact.setImage(newImageUrl);

                            if(oldImageUrl != null && !oldImageUrl.trim().isEmpty()){
                                imageUploader.deleteImage(oldImageUrl, new SupabaseImageUploader.DeleteCallback(){
                                    @Override
                                    public void onSuccess(){
                                        callback.onSuccess(newImageUrl);
                                    }
                                    @Override
                                    public void onError(String message){
                                        callback.onSuccess(newImageUrl);
                                    }
                                });
                            }
                            else{
                                callback.onSuccess(newImageUrl);
                            }
                        })
                        .addOnFailureListener(e -> callback.onError(
                                e.getMessage() == null ? "Failed to update artifact image." : e.getMessage()
                ));
            }
            @Override
            public void onError(String message){
                callback.onError(message);
            }
        });
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
