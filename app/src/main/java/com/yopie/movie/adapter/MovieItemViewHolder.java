package com.yopie.movie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yopie.movie.R;
import com.yopie.movie.model.MovieList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yopie on 5/25/2017.
 */

public class MovieItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_title) TextView tvCardTitle;
    @BindView(R.id.card_thumbnail) ImageView ivCardThumbnail;

    public MovieItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(MovieList data, Context context) {
        Glide
                .with(context)
                .load("https://image.tmdb.org/t/p/w300" + data.getPosterPath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        progressBar.setVisibility(View.GONE);
//                        return false;
//                    }
//                })
                .into(ivCardThumbnail);

        tvCardTitle.setText(data.getTitle());
    }
}
