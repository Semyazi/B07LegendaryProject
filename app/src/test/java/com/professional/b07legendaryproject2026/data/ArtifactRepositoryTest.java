package com.professional.b07legendaryproject2026.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import android.net.Uri;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.professional.b07legendaryproject2026.utils.SupabaseImageUploader;

public class ArtifactRepositoryTest {

    private static final String DATABASE_URL =
            "https://b07legendaryproject-default-rtdb.firebaseio.com/";

    private MockedStatic<FirebaseDatabase> firebaseDatabaseStatic;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference artifactsReference;
    private ArtifactRepository repository;

    @Before
    public void setUp() {
        firebaseDatabase = mock(FirebaseDatabase.class);
        artifactsReference = mock(DatabaseReference.class);

        firebaseDatabaseStatic = mockStatic(FirebaseDatabase.class);
        firebaseDatabaseStatic
                .when(() -> FirebaseDatabase.getInstance(DATABASE_URL))
                .thenReturn(firebaseDatabase);

        when(firebaseDatabase.getReference("artifacts")).thenReturn(artifactsReference);

        repository = new ArtifactRepository();
    }

    @After
    public void tearDown() {
        firebaseDatabaseStatic.close();
    }

    @Test
    public void constructorUsesArtifactsReference() {
        verify(firebaseDatabase).getReference("artifacts");
    }

    @Test
    public void observeArtifacts_mapsFirebaseDetailsIntoArtifact() {
        ArtifactRepository.ArtifactsCallback callback =
                mock(ArtifactRepository.ArtifactsCallback.class);

        repository.observeArtifacts(callback);
        ValueEventListener listener = captureAttachedListener();

        DataSnapshot root = mock(DataSnapshot.class);
        DataSnapshot artifactSnapshot = mock(DataSnapshot.class);
        DataSnapshot details = mock(DataSnapshot.class);

        when(root.getChildren()).thenReturn(
                Collections.singletonList(artifactSnapshot)
        );
        when(artifactSnapshot.getKey()).thenReturn("LOT-001");
        when(artifactSnapshot.child("details")).thenReturn(details);
        when(details.exists()).thenReturn(true);

        stubString(details, "name", "Ceramic Bowl");
        stubString(details, "description", "A historical ceramic bowl");
        stubNumber(details, "category", firstValidCategory().getId());
        stubNumber(details, "material", firstValidMaterial().getId());
        stubNumber(details, "dynastyPeriod", firstValidPeriod().getId());
        stubString(details, "culturalOrigin", "China");
        stubString(details, "dimensions", "12 x 8 cm");
        stubString(details, "conditionReport", "Good");
        stubString(details, "currentLocation", "Gallery A");
        stubString(details, "acquisitionMethod", "Donation");
        stubString(details, "provenance", "Private collection");
        stubString(details, "accessionNumber", "ACC-100");
        stubString(details, "notes", "Handle with care");
        stubString(details, "imageUrl", "https://example.supabase.co/artifact.jpg");

        listener.onDataChange(root);

        ArgumentCaptor<List<Artifact>> captor = ArgumentCaptor.forClass(List.class);
        verify(callback).onArtifactsLoaded(captor.capture());

        List<Artifact> result = captor.getValue();
        assertEquals(1, result.size());

        Artifact artifact = result.get(0);
        assertEquals("LOT-001", artifact.getLotNumber());
        assertEquals("Ceramic Bowl", artifact.getName());
        assertEquals("A historical ceramic bowl", artifact.getDescription());
        assertSame(firstValidCategory(), artifact.getCategoryNum());
        assertSame(firstValidMaterial(), artifact.getMaterialNum());
        assertSame(firstValidPeriod(), artifact.getPeriodNum());
        assertEquals("China", artifact.getCulturalOrigin());
        assertEquals("12 x 8 cm", artifact.getDimensions());
        assertEquals("Good", artifact.getConditionReport());
        assertEquals("Gallery A", artifact.getCurrentLocation());
        assertEquals("Donation", artifact.getAcquisitionMethod());
        assertEquals("Private collection", artifact.getProvenance());
        assertEquals("ACC-100", artifact.getAccessionNumber());
        assertEquals("Handle with care", artifact.getNotes());
        assertEquals(
                "https://example.supabase.co/artifact.jpg",
                artifact.getImage()
        );
    }

