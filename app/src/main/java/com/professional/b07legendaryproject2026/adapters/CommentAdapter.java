package com.professional.b07legendaryproject2026.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.professional.b07legendaryproject2026.R;
import com.professional.b07legendaryproject2026.data.Comment;
import com.professional.b07legendaryproject2026.managers.UserSession;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import android.view.View;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {
    private final List<Comment> comments = new ArrayList<>();

    public interface OnCommentDeleteListener {
        void onDeleteComment(Comment comment);
    }
    private OnCommentDeleteListener deleteListener;
    public void setOnCommentDeleteListener(OnCommentDeleteListener listener) {
        this.deleteListener = listener;
    }

    public void submitList(List<Comment> newComments) {
        comments.clear();
        if (newComments != null) {
            newComments.sort(Comparator.reverseOrder());
            comments.addAll(newComments);
        }
        notifyDataSetChanged();
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comments.sort(Comparator.reverseOrder());
        notifyItemInserted(comments.indexOf(comment));
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);

        View deleteBtn = holder.itemView.findViewById(R.id.button_delete_comment);

        if (deleteBtn != null) {
            UserSession session = UserSession.getInstance();
            String currentUid = session.getUid();
            
            boolean isOwner = currentUid != null && currentUid.equals(comment.getAuthorUid());
            boolean canDelete = isOwner || session.isAdmin();

            deleteBtn.setVisibility(canDelete ? View.VISIBLE : View.GONE);
            deleteBtn.setOnClickListener(v -> {
                if (deleteListener != null) deleteListener.onDeleteComment(comment);
            });
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}