package com.agelmahdi.twitterapp;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.agelmahdi.twitterapp.Adapters.TweetAdapter;
import com.agelmahdi.twitterapp.Model.tweet;
import com.agelmahdi.twitterapp.Utils.TweetAlertDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import static com.agelmahdi.twitterapp.FollowerActivity.COL_NUM_BG_IMAGE;
import static com.agelmahdi.twitterapp.FollowerActivity.COL_NUM_ID;
import static com.agelmahdi.twitterapp.FollowerActivity.COL_NUM_PROFILE_IMAGE;
import static com.agelmahdi.twitterapp.FollowerActivity.COL_NUM_USERS_NAME;
import static com.agelmahdi.twitterapp.FollowerActivity.FOLLOWER_PROJECTION;
import static com.agelmahdi.twitterapp.Utils.Constant.CONSUMER_KEY;
import static com.agelmahdi.twitterapp.Utils.Constant.CONSUMER_SECRET;
import static com.agelmahdi.twitterapp.Utils.Constant.PREFERENCE_KEY;
import static com.agelmahdi.twitterapp.Utils.Constant.PREF_OAUTH_SECRET;
import static com.agelmahdi.twitterapp.Utils.Constant.PREF_OAUTH_TOKEN;
import static com.agelmahdi.twitterapp.Utils.Utils.isNetworkAvailable;

public class TweetActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    @Bind(R.id.tweet_rv)
    RecyclerView mRecyclerView;
    @Bind(R.id.profile_image)
    ImageView profileImage;
    @Bind(R.id.back_ground_image)
    ImageView backGroundImage;
    @Bind(R.id.profile_name)
    TextView profile_name;

    private TweetAdapter tweetAdapter;
    private ArrayList<tweet> mTweets;

    Twitter twitter;
    AccessToken accessToken;
    SharedPreferences SharedPrefs;

    private Uri mUri;

    long follower_id;

    private static final int ID_DETAIL_LOADER = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        SharedPrefs = getApplicationContext().getSharedPreferences(PREFERENCE_KEY, 0);
        ButterKnife.bind(this);
        configViews();

        if (!isNetworkAvailable(this)) {
            TweetAlertDialog dialog = new TweetAlertDialog();
            dialog.showAlertDialog(this, getString(R.string.check_network));
        }


        mUri = getIntent().getData();
        Log.e("Response", "urii :" + mUri);

        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(ID_DETAIL_LOADER, null, this);

    }

    private void configViews() {
        mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        tweetAdapter = new TweetAdapter(this, new ArrayList<tweet>());
        mRecyclerView.setAdapter(tweetAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {

            case ID_DETAIL_LOADER:

                return new CursorLoader(this,
                        mUri,
                        FOLLOWER_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;

        }

        if (!cursorHasValidData) {
            /* No data to display, simply return and do nothing */
            return;
        }
        Log.e("Response", "Data :" + data.getString(COL_NUM_USERS_NAME));

        follower_id = data.getLong(COL_NUM_ID);
        String followerName = data.getString(COL_NUM_USERS_NAME);
        String followerImage = data.getString(COL_NUM_PROFILE_IMAGE);
        String followerBgImage = data.getString(COL_NUM_BG_IMAGE);

        profile_name.setText(followerName);

        Picasso.with(TweetActivity.this)
                .load(followerImage)
                .placeholder(R.drawable.ic_black_person)
                .into(profileImage);

        Picasso.with(TweetActivity.this)
                .load(followerBgImage)
                .placeholder(R.drawable.ic_black_person)
                .into(backGroundImage);

        new GetTweets().execute(follower_id);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
