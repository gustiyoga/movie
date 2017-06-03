package com.yopie.movie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yopie.movie.R;
import com.yopie.movie.model.TrailerList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yopie on 6/2/2017.
 */

public class TrailerItemViewHolder extends RecyclerView.ViewHolder {


    @BindView(R.id.iv_trailer) ImageView ivTrailer;

    public TrailerItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(TrailerList data, Context context) {
        Glide
                .with(context)
                .load("http://img.youtube.com/vi/" + data.getKey() + "/mqdefault.jpg")
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
//                        pbTrailer.setVisibility(View.GONE);
//                        return false;
//                    }
//                })
                .into(ivTrailer);
    }
}
