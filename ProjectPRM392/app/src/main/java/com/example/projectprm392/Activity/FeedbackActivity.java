package com.example.projectprm392.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectprm392.Domain.FeedbackDomain;
import com.example.projectprm392.Domain.ItemsDomain;
import com.example.projectprm392.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class FeedbackActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText editTextComment;
    private Button submitButton;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private ItemsDomain object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initBackIcon();

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback");

        ratingBar = findViewById(R.id.ratingBar);
        editTextComment = findViewById(R.id.editTextComment);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });
    }

    private void initBackIcon(){
        setContentView(R.layout.activity_feedback);

        ImageView backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void submitFeedback() {
        String comment = editTextComment.getText().toString().trim();
        float ranking = ratingBar.getRating();
        object = (ItemsDomain) getIntent().getSerializableExtra("object");

        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(this, "Please write a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        String feedbackId = UUID.randomUUID().toString();
        FeedbackDomain feedback = new FeedbackDomain(comment, feedbackId, ranking, object.getTitle(), auth.getCurrentUser().getUid());

        databaseReference.child(feedbackId).setValue(feedback)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();

                    // Reset the form
                    ratingBar.setRating(0);
                    editTextComment.setText("");
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error submitting feedback: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
