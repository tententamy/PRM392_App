package com.example.projectprm392.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectprm392.Adapter.ReviewAdapter;
import com.example.projectprm392.Domain.ReviewDomain;
import com.example.projectprm392.R;

import java.util.ArrayList;
import java.util.List;

public class ViewReviewsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private List<ReviewDomain> reviewList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_reviews, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_reviews);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Dummy data for reviews
        reviewList = new ArrayList<>();
        reviewList.add(new ReviewDomain("User1", "Great shop!", 5));
        reviewList.add(new ReviewDomain("User2", "Average experience.", 3));

        reviewAdapter = new ReviewAdapter(reviewList);
        recyclerView.setAdapter(reviewAdapter);
        return view;
    }
}
