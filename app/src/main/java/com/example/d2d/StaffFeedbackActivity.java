package com.example.d2d;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StaffFeedbackActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FeedbackAdapter adapter;
    private List<Feedback> feedbackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s_view_feedback);

        ImageButton backBtn = findViewById(R.id.back_btn);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> finish());
        }

        recyclerView = findViewById(R.id.feedback_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        feedbackList = new ArrayList<>();
        adapter = new FeedbackAdapter(feedbackList);
        recyclerView.setAdapter(adapter);

        loadMockFeedback();
    }

    private void loadMockFeedback() {
        feedbackList.clear();
        
        // Mock data
        feedbackList.add(new Feedback(5.0, "16 May 2026", "Fast and friendly service. The food was hot!"));
        feedbackList.add(new Feedback(4.0, "15 May 2026", "Good, but forgot my extra napkins."));
        feedbackList.add(new Feedback(5.0, "14 May 2026", "Perfect as always."));
        feedbackList.add(new Feedback(4.5, "12 May 2026", "Very polite staff member."));
        feedbackList.add(new Feedback(5.0, "10 May 2026", "Quick collection."));
        
        // Update stats
        TextView avgRatingText = findViewById(R.id.avg_rating_text);
        TextView totalReviewsText = findViewById(R.id.total_reviews_text);
        
        if (avgRatingText != null) avgRatingText.setText("4.7");
        if (totalReviewsText != null) totalReviewsText.setText("Based on " + feedbackList.size() + " recent reviews");
        
        adapter.notifyDataSetChanged();
    }
}
