package com.professional.b07legendaryproject2026.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.professional.b07legendaryproject2026.R;
import com.professional.b07legendaryproject2026.adapters.CommentAdapter;
import com.professional.b07legendaryproject2026.data.Comment;
import com.professional.b07legendaryproject2026.managers.UserSession;
import com.professional.b07legendaryproject2026.utils.ToastUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CommentFragment extends Fragment {
    private static final String ARG_ARTIFACT_LOT = "artifact_lot";
    private CommentAdapter adapter;
    private String artifactLot;
    private DatabaseReference commentsRef;
    private ValueEventListener commentsListener;

    public static CommentFragment newInstance(String artifactLot) {
        CommentFragment commentFragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTIFACT_LOT, artifactLot);
        commentFragment.setArguments(args);
        return commentFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        if (getArguments() != null) {
            artifactLot = getArguments().getString(ARG_ARTIFACT_LOT);
        }


        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new CommentAdapter();
        adapter.setOnCommentDeleteListener(this::deleteComment);
        recyclerView.setAdapter(adapter);


        setupPostButton(view);

        // Start listening to Firebase if we have a lot number
        if (artifactLot != null) {
            commentsRef = FirebaseDatabase.getInstance().getReference("artifactComments").child(artifactLot);
            listenForComments();
        }

        return view;
    }

    private void listenForComments() {
        commentsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Comment> commentList = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String authorUid = userSnapshot.getKey();
                    for (DataSnapshot commentSnapshot : userSnapshot.getChildren()) {
                        String commentId = commentSnapshot.getKey();
                        String content = commentSnapshot.child("content").getValue(String.class);
                        String username = commentSnapshot.child("username").getValue(String.class);
                        Long timestampLong = commentSnapshot.child("timestamp").getValue(Long.class);

                        if (content != null && username != null && timestampLong != null) {
                            Instant instant = Instant.ofEpochSecond(timestampLong);
                            Comment comment = new Comment(commentId, content, instant, username, authorUid);
                            commentList.add(comment);
                        }
                    }
                }

                commentList.sort(Comparator.reverseOrder());
                adapter.submitList(commentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ToastUtils.showToast(getContext(), "Failed to load comments");
            }
        };
        commentsRef.addValueEventListener(commentsListener);
    }

    private void setupPostButton(View view) {
        EditText editComment = view.findViewById(R.id.edit_text_comment);
        Button btnPost = view.findViewById(R.id.button_post_comment);

        btnPost.setOnClickListener(v -> {
            String commentText = editComment.getText().toString().trim();
            if (commentText.isEmpty()) {
                ToastUtils.showToast(getContext(), "Comment cannot be empty");
                return;
            }

            UserSession session = UserSession.getInstance();
            String uid = session.getUid();
            String username = session.getUsername();

            if (uid == null || artifactLot == null) {
                ToastUtils.showToast(getContext(), "An error occurred while trying to post that comment");
                return;
            }

            String finalUsername = (username != null && !username.trim().isEmpty()) 
                    ? username 
                    : "Anonymous";

            DatabaseReference userCommentsRef = FirebaseDatabase.getInstance()
                    .getReference("artifactComments")
                    .child(artifactLot)
                    .child(uid);

            String commentId = userCommentsRef.push().getKey();
            if (commentId == null) return;

            Map<String, Object> commentData = new HashMap<>();
            commentData.put("content", commentText);
            commentData.put("username", finalUsername);
            commentData.put("timestamp", Instant.now().getEpochSecond());

            userCommentsRef.child(commentId).setValue(commentData)
                    .addOnSuccessListener(aVoid -> editComment.setText(""))
                    .addOnFailureListener(e -> ToastUtils.showToast(getContext(), "Failed to post comment"));
        });
    }

    private void deleteComment(Comment comment) {
        if (artifactLot == null || comment.getAuthorUid() == null || comment.getId() == null) return;

        FirebaseDatabase.getInstance()
                .getReference("artifactComments")
                .child(artifactLot)
                .child(comment.getAuthorUid())
                .child(comment.getId())
                .removeValue()
                .addOnFailureListener(e -> ToastUtils.showToast(getContext(), "Failed to delete comment"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (commentsRef != null && commentsListener != null) {
            commentsRef.removeEventListener(commentsListener);
        }
    }
}