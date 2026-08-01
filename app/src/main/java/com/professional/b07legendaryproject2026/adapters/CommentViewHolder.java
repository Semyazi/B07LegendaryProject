package com.professional.b07legendaryproject2026.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.professional.b07legendaryproject2026.R;
import  com.professional.b07legendaryproject2026.data.Comment;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    private final TextView username;
    private final TextView timestamp;
    private final TextView message;
    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);

        username = itemView.findViewById(R.id.text_comment_username);
        timestamp = itemView.findViewById(R.id.text_comment_timestamp);
        message = itemView.findViewById(R.id.text_comment_message);
    }

    public void bind(Comment comment) {
        username.setText(comment.getUsername());
        timestamp.setText(comment.getTimestampInDeviceTimezone());
        message.setText(comment.getMsg());
    }
}
