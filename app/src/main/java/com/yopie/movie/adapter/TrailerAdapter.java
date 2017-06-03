package com.yopie.movie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yopie.movie.R;
import com.yopie.movie.model.TrailerList;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Yopie on 6/2/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<TrailerList> listData = new ArrayList<>();

    private TrailerItemClickListener mTrailerItemClickListener;

    public TrailerAdapter(Context context, List<TrailerList> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_card, parent, false);
        return new TrailerItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final TrailerItemViewHolder trailerItemViewHolder = (TrailerItemViewHolder) holder; // casting view holder menjadi ForecastItemViewHolder
        final TrailerList data = listData.get(position);
        trailerItemViewHolder.bind(data, context);

        // holder.itemView adalah item view pada list
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTrailerItemClickListener != null)
                    mTrailerItemClickListener.onTrailerItemClick(data);
                else
                    Log.e(TAG, "Error onClick listener");
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void setTrailerItemClickListener(TrailerAdapter.TrailerItemClickListener clickListener) {
        if (clickListener != null)
            mTrailerItemClickListener = clickListener;
    }

    // Inner interface class utk click listener pada recyclerview
    public interface TrailerItemClickListener {
        void onTrailerItemClick(TrailerList data);
    }
}
