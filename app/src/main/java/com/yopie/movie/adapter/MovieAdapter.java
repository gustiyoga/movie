package com.yopie.movie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yopie.movie.R;
import com.yopie.movie.model.MovieList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yopie on 5/25/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<MovieList> listData = new ArrayList<>();

    // constructor
    public MovieAdapter(List<MovieList> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_card, parent, false);
        return new MovieItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MovieItemViewHolder movieItemViewHolder = (MovieItemViewHolder) holder; // casting view holder menjadi ForecastItemViewHolder
        MovieList data = listData.get(position);
        movieItemViewHolder.bind(data, context);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