    @Test
    public void observeArtifacts_missingOptionalStringsBecomeEmptyStrings() {
        ArtifactRepository.ArtifactsCallback callback =
                mock(ArtifactRepository.ArtifactsCallback.class);

        repository.observeArtifacts(callback);
        ValueEventListener listener = captureAttachedListener();

        DataSnapshot root = mock(DataSnapshot.class);
        DataSnapshot artifactSnapshot = mock(DataSnapshot.class);
        DataSnapshot details = mock(DataSnapshot.class);

        when(root.getChildren()).thenReturn(
                Collections.singletonList(artifactSnapshot)
        );
        when(artifactSnapshot.getKey()).thenReturn("LOT-002");
        when(artifactSnapshot.child("details")).thenReturn(details);
        when(details.exists()).thenReturn(true);

        stubNullString(details, "name");
        stubNullString(details, "description");
        stubNullNumber(details, "category");
        stubNullNumber(details, "material");
        stubNullNumber(details, "dynastyPeriod");
        stubNullString(details, "culturalOrigin");
        stubNullString(details, "dimensions");
        stubNullString(details, "conditionReport");
        stubNullString(details, "currentLocation");
        stubNullString(details, "acquisitionMethod");
        stubNullString(details, "provenance");
        stubNullString(details, "accessionNumber");
        stubNullString(details, "notes");
        stubNullString(details, "imageUrl");

        listener.onDataChange(root);

        ArgumentCaptor<List<Artifact>> captor = ArgumentCaptor.forClass(List.class);
        verify(callback).onArtifactsLoaded(captor.capture());

        Artifact artifact = captor.getValue().get(0);
        assertEquals("", artifact.getName());
        assertEquals("", artifact.getDescription());
        assertEquals("", artifact.getImage());
        assertSame(CategoryNum.UNKNOWN, artifact.getCategoryNum());
        assertSame(MaterialNum.UNKNOWN, artifact.getMaterialNum());
        assertSame(PeriodNum.UNKNOWN, artifact.getPeriodNum());
    }

    @Test
    public void observeArtifacts_skipsEntriesWithoutDetailsNode() {
        ArtifactRepository.ArtifactsCallback callback =
                mock(ArtifactRepository.ArtifactsCallback.class);

        repository.observeArtifacts(callback);
        ValueEventListener listener = captureAttachedListener();

        DataSnapshot root = mock(DataSnapshot.class);
        DataSnapshot missingDetailsArtifact = mock(DataSnapshot.class);
        DataSnapshot missingDetails = mock(DataSnapshot.class);

        when(root.getChildren()).thenReturn(
                Collections.singletonList(missingDetailsArtifact)
        );
        when(missingDetailsArtifact.child("details")).thenReturn(missingDetails);
        when(missingDetails.exists()).thenReturn(false);

        listener.onDataChange(root);

        ArgumentCaptor<List<Artifact>> captor = ArgumentCaptor.forClass(List.class);
        verify(callback).onArtifactsLoaded(captor.capture());
        assertEquals(0, captor.getValue().size());
    }

    @Test
    public void observeArtifacts_invalidEnumIdsMapToUnknown() {
        ArtifactRepository.ArtifactsCallback callback =
                mock(ArtifactRepository.ArtifactsCallback.class);

        repository.observeArtifacts(callback);
        ValueEventListener listener = captureAttachedListener();

        DataSnapshot root = mock(DataSnapshot.class);
        DataSnapshot artifactSnapshot = mock(DataSnapshot.class);
        DataSnapshot details = mock(DataSnapshot.class);

        when(root.getChildren()).thenReturn(
                Collections.singletonList(artifactSnapshot)
        );
        when(artifactSnapshot.getKey()).thenReturn("LOT-003");
        when(artifactSnapshot.child("details")).thenReturn(details);
        when(details.exists()).thenReturn(true);

        stubRequiredStringsAsEmpty(details);
        stubNumber(details, "category", 9999);
        stubNumber(details, "material", 9999);
        stubNumber(details, "dynastyPeriod", 9999);

        listener.onDataChange(root);

        ArgumentCaptor<List<Artifact>> captor = ArgumentCaptor.forClass(List.class);
        verify(callback).onArtifactsLoaded(captor.capture());

        Artifact artifact = captor.getValue().get(0);
        assertSame(CategoryNum.UNKNOWN, artifact.getCategoryNum());
        assertSame(MaterialNum.UNKNOWN, artifact.getMaterialNum());
        assertSame(PeriodNum.UNKNOWN, artifact.getPeriodNum());
    }

