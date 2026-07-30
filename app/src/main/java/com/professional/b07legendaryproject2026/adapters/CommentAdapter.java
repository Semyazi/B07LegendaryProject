package com.professional.b07legendaryproject2026.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.professional.b07legendaryproject2026.R;
import com.professional.b07legendaryproject2026.data.Comment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {
    private final List<Comment> comments = new ArrayList<>();


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
        holder.bind(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
