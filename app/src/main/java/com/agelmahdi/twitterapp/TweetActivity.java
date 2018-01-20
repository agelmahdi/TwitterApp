package com.agelmahdi.twitterapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.agelmahdi.twitterapp.Adapters.TweetAdapter;
import com.agelmahdi.twitterapp.Model.follower;
import com.agelmahdi.twitterapp.Model.tweet;
import com.agelmahdi.twitterapp.Utils.TweetAlertDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

import static com.agelmahdi.twitterapp.Utils.Constant.CONSUMER_KEY;
import static com.agelmahdi.twitterapp.Utils.Constant.CONSUMER_SECRET;
import static com.agelmahdi.twitterapp.Utils.Constant.FOLLOWER;
import static com.agelmahdi.twitterapp.Utils.Constant.PREFERENCE_KEY;
import static com.agelmahdi.twitterapp.Utils.Constant.PREF_OAUTH_SECRET;
import static com.agelmahdi.twitterapp.Utils.Constant.PREF_OAUTH_TOKEN;
import static com.agelmahdi.twitterapp.Utils.Constant.TWEETS;
import static com.agelmahdi.twitterapp.Utils.Utils.isNetworkAvailable;

public class TweetActivity extends AppCompatActivity {


    @Bind(R.id.tweet_rv)
    RecyclerView mRecyclerView;
    @Bind(R.id.profile_image)
    ImageView profileImage;
    @Bind(R.id.back_ground_image)
    ImageView backGrounfImage;
    @Bind(R.id.profile_name)
    TextView profile_name;

    private TweetAdapter tweetAdapter;
    private ArrayList<tweet> mTweets;
    follower follower;
    Twitter twitter;
    AccessToken accessToken;
    SharedPreferences SharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        SharedPrefs = getApplicationContext().getSharedPreferences(PREFERENCE_KEY, 0);
        ButterKnife.bind(this);
        configViews();
        Intent intent = getIntent();
        follower = intent.getParcelableExtra(FOLLOWER);
        profile_name.setText(follower.getFollowerName());

        Picasso.with(this)
                .load(follower.getFollowerImageUrl())
                .placeholder(R.drawable.ic_black_person)
                .into(profileImage);

        Picasso.with(this)
                .load(follower.getFollowerBackgroundImage())
                .placeholder(R.drawable.ic_black_person)
                .into(backGrounfImage);

        if (!isNetworkAvailable(this)) {
            TweetAlertDialog dialog = new TweetAlertDialog();
            dialog.showAlertDialog(this, getString(R.string.check_network));
        }
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(TWEETS)) {
                mTweets = savedInstanceState.getParcelableArrayList(TWEETS);
                tweetAdapter.addTweet(mTweets);
            } else {
                new GetTweets().execute(follower.getFollowerId());
            }
        } else {
            new GetTweets().execute(follower.getFollowerId());
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<tweet> tweets = tweetAdapter.getTweets();
        if (tweets != null && !tweets.isEmpty()) {
            outState.putParcelableArrayList(TWEETS, tweets);
        }
    }

    private void configViews() {
        mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        tweetAdapter = new TweetAdapter(this, new ArrayList<tweet>());
        mRecyclerView.setAdapter(tweetAdapter);
    }

    private class GetTweets extends AsyncTask<Long, String, ArrayList<tweet>> {
        @Override
        protected ArrayList<tweet> doInBackground(Long... strings) {
            mTweets = new ArrayList<>();
            try {

                TwitterFactory factory = new TwitterFactory();
                twitter = factory.getInstance();
                twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
                accessToken = new AccessToken(SharedPrefs.getString(PREF_OAUTH_TOKEN, ""),
                        SharedPrefs.getString(PREF_OAUTH_SECRET, ""));
                twitter.setOAuthAccessToken(accessToken);
                int totalTweet = 10;
                Paging paging = new Paging(1, totalTweet);
                List tweets;

                tweets = twitter.getUserTimeline(strings[0], paging);
                Log.e("response", tweets.toString());
                for (int i = 0; i < tweets.size(); i++) {
                    twitter4j.Status response = (twitter4j.Status) tweets.get(i);
                    Log.e("status", response.getText());
                    tweet tweet = new tweet(response);
                    mTweets.add(tweet);
                }

            } catch (TwitterException e) {

                e.getMessage();
            }
            return mTweets;
        }

        @Override
        protected void onPostExecute(ArrayList<tweet> tweets) {
            super.onPostExecute(tweets);
            Log.v("Tweets", mTweets.toString());
            tweetAdapter.addTweet(tweets);
        }
    }
}
