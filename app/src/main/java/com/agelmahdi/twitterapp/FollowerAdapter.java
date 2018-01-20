package com.agelmahdi.twitterapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed El-Mahdi on 1/19/2018.
 */

class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.ViewHolder> {
    private ArrayList<follower> mFollowers = new ArrayList<>();
    private Context mContext;

    FollowerAdapter(Context c) {
        this.mContext = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.follower_list_item, parent, false);
        return new FollowerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final follower follower = mFollowers.get(position);

        holder.followerName.setText(follower.getFollowerName());
        holder.followerBio.setText(follower.getFollowerBio());
        holder.itemView.setTag(follower.getFollowerId());

        if (follower.getFollowerImageUrl().isEmpty()) { //url.isEmpty()
            Picasso.with(mContext)
                    .load(R.drawable.ic_black_person)
                    .placeholder(R.drawable.ic_black_person)
                    .into(holder.followerImage);

        } else {
            Picasso.with(mContext)
                    .load(follower.getFollowerImageUrl())
                    .placeholder(R.drawable.ic_black_person)
                    .into(holder.followerImage);
        }

    }
    @Override
    public int getItemCount() {
        return mFollowers.size();
    }

    void addFollower(ArrayList<follower> followers) {
        mFollowers.clear();
        mFollowers.addAll(followers);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView followerName, followerBio;
        ImageView followerImage;

        ViewHolder(View itemView) {
            super(itemView);
            followerName = itemView.findViewById(R.id.follower_name);
            followerBio = itemView.findViewById(R.id.follower_bio);
            followerImage = itemView.findViewById(R.id.follower_image);
        }
    }
}