    @Test
    public void observeArtifacts_forwardsFirebaseErrorMessage() {
        ArtifactRepository.ArtifactsCallback callback =
                mock(ArtifactRepository.ArtifactsCallback.class);

        repository.observeArtifacts(callback);
        ValueEventListener listener = captureAttachedListener();

        DatabaseError error = mock(DatabaseError.class);
        when(error.getMessage()).thenReturn("Permission denied");

        listener.onCancelled(error);

        verify(callback).onError("Permission denied");
    }

    @Test
    public void observeArtifacts_replacesExistingListenerBeforeAttachingNewOne() {
        ArtifactRepository.ArtifactsCallback firstCallback =
                mock(ArtifactRepository.ArtifactsCallback.class);
        ArtifactRepository.ArtifactsCallback secondCallback =
                mock(ArtifactRepository.ArtifactsCallback.class);

        repository.observeArtifacts(firstCallback);
        ValueEventListener firstListener = captureAttachedListener();

        repository.observeArtifacts(secondCallback);

        verify(artifactsReference).removeEventListener(firstListener);
        verify(artifactsReference, org.mockito.Mockito.times(2))
                .addValueEventListener(any(ValueEventListener.class));
    }

    @Test
    public void stopObserving_removesAttachedListener() {
        ArtifactRepository.ArtifactsCallback callback =
                mock(ArtifactRepository.ArtifactsCallback.class);

        repository.observeArtifacts(callback);
        ValueEventListener listener = captureAttachedListener();

        repository.stopObserving();

        verify(artifactsReference).removeEventListener(listener);
    }

    @Test
    public void stopObserving_withoutListenerDoesNothing() {
        repository.stopObserving();

        verify(artifactsReference, never())
                .removeEventListener(any(ValueEventListener.class));
    }

    @Test
    public void deleteArtifact_nullArtifact_returnsError(){
        ArtifactRepository.DeleteCallback callback = mock(ArtifactRepository.DeleteCallback.class);
        SupabaseImageUploader imageUploader = mock(SupabaseImageUploader.class);

        repository.deleteArtifact(null, imageUploader, callback);

        verify(callback).onError("Invalid artifact.");
        verifyNoInteractions(imageUploader);
    }

    @Test
    public void deleteArtifact_missingLotNumber_returnsError(){
        Artifact artifact = new Artifact();
        artifact.setLotNumber("");

        ArtifactRepository.DeleteCallback callback = mock(ArtifactRepository.DeleteCallback.class);
        SupabaseImageUploader imageUploader = mock(SupabaseImageUploader.class);

        repository.deleteArtifact(artifact, imageUploader, callback);

        verify(callback).onError("Invalid artifact.");
        verifyNoInteractions(imageUploader);
    }

    @Test
    public void replaceArtifactImage_nullArtifact_returnsError(){
        SupabaseImageUploader imageUploader = mock(SupabaseImageUploader.class);
        ArtifactRepository.ReplaceImageCallback callback = mock(ArtifactRepository.ReplaceImageCallback.class);
        repository.replaceArtifactImage(null, mock(Uri.class), imageUploader, callback);
        verify(callback).onError("Invalid artifact.");
        verifyNoInteractions(imageUploader);
    }

    @Test
    public void replaceArtifactImage_nullImageUri_returnsError(){
        Artifact artifact = new Artifact();
        artifact.setLotNumber("LOT123");
        SupabaseImageUploader imageUploader = mock(SupabaseImageUploader.class);
        ArtifactRepository.ReplaceImageCallback callback = mock(ArtifactRepository.ReplaceImageCallback.class);
        repository.replaceArtifactImage(artifact, null, imageUploader, callback);
        verify(callback).onError("No new image selected.");
        verifyNoInteractions(imageUploader);

    }

    @Test
    public void addArtifact_newLot_writesDetailsAndReportsSuccess() {
        Artifact artifact = validArtifact("LOT-ADD");
        ArtifactRepository.MutationCallback callback =
                mock(ArtifactRepository.MutationCallback.class);
        DatabaseReference lotReference = mock(DatabaseReference.class);
        DatabaseReference detailsReference = mock(DatabaseReference.class);
        Task<Void> task = successfulTask();

        when(artifactsReference.child("LOT-ADD")).thenReturn(lotReference);
        when(lotReference.child("details")).thenReturn(detailsReference);
        when(detailsReference.setValue(any())).thenReturn(task);

        repository.addArtifact(artifact, callback);
        ArgumentCaptor<ValueEventListener> listenerCaptor =
                ArgumentCaptor.forClass(ValueEventListener.class);
        verify(lotReference).addListenerForSingleValueEvent(listenerCaptor.capture());

        DataSnapshot snapshot = mock(DataSnapshot.class);
        when(snapshot.exists()).thenReturn(false);
        listenerCaptor.getValue().onDataChange(snapshot);

        ArgumentCaptor<Map<String, Object>> detailsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(detailsReference).setValue(detailsCaptor.capture());
        assertEquals("Test Artifact", detailsCaptor.getValue().get("name"));
        assertEquals("", detailsCaptor.getValue().get("notes"));
        assertEquals(firstValidCategory().getId(), detailsCaptor.getValue().get("category"));
        verify(callback).onSuccess();
    }

