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
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.yopie.movie.MovieDetailActivity;
import com.yopie.movie.R;
import com.yopie.movie.adapter.MovieAdapter;
import com.yopie.movie.database.MovieDBHelper;
import com.yopie.movie.model.MovieList;
import com.yopie.movie.model.MovieResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Yopie on 5/28/2017.
 */

public class FavoritFragment extends android.support.v4.app.Fragment implements MovieAdapter.MovieItemClickListener{

    @BindView(R.id.recyclerviewFavorit) RecyclerView recyclerViewFavorit;
    @BindView(R.id.swipeFavorit) SwipeRefreshLayout swipeRefreshFavorit;

    private MovieAdapter favoritAdapter;
    private List<MovieList> listFavoritMovie = new ArrayList<>();
    private Unbinder unbinder;
    private SwipeRefreshLayout swipeContainer;
    private MovieDBHelper dbHelper;
    Gson gson = new Gson();

    public FavoritFragment() {}

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorit, container, false);
        unbinder = ButterKnife.bind(this, view);

        dbHelper = new MovieDBHelper(getActivity());

        swipeContainer = (SwipeRefreshLayout) swipeRefreshFavorit;
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        setupRecyclerview();
        favoritAdapter.setMovieItemClickListener(this);
        showDataFromDB(dbHelper.getFavoriteMovie());

        return view;

    }

    private void showDataFromDB(MovieResponse data) {
        listFavoritMovie.clear();
        for (MovieList item : data.getResults()) {
            listFavoritMovie.add(item);
        }
        favoritAdapter.notifyDataSetChanged();
        favoritAdapter.setMovieItemClickListener(this);

        // remove loading for refresh
        swipeContainer.setRefreshing(false);
    }

    public void fetchTimelineAsync() {
        showDataFromDB(dbHelper.getFavoriteMovie());
    }

    private void setupRecyclerview(){
        favoritAdapter = new MovieAdapter(listFavoritMovie, getActivity());

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerViewFavorit.setLayoutManager(mLayoutManager);
        recyclerViewFavorit.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerViewFavorit.setItemAnimator(new DefaultItemAnimator());
        recyclerViewFavorit.setAdapter(favoritAdapter);
    }

    @Override
    public void onMovieItemClick(MovieList data, ImageView ivCardThumbnail) {
        Intent intentDetail = new Intent(getActivity(), MovieDetailActivity.class);

        intentDetail.putExtra("data", gson.toJson(data)); // mengirim data ke detail activity

        Pair<View, String> p1 = Pair.create((View)ivCardThumbnail, getResources().getString(R.string.trans_poster));

        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(), p1);

        startActivity(intentDetail, transitionActivityOptions.toBundle());
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
