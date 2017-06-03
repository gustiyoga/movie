package com.yopie.movie.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yopie.movie.R;
import com.yopie.movie.model.ReviewList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yopie on 6/3/2017.
 */

public class ReviewItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_reviewAuthor) TextView tvAuthor;
    @BindView(R.id.tv_reviewContent) TextView tvContent;

    public ReviewItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(ReviewList data) {
        tvAuthor.setText(data.getAuthor());
        tvContent.setText(data.getContent());
    }
}
