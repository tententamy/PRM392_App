package com.example.projectprm392.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.projectprm392.Domain.ReviewDomain;
import com.example.projectprm392.R;

import java.util.List;

public class FeedbackAdapter extends BaseAdapter {
    private List<ReviewDomain> reviewList; // Use ReviewDomain
    private LayoutInflater inflater;

    public FeedbackAdapter(Context context, List<ReviewDomain> reviewList) {
        this.reviewList = reviewList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return reviewList.size();
    }

    @Override
    public Object getItem(int position) {
        return reviewList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.feedback_item, parent, false);
            holder = new ViewHolder();
            holder.usernameTextView = convertView.findViewById(R.id.feedback_username);
            holder.commentTextView = convertView.findViewById(R.id.feedback_comment);
            holder.ratingBar = convertView.findViewById(R.id.feedback_rating_bar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ReviewDomain review = reviewList.get(position);
        holder.usernameTextView.setText(review.getUsername());
        holder.commentTextView.setText(review.getComment());
        holder.ratingBar.setRating(review.getRating());

        return convertView;
    }

    private static class ViewHolder {
        TextView usernameTextView;
        TextView commentTextView;
        RatingBar ratingBar;
    }
}
