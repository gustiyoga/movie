package com.yopie.movie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yopie.movie.R;
import com.yopie.movie.model.MovieList;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Yopie on 5/25/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<MovieList> listData = new ArrayList<>();

    private MovieItemClickListener mMovieItemClickListener;

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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final MovieItemViewHolder movieItemViewHolder = (MovieItemViewHolder) holder; // casting view holder menjadi ForecastItemViewHolder
        final MovieList data = listData.get(position);
        movieItemViewHolder.bind(data, context);

        // holder.itemView adalah item view pada list
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMovieItemClickListener != null)
                    mMovieItemClickListener.onMovieItemClick(data, movieItemViewHolder.ivCardThumbnail);
                else
                    Log.e(TAG, "Error onClick listener");
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void setMovieItemClickListener(MovieItemClickListener clickListener) {
        if (clickListener != null)
            mMovieItemClickListener = clickListener;
    }

    // Inner interface class utk click listener pada recyclerview
    public interface MovieItemClickListener {
        void onMovieItemClick(MovieList data, ImageView ivCardThumbnail);
    }
}
