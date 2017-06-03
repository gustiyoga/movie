package com.yopie.movie.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.yopie.movie.BuildConfig;
import com.yopie.movie.MovieDetailActivity;
import com.yopie.movie.R;
import com.yopie.movie.adapter.MovieAdapter;
import com.yopie.movie.model.MovieList;
import com.yopie.movie.model.MovieResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.ContentValues.TAG;

/**
 * Created by Yopie on 5/28/2017.
 */

public class PopularFragment extends android.support.v4.app.Fragment implements MovieAdapter.MovieItemClickListener{

    @BindView(R.id.recyclerviewPopular) RecyclerView recyclerViewPopular;
    @BindView(R.id.swipePopular) SwipeRefreshLayout swipeRefreshPopular;
    @BindView(R.id.tv_noConnection) TextView tvNoConnection;
    Gson gson = new Gson();

    private MovieAdapter popularAdapter;
    private List<MovieList> listPopularMovie = new ArrayList<>();
    private Unbinder unbinder;
    private SwipeRefreshLayout swipeContainer;

    private String APIurl;

    public PopularFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (swipeContainer!=null) {
            swipeContainer.setRefreshing(false);
            swipeContainer.destroyDrawingCache();
            swipeContainer.clearAnimation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // check connection
        setConnectedStatus(isConnected());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popular, container, false);
        unbinder = ButterKnife.bind(this, view);

        swipeContainer = (SwipeRefreshLayout) swipeRefreshPopular;
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // check connection
                setConnectedStatus(isConnected());

                fetchTimelineAsync();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        setupRecyclerview();
        popularAdapter.setMovieItemClickListener(this);
        getDataFromApi();

        // check connection
        setConnectedStatus(isConnected());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void getDataFromApi() {

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        APIurl = getResources().getString(R.string.base_url) + "/movie/popular/?api_key=" + BuildConfig.API_KEY + "&page=1";

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MovieResponse movieResponse = gson.fromJson(response, MovieResponse.class);

                listPopularMovie.clear();

                Log.e(TAG, "onResponse: " + response );
                // perulangan untuk object list di json
                for (MovieList movieList : movieResponse.getResults()){
                    listPopularMovie.add(movieList);
                }

                popularAdapter.notifyDataSetChanged();

                // remove loading for refresh
                swipeContainer.setRefreshing(false);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null)
                    Log.e(TAG, "onErrorResponse: " + error.getMessage());
                else
                    Log.e(TAG, "onErrorResponse: Something wrong happened");

                // remove loading for refresh
                swipeContainer.setRefreshing(false);
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
        popularAdapter = new MovieAdapter(listPopularMovie, getActivity());

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerViewPopular.setLayoutManager(mLayoutManager);
        recyclerViewPopular.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerViewPopular.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPopular.setAdapter(popularAdapter);

//        getDataFromApi(false);
    }

    @Override
    public void onMovieItemClick(MovieList data, ImageView ivCardThumbnail) {
        Intent intentDetail = new Intent(getActivity(), MovieDetailActivity.class);

        intentDetail.putExtra("data", gson.toJson(data)); // mengirim data ke detail activity

        Pair<View, String> p1 = Pair.create((View)ivCardThumbnail, getResources().getString(R.string.trans_poster));

        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(), p1);

        startActivity(intentDetail, transitionActivityOptions.toBundle());
    }

    public boolean isConnected() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    public void setConnectedStatus(boolean status) {
        if(status) {
            tvNoConnection.setVisibility(View.GONE);
        }else{
            tvNoConnection.setVisibility(View.VISIBLE);
        }
    }

    public void fetchTimelineAsync() {
        getDataFromApi();
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
}
