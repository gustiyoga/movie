package com.yopie.movie;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.yopie.movie.adapter.MovieAdapter;
import com.yopie.movie.model.MovieList;
import com.yopie.movie.model.MovieResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    @BindView(R.id.recyclerview) RecyclerView recyclerView;
    MovieAdapter adapter;
    private List<MovieList> listMovie = new ArrayList<>();
    private Gson gson = new Gson();

    SharedPreferences sharedpreferences;
    private String APIurl;
    private String sortBy = "popular";
    private Boolean configSortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        setupSharedPreferences();

        setupRecyclerview();

        adapter.setMovieItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Setting:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setupSharedPreferences() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        configSortBy = sharedPreferences.getBoolean("sort_by", true);

        if(configSortBy)
            sortBy = "popular";
        else
            sortBy = "top_rated";

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public void getDataFromApi() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        APIurl = getResources().getString(R.string.base_url) + "/movie/" + sortBy + "?api_key=" + BuildConfig.API_KEY + "&page=1";

        Log.e(TAG, APIurl);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MovieResponse movieResponse = gson.fromJson(response, MovieResponse.class);

                listMovie.clear();

                // perulangan untuk object list di json
                for (MovieList movieList : movieResponse.getResults()){
                    listMovie.add(movieList);
                }

                adapter.notifyDataSetChanged();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null)
                    Log.e(TAG, "onErrorResponse: " + error.getMessage());
                else
                    Log.e(TAG, "onErrorResponse: Something wrong happened");
            }
        };

        StringRequest request = new StringRequest(
                Request.Method.GET,
                APIurl,
                responseListener,
                errorListener
        );

        requestQueue.add(request);
    }

    private void setupRecyclerview(){
        adapter = new MovieAdapter(listMovie, this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        getDataFromApi();
    }

    @Override
    public void onMovieItemClick(MovieList data, ImageView ivCardThumbnail, TextView tvTitle) {
        Intent intentDetail = new Intent(MainActivity.this, MovieDetailActivity.class);

        intentDetail.putExtra("data", gson.toJson(data)); // mengirim data ke detail activity

        Pair<View, String> p1 = Pair.create((View)ivCardThumbnail, getResources().getString(R.string.trans_poster));
        Pair<View, String> p2 = Pair.create((View)tvTitle, getResources().getString(R.string.trans_title));

        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this, p1, p2);
        startActivity(intentDetail, transitionActivityOptions.toBundle());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("sort_by")) {
            configSortBy = sharedPreferences.getBoolean(key, true);

            if(configSortBy)
                sortBy = "popular";
            else
                sortBy = "top_rated";

            getDataFromApi();
        }
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
