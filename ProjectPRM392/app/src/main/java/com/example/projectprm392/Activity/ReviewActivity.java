package com.example.projectprm392.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectprm392.Adapter.ReviewAdapter;
import com.example.projectprm392.Domain.ReviewDomain;
import com.example.projectprm392.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.ImageView;
import android.view.View;

import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private ArrayList<ReviewDomain> reviewList;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        initBackIcon();
        initFirebase();
        initRecyclerView();

        // Assuming you have the productId you want to fetch reviews for
        String productId = "product001"; // Replace with the actual product ID you want to filter by
        fetchReviewsByProductId(productId);
    }

    private void initFirebase() {
        database = FirebaseDatabase.getInstance();
    }

    private void initBackIcon(){
        setContentView(R.layout.activity_review);

        ImageView backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view_reviews); // Ensure this ID matches your XML
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reviewAdapter);
    }

    private void fetchReviewsByProductId(String productId) {
        DatabaseReference reviewsRef = database.getReference("Feedback"); // Ensure the table name is correct

        // Query to get feedback for the specified productId
        reviewsRef.orderByChild("productId").equalTo(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear(); // Clear existing reviews

                if (snapshot.exists()) {
                    for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                        String userId = reviewSnapshot.child("userId").getValue(String.class);
                        String comment = reviewSnapshot.child("comment").getValue(String.class);
                        Float ranking = reviewSnapshot.child("ranking").getValue(Float.class);

                        // Log the retrieved data
                        Log.d("ReviewActivity", "UserId: " + userId + ", Comment: " + comment + ", Rating: " + ranking);

                        // Create ReviewDomain object and add it to the list
                        ReviewDomain review = new ReviewDomain(userId, comment, ranking);
                        reviewList.add(review);
                    }

                    // Notify adapter of data change
                    reviewAdapter.notifyDataSetChanged();
                    Log.d("ReviewActivity", "Data fetched successfully. Total reviews: " + reviewList.size());
                } else {
                    Log.d("ReviewActivity", "No data found for productId: " + productId);
                    Toast.makeText(ReviewActivity.this, "No reviews available for this product.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ReviewActivity", "Error fetching data: " + error.getMessage());
                Toast.makeText(ReviewActivity.this, "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
