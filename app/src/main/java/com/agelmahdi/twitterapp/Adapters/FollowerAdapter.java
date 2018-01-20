package com.agelmahdi.twitterapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.agelmahdi.twitterapp.Model.follower;
import com.agelmahdi.twitterapp.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by Ahmed El-Mahdi on 1/19/2018.
 */

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.ViewHolder> {
    private ArrayList<follower> mFollowers = new ArrayList<>();
    private Context mContext;
    private FollowerOnClickHandler mFollowerOnClickHandler;

    public interface FollowerOnClickHandler {
        void onClickFollower(follower follower, int position);
    }

    public FollowerAdapter(Context c, FollowerOnClickHandler followerOnClickHandler) {
        this.mContext = c;
        this.mFollowerOnClickHandler = followerOnClickHandler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.follower_list_item, parent, false);
        return new FollowerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
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
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFollowerOnClickHandler.onClickFollower(follower, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFollowers.size();
    }

    public void addFollower(ArrayList<follower> followers) {
        mFollowers.clear();
        mFollowers.addAll(followers);
        notifyDataSetChanged();
    }
    public ArrayList<follower> getFollowers(){
        return mFollowers;
    }

    public void clear(){
        mFollowers.clear();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView followerName, followerBio;
        ImageView followerImage;
        public final View mView;

        ViewHolder(View itemView) {
            super(itemView);
            followerName = itemView.findViewById(R.id.follower_name);
            followerBio = itemView.findViewById(R.id.follower_bio);
            followerImage = itemView.findViewById(R.id.follower_image);
            mView = itemView;
        }
    }
}
