package com.agelmahdi.twitterapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agelmahdi.twitterapp.Model.tweet;
import com.agelmahdi.twitterapp.R;

import java.util.ArrayList;

/**
 * Created by Ahmed El-Mahdi on 1/20/2018.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    private ArrayList<tweet> mTweets;
    private Context mContext;

    public TweetAdapter(Context context, ArrayList<tweet> mTweets) {
        this.mContext = context;
        this.mTweets = mTweets;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.tweet_list_item, parent, false);
        return new TweetAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final tweet tweet = mTweets.get(position);
        holder.tweetView.setText(tweet.getTweetStatus());

    }

    public void addTweet(ArrayList<tweet> tweets) {
        mTweets.clear();
        mTweets.addAll(tweets);
        notifyDataSetChanged();
    }

    public ArrayList<tweet> getTweets() {
        return mTweets;
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tweetView;

        public ViewHolder(View itemView) {
            super(itemView);
            tweetView = itemView.findViewById(R.id.tweet_text);
        }
    }
}
