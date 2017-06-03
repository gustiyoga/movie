package com.yopie.movie.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yopie.movie.R;
import com.yopie.movie.model.ReviewList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yopie on 6/3/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ReviewList> listData = new ArrayList<>();

    public ReviewAdapter(List<ReviewList> listData) {
        this.listData = listData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_card, parent, false);
        return new ReviewItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ReviewItemViewHolder reviewItemViewHolder = (ReviewItemViewHolder) holder; // casting view holder menjadi ForecastItemViewHolder
        final ReviewList data = listData.get(position);
        reviewItemViewHolder.bind(data);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
