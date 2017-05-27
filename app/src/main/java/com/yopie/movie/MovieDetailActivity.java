package com.yopie.movie;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.yopie.movie.model.MovieList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yopie on 5/27/2017.
 */

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private MovieList listData;
    private Gson gson = new Gson();

    @BindView(R.id.iv_cover) ImageView iv_cover;
    @BindView(R.id.card_thumbnail) ImageView cardThumbnail;
    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.tv_overview) TextView tvOverview;
    @BindView(R.id.tv_rating) TextView tvRating;
    @BindView(R.id.tv_release_date) TextView tvReleaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String extraJsonData = getIntent().getStringExtra("data"); // mengambil data dari intent
        if (extraJsonData != null) {
            listData = gson.fromJson(extraJsonData, MovieList.class);
            bindData();
            initCollapsingToolbar();
        }
    }

    private void bindData() {

        Glide
                .with(this)
                .load("https://image.tmdb.org/t/p/w154" + listData.getPosterPath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .into(cardThumbnail);

        Glide
                .with(this)
                .load("https://image.tmdb.org/t/p/w500" + listData.getBackdropPath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .into(iv_cover);

        tvTitle.setText(listData.getTitle());
        tvOverview.setText(listData.getOverview());
        tvRating.setText(listData.getVoteAverage() + "/10");
        tvReleaseDate.setText(listData.getReleaseDate());
    }

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle("Movie Detail");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
