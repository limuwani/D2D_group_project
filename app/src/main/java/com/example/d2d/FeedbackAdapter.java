package com.example.d2d;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {
    private List<Feedback> feedbackList;

    public FeedbackAdapter(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Feedback feedback = feedbackList.get(position);
        holder.ratingText.setText(String.format("⭐ %.1f", feedback.getRating()));
        holder.dateText.setText(feedback.getDate());
        holder.commentText.setText("\"" + feedback.getComment() + "\"");
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ratingText, dateText, commentText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingText = itemView.findViewById(R.id.review_rating_text);
            dateText = itemView.findViewById(R.id.review_date_text);
            commentText = itemView.findViewById(R.id.review_comment_text);
        }
    }
}
