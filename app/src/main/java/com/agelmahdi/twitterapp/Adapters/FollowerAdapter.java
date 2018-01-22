package com.agelmahdi.twitterapp.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.agelmahdi.twitterapp.FollowerActivity;
import com.agelmahdi.twitterapp.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Ahmed El-Mahdi on 1/19/2018.
 */

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.ViewHolder> {
    //private ArrayList<follower> mFollowers = new ArrayList<>();

    private Cursor mCursor;

    private Context mContext;
    private FollowerOnClickHandler mFollowerOnClickHandler;

    public interface FollowerOnClickHandler {
        void onClickFollower(long position);
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
        // final follower follower = mFollowers.get(position);

        mCursor.moveToPosition(position);

        long id = mCursor.getLong(FollowerActivity.COL_NUM_ID);
        String name = mCursor.getString(FollowerActivity.COL_NUM_USERS_NAME);
        String bio = mCursor.getString(FollowerActivity.COL_NUM_BIO);
        String imageUrl = mCursor.getString(FollowerActivity.COL_NUM_PROFILE_IMAGE);
        //String imageBgUrl = mCursor.getString(FollowerActivity.COL_NUM_PROFILE_IMAGE);

        holder.itemView.setTag(id);
        holder.followerName.setText(name);
        holder.followerBio.setText(bio);

        if (imageUrl.isEmpty()) { //url.isEmpty()
            Picasso.with(mContext)
                    .load(R.drawable.ic_black_person)
                    .placeholder(R.drawable.ic_black_person)
                    .into(holder.followerImage);

        } else {
            Picasso.with(mContext)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_black_person)
                    .into(holder.followerImage);
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

   /* public void addFollower(ArrayList<follower> followers) {
        mFollowers.clear();
        mFollowers.addAll(followers);
        notifyDataSetChanged();
    }
    public ArrayList<follower> getFollowers(){
        return mFollowers;
    }

    public void clear(){
        mFollowers.clear();
    }*/

    public Cursor swapCursor(Cursor newCursor) {

        if (mCursor == newCursor) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = newCursor;

        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
        return temp;

    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView followerName, followerBio;
        ImageView followerImage;

        ViewHolder(View itemView) {
            super(itemView);
            followerName = itemView.findViewById(R.id.follower_name);
            followerBio = itemView.findViewById(R.id.follower_bio);
            followerImage = itemView.findViewById(R.id.follower_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long id = mCursor.getLong(FollowerActivity.COL_NUM_ID);
            mFollowerOnClickHandler.onClickFollower(id);
        }
    }
}