    @Test
    public void addArtifact_existingLot_doesNotOverwrite() {
        Artifact artifact = validArtifact("LOT-DUPLICATE");
        ArtifactRepository.MutationCallback callback =
                mock(ArtifactRepository.MutationCallback.class);
        DatabaseReference lotReference = mock(DatabaseReference.class);
        when(artifactsReference.child("LOT-DUPLICATE")).thenReturn(lotReference);

        repository.addArtifact(artifact, callback);
        ArgumentCaptor<ValueEventListener> listenerCaptor =
                ArgumentCaptor.forClass(ValueEventListener.class);
        verify(lotReference).addListenerForSingleValueEvent(listenerCaptor.capture());
        DataSnapshot snapshot = mock(DataSnapshot.class);
        when(snapshot.exists()).thenReturn(true);
        listenerCaptor.getValue().onDataChange(snapshot);

        verify(callback).onError("Lot LOT-DUPLICATE is already in use.");
        verify(lotReference, never()).child("details");
    }

    @Test
    public void updateArtifact_writesDetailsWithoutReplacingArtifactNode() {
        Artifact artifact = validArtifact("LOT-EDIT");
        artifact.setName("Edited Name");
        ArtifactRepository.MutationCallback callback =
                mock(ArtifactRepository.MutationCallback.class);
        DatabaseReference lotReference = mock(DatabaseReference.class);
        DatabaseReference detailsReference = mock(DatabaseReference.class);
        Task<Void> task = successfulTask();
        when(artifactsReference.child("LOT-EDIT")).thenReturn(lotReference);
        when(lotReference.child("details")).thenReturn(detailsReference);
        when(detailsReference.setValue(any())).thenReturn(task);

        repository.updateArtifact(artifact, callback);

        ArgumentCaptor<Map<String, Object>> detailsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(detailsReference).setValue(detailsCaptor.capture());
        assertEquals("Edited Name", detailsCaptor.getValue().get("name"));
        verify(lotReference, never()).setValue(any());
        verify(callback).onSuccess();
    }

    @Test
    public void updateArtifact_databaseFailure_reportsMessage() {
        Artifact artifact = validArtifact("LOT-FAIL");
        ArtifactRepository.MutationCallback callback =
                mock(ArtifactRepository.MutationCallback.class);
        DatabaseReference lotReference = mock(DatabaseReference.class);
        DatabaseReference detailsReference = mock(DatabaseReference.class);
        Task<Void> task = failedTask(new RuntimeException("Permission denied"));
        when(artifactsReference.child("LOT-FAIL")).thenReturn(lotReference);
        when(lotReference.child("details")).thenReturn(detailsReference);
        when(detailsReference.setValue(any())).thenReturn(task);

        repository.updateArtifact(artifact, callback);

        verify(callback).onError("Permission denied");
        verify(callback, never()).onSuccess();
    }

    @Test
    public void addArtifact_missingRequiredField_isRejectedBeforeFirebaseWrite() {
        Artifact artifact = validArtifact("LOT-INVALID");
        artifact.setName("  ");
        ArtifactRepository.MutationCallback callback =
                mock(ArtifactRepository.MutationCallback.class);

        repository.addArtifact(artifact, callback);

        verify(callback).onError("Artifact name is required.");
        verifyNoInteractions(artifactsReference);
    }

    @Test
    public void deleteArtifact_withoutImage_removesDatabaseNodeAndReportsSuccess() {
        Artifact artifact = validArtifact("LOT-DELETE");
        artifact.setImage("");
        ArtifactRepository.DeleteCallback callback = mock(ArtifactRepository.DeleteCallback.class);
        SupabaseImageUploader uploader = mock(SupabaseImageUploader.class);
        DatabaseReference lotReference = mock(DatabaseReference.class);
        Task<Void> task = successfulTask();
        when(artifactsReference.child("LOT-DELETE")).thenReturn(lotReference);
        when(lotReference.removeValue()).thenReturn(task);

        repository.deleteArtifact(artifact, uploader, callback);

        verify(lotReference).removeValue();
        verify(callback).onSuccess();
        verifyNoInteractions(uploader);
    }

