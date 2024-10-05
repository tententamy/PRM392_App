package com.example.projectprm392.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectprm392.Adapter.FeedbackAdapter;
import com.example.projectprm392.Domain.ReviewDomain; // Update import to ReviewDomain
import com.example.projectprm392.R;

import java.util.ArrayList;

public class FeedbackActivity extends AppCompatActivity {
    private ArrayList<ReviewDomain> reviewList;
    private FeedbackAdapter feedbackAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        reviewList = new ArrayList<>();
        feedbackAdapter = new FeedbackAdapter(this, reviewList);

        EditText usernameEditText = findViewById(R.id.username_edit_text);
        EditText commentEditText = findViewById(R.id.comment_edit_text);
        RatingBar ratingBar = findViewById(R.id.rating_bar);
        Button submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String comment = commentEditText.getText().toString();
                float rating = ratingBar.getRating();

                if (!username.isEmpty() && !comment.isEmpty()) {
                    // Create a ReviewDomain object
                    ReviewDomain review = new ReviewDomain(username, comment, rating);
                    reviewList.add(review);
                    feedbackAdapter.notifyDataSetChanged();

                    // Clear inputs
                    usernameEditText.setText("");
                    commentEditText.setText("");
                    ratingBar.setRating(0);
                }
            }
        });
    }
}
