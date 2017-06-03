package com.yopie.movie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.stetho.Stetho;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yopie.movie.adapter.ReviewAdapter;
import com.yopie.movie.adapter.TrailerAdapter;
import com.yopie.movie.database.MovieDBHelper;
import com.yopie.movie.model.MovieList;
import com.yopie.movie.model.ReviewList;
import com.yopie.movie.model.ReviewResponse;
import com.yopie.movie.model.TrailerList;
import com.yopie.movie.model.TrailerResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yopie on 5/27/2017.
 */

public class MovieDetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerItemClickListener{

    private static final String TAG = "MainActivity";
    private boolean favStatus;

    private MovieList listData;
    private Gson gson = new Gson();
    private String ApiURL;
    private List<TrailerList> listTrailer = new ArrayList<>();
    private TrailerAdapter trailerAdapter;
    private List<ReviewList> listReview = new ArrayList<>();
    private ReviewAdapter reviewAdapter;
    private MovieDBHelper dbHelper;

    @BindView(R.id.iv_cover) ImageView iv_cover;
    @BindView(R.id.card_thumbnail) ImageView cardThumbnail;
    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.tv_overview) TextView tvOverview;
    @BindView(R.id.tv_releaseDate) TextView tvReleaseDate;
    @BindView(R.id.tv_voteCount) TextView tvVoteCount;
    @BindView(R.id.tv_noConnectionTrailer) TextView tvNoConnectionTrailer;
    @BindView(R.id.tv_noConnectionReview) TextView tvNoConnectionReview;
    @BindView(R.id.rb_rating) RatingBar rbRating;
    @BindView(R.id.btn_fav) Button btnFav;
    @BindView(R.id.rv_trailer) RecyclerView rvTrailer;
    @BindView(R.id.rv_review) RecyclerView rvReview;
    @BindView(R.id.sliding_layout) SlidingUpPanelLayout spSlidePanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new MovieDBHelper(this);
        Stetho.initializeWithDefaults(this);

        initCollapsingToolbar();

        String extraJsonData = getIntent().getStringExtra("data"); // mengambil data dari intent
        if (extraJsonData != null) {
            listData = gson.fromJson(extraJsonData, MovieList.class);
            bindData();

            btnFav.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    favorite(view);
                }
            });

            setupRecyclerviewTrailer();
            setupRecyclerviewReview();
            getData(true);
            getData(false);
            trailerAdapter.setTrailerItemClickListener(this);
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
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        pbThumbnail.setVisibility(View.GONE);
//                        return false;
//                    }
//                })
                .into(cardThumbnail);

        Glide
                .with(this)
                .load("https://image.tmdb.org/t/p/w500" + listData.getBackdropPath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .crossFade()
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        pbCover.setVisibility(View.GONE);
//                        return false;
//                    }
//                })
                .into(iv_cover);

        float floatRating = (float) (listData.getVoteAverage()/2);

        tvVoteCount.setText(String.valueOf(listData.getVoteCount()) + " Votes");
        tvTitle.setText(listData.getTitle());
        tvOverview.setText(listData.getOverview());
        rbRating.setRating(floatRating);

        dbHelper.getFavoriteMovie();

        if(dbHelper.isFavorite(listData.getId())){
            btnFav.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_red_24dp, 0, 0, 0);
            favStatus = true;
        }else{
            btnFav.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_border_red_24dp, 0, 0, 0);
            favStatus = false;
        }

        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = null;

        try {
            date = form.parse(listData.getReleaseDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat postFormater = new SimpleDateFormat("dd MMMM yyyy");
        String newDateStr = postFormater.format(date);
        tvReleaseDate.setText(newDateStr);
//        Button[] tagCategory = new Button[3];
//
//        RelativeLayout ll = (RelativeLayout)findViewById(R.id.buttonContainer);
//        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        param.setMargins(0, 5, 5, 5);
//
//        int ID = 1000;
//        for (int i = 0; i < 3; i++) {
//            tagCategory[i] = new Button(getApplicationContext());
//            tagCategory[i].setId(ID + i);
//            tagCategory[i].setBackgroundResource(R.drawable.tag_background);
//            tagCategory[i].setText("test");
//            tagCategory[i].setTextColor(getResources().getColor(black));
//            if(i == 0){
//                param.addRule(RelativeLayout.ALIGN_PARENT_START);
//            }else{
//                int a = (ID + i) - 1;
//                param.addRule(RelativeLayout.RIGHT_OF, a);
//            }
//            tagCategory[i].setLayoutParams(param);
//            ll.addView(tagCategory[i], param);
//        }
    }

    public void favorite(View v) {
        if(favStatus) {
            dbHelper.unsetFavorite(listData.getId());
            btnFav.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_border_red_24dp, 0, 0, 0);
            favStatus = false;
        }else{
            dbHelper.setFavorite(listData);
            btnFav.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_red_24dp, 0, 0, 0);
            favStatus = true;
        }
    }

    private void getData(final boolean key) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        if(key){
            ApiURL = getResources().getString(R.string.base_url) + "/movie/" + listData.getId() + "/videos?api_key=" + BuildConfig.API_KEY;
        }else{
            ApiURL = getResources().getString(R.string.base_url) + "/movie/" + listData.getId() + "/reviews?api_key=" + BuildConfig.API_KEY;
        }

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(key){
                    TrailerResponse trailerResponse = gson.fromJson(response, TrailerResponse.class);

                    listTrailer.clear();

                    // perulangan untuk object list di json
                    for (TrailerList trailerList: trailerResponse.getResults()){
                        listTrailer.add(trailerList);
                    }

                    if(trailerAdapter.getItemCount() == 0) {
                        tvNoConnectionTrailer.setVisibility(View.VISIBLE);
                        tvNoConnectionTrailer.setText("No Trailer");
                    }else{
                        tvNoConnectionTrailer.setVisibility(View.GONE);
                    }

                    trailerAdapter.notifyDataSetChanged();
                }else{
                    ReviewResponse reviewResponse = gson.fromJson(response, ReviewResponse.class);

                    listReview.clear();

                    // perulangan untuk object list di json
                    for (ReviewList reviewList: reviewResponse.getResults()){
                        listReview.add(reviewList);
                    }

                    if(reviewAdapter.getItemCount() == 0) {
                        tvNoConnectionReview.setVisibility(View.VISIBLE);
                        tvNoConnectionReview.setText("No Review");
                    }else{
                        tvNoConnectionReview.setVisibility(View.GONE);
                    }

                    reviewAdapter.notifyDataSetChanged();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tvNoConnectionTrailer.setVisibility(View.VISIBLE);
                tvNoConnectionReview.setVisibility(View.VISIBLE);

                if (error != null)
                    Log.e(TAG, "onErrorResponse: " + error.getMessage());
                else
                    Log.e(TAG, "onErrorResponse: Something wrong happened");
            }
        };

        StringRequest request = new StringRequest(
                Request.Method.GET,
                ApiURL,
                responseListener,
                errorListener
        );

        requestQueue.add(request);
    }

    private void setupRecyclerviewTrailer(){
        trailerAdapter = new TrailerAdapter(this, listTrailer);
        rvTrailer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTrailer.setAdapter(trailerAdapter);
    }

    private void setupRecyclerviewReview(){
        reviewAdapter = new ReviewAdapter(listReview);
        rvReview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvReview.setAdapter(reviewAdapter);
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
                supportFinishAfterTransition();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(spSlidePanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
            spSlidePanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        else
            super.onBackPressed();

    }

    @Override
    public void onTrailerItemClick(TrailerList data) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + data.getKey())));
    }
}