    @Test
    public void deleteArtifact_databaseFailure_keepsSupabaseImage() {
        Artifact artifact = validArtifact("LOT-DELETE-FAIL");
        ArtifactRepository.DeleteCallback callback = mock(ArtifactRepository.DeleteCallback.class);
        SupabaseImageUploader uploader = mock(SupabaseImageUploader.class);
        DatabaseReference lotReference = mock(DatabaseReference.class);
        Task<Void> task = failedTask(new RuntimeException("Permission denied"));
        when(artifactsReference.child("LOT-DELETE-FAIL")).thenReturn(lotReference);
        when(lotReference.removeValue()).thenReturn(task);

        repository.deleteArtifact(artifact, uploader, callback);

        verify(callback).onError("Permission denied");
        verifyNoInteractions(uploader);
    }

    private static Artifact validArtifact(String lotNumber) {
        Artifact artifact = new Artifact(lotNumber, "Test Artifact", "Test Description",
                firstValidCategory().getId(), firstValidMaterial().getId(), firstValidPeriod().getId());
        artifact.setImage("https://example.supabase.co/image.jpg");
        return artifact;
    }

    @SuppressWarnings("unchecked")
    private static Task<Void> successfulTask() {
        Task<Void> task = mock(Task.class);
        when(task.addOnSuccessListener(any())).thenAnswer(invocation -> {
            ((OnSuccessListener<Void>) invocation.getArgument(0)).onSuccess(null);
            return task;
        });
        when(task.addOnFailureListener(any())).thenReturn(task);
        return task;
    }

    @SuppressWarnings("unchecked")
    private static Task<Void> failedTask(Exception error) {
        Task<Void> task = mock(Task.class);
        when(task.addOnSuccessListener(any())).thenReturn(task);
        when(task.addOnFailureListener(any())).thenAnswer(invocation -> {
            ((OnFailureListener) invocation.getArgument(0)).onFailure(error);
            return task;
        });
        return task;
    }

    private ValueEventListener captureAttachedListener() {
        ArgumentCaptor<ValueEventListener> captor =
                ArgumentCaptor.forClass(ValueEventListener.class);
        verify(artifactsReference, org.mockito.Mockito.atLeastOnce())
                .addValueEventListener(captor.capture());

        List<ValueEventListener> captured = captor.getAllValues();
        return captured.get(captured.size() - 1);
    }

    private static void stubString(
            DataSnapshot parent,
            String field,
            String value
    ) {
        DataSnapshot child = mock(DataSnapshot.class);
        when(parent.child(field)).thenReturn(child);
        when(child.getValue(String.class)).thenReturn(value);
    }

    private static void stubNullString(DataSnapshot parent, String field) {
        stubString(parent, field, null);
    }

    private static void stubNumber(
            DataSnapshot parent,
            String field,
            Number value
    ) {
        DataSnapshot child = mock(DataSnapshot.class);
        when(parent.child(field)).thenReturn(child);
        when(child.getValue()).thenReturn(value);
    }

    private static void stubNullNumber(DataSnapshot parent, String field) {
        DataSnapshot child = mock(DataSnapshot.class);
        when(parent.child(field)).thenReturn(child);
        when(child.getValue()).thenReturn(null);
    }

    private static void stubRequiredStringsAsEmpty(DataSnapshot details) {
        for (String field : Arrays.asList(
                "name",
                "description",
                "culturalOrigin",
                "dimensions",
                "conditionReport",
                "currentLocation",
                "acquisitionMethod",
                "provenance",
                "accessionNumber",
                "notes",
                "imageUrl"
        )) {
            stubString(details, field, "");
        }
    }

    private static CategoryNum firstValidCategory() {
        for (CategoryNum value : CategoryNum.values()) {
            if (value != CategoryNum.UNKNOWN) {
                return value;
            }
        }
        throw new AssertionError("CategoryNum has no valid values");
    }

    private static MaterialNum firstValidMaterial() {
        for (MaterialNum value : MaterialNum.values()) {
            if (value != MaterialNum.UNKNOWN) {
                return value;
            }
        }
        throw new AssertionError("MaterialNum has no valid values");
    }

    private static PeriodNum firstValidPeriod() {
        for (PeriodNum value : PeriodNum.values()) {
            if (value != PeriodNum.UNKNOWN) {
                return value;
            }
        }
        throw new AssertionError("PeriodNum has no valid values");
    }
}
