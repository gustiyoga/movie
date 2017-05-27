package com.yopie.movie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yopie.movie.R;
import com.yopie.movie.model.MovieList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yopie on 5/25/2017.
 */

public class MovieItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.card_title) TextView tvCardTitle;
    @BindView(R.id.card_rating) TextView tvCardRating;
    @BindView(R.id.card_thumbnail) ImageView ivCardThumbnail;

    public MovieItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(MovieList data, Context context) {
        Glide
                .with(context)
                .load("https://image.tmdb.org/t/p/w300" + data.getPosterPath())
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .into(ivCardThumbnail);

        tvCardTitle.setText(data.getTitle());
        tvCardRating.setText("Rating : " + String.valueOf(data.getVoteAverage()));
    }
}
