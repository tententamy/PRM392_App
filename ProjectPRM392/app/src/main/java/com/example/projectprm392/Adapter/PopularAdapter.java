package com.example.projectprm392.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.projectprm392.Activity.DetailActivity;
import com.example.projectprm392.Activity.ReviewActivity;
import com.example.projectprm392.Domain.ItemsDomain;
import com.example.projectprm392.databinding.ViewholderPopularBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.Viewholder> {
    ArrayList<ItemsDomain> items;
    Context context;
    private FirebaseDatabase database;

    public PopularAdapter(ArrayList<ItemsDomain> items) {
        this.items = items;
        this.database = FirebaseDatabase.getInstance(); // Initialize Firebase Database
    }

    @NonNull
    @Override
    public PopularAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderPopularBinding binding = ViewholderPopularBinding.inflate(LayoutInflater.from(context), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularAdapter.Viewholder holder, int position) {
        ItemsDomain currentItem = items.get(position);
        holder.binding.titletxt.setText(currentItem.getTitle());
        holder.binding.priceTxt.setText("$" + currentItem.getPrice());
        holder.binding.ratingtxt.setText("(" + currentItem.getRating() + "')");
        holder.binding.oldPricetxt.setText("$" + currentItem.getOldPrice());
        holder.binding.oldPricetxt.setPaintFlags(holder.binding.oldPricetxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.binding.ratingBar.setRating((float) currentItem.getRating());

        // Load image using Glide
        RequestOptions options = new RequestOptions().transform(new CenterCrop());
        Glide.with(context)
                .load(currentItem.getPicUrl().get(0))
                .apply(options)
                .into(holder.binding.pic);

        // Fetch and set review count
        fetchReviewCount(currentItem.getTitle(), holder);

        // Set OnClickListener for the comment icon
        holder.binding.imageView6.setOnClickListener(view -> {
            Intent intent = new Intent(context, ReviewActivity.class);
            intent.putExtra("object", currentItem);
            context.startActivity(intent);
        });

        // Set OnClickListener for the entire item view
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", currentItem);
            context.startActivity(intent);
        });
    }

    private void fetchReviewCount(String title, Viewholder holder) {
        DatabaseReference reviewsRef = database.getReference("Feedback");
        reviewsRef.orderByChild("title").equalTo(title).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int reviewCount = (int) snapshot.getChildrenCount(); // Get the count of reviews
                holder.binding.reviewTxt.setText(String.valueOf(reviewCount)); // Set the review count in the TextView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
                holder.binding.reviewTxt.setText("0"); // Set to 0 if there's an error
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderPopularBinding binding;

        public Viewholder(ViewholderPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}