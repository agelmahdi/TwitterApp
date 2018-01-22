package com.agelmahdi.twitterapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.agelmahdi.twitterapp.Adapters.FollowerAdapter;
import com.agelmahdi.twitterapp.Database.TweetContract;
import com.agelmahdi.twitterapp.Model.follower;
import com.agelmahdi.twitterapp.Utils.TweetAlertDialog;

import java.util.ArrayList;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

import static com.agelmahdi.twitterapp.Database.TweetContract.FollowerEntry.COLUMN_BG_IMAGE;
import static com.agelmahdi.twitterapp.Database.TweetContract.FollowerEntry.COLUMN_BIO;
import static com.agelmahdi.twitterapp.Database.TweetContract.FollowerEntry.COLUMN_PROFILE_IMAGE;
import static com.agelmahdi.twitterapp.Database.TweetContract.FollowerEntry.COLUMN_USER_ID;
import static com.agelmahdi.twitterapp.Database.TweetContract.FollowerEntry.COLUMN_USER_NAME;
import static com.agelmahdi.twitterapp.Utils.Constant.CONSUMER_KEY;
import static com.agelmahdi.twitterapp.Utils.Constant.CONSUMER_SECRET;
import static com.agelmahdi.twitterapp.Utils.Constant.PREFERENCE_KEY;
import static com.agelmahdi.twitterapp.Utils.Constant.PREF_OAUTH_SECRET;
import static com.agelmahdi.twitterapp.Utils.Constant.PREF_OAUTH_TOKEN;
import static com.agelmahdi.twitterapp.Utils.Utils.isNetworkAvailable;

public class FollowerActivity extends AppCompatActivity implements FollowerAdapter.FollowerOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = FollowerActivity.class.getName();
    Twitter twitter;
    AccessToken accessToken;
    SharedPreferences SharedPrefs;
    ArrayList<follower> mFollowers;
    private FollowerAdapter mFollowerAdapter;
    private follower follower;

    RecyclerView mRecyclerView;

    RecyclerView mRecyclerViewPortrait;

    SwipeRefreshLayout swipeRefreshLayout;

    Toolbar toolbar;
    private LoaderManager mLoaderManager;

    private static final int TASK_LOADER_ID = 0;

    public static final String[] FOLLOWER_PROJECTION = {

            COLUMN_USER_ID,
            COLUMN_USER_NAME,
            COLUMN_BIO,
            COLUMN_PROFILE_IMAGE,
            COLUMN_BG_IMAGE,

    };
    public static final int COL_NUM_ID = 0;
    public static final int COL_NUM_USERS_NAME = 1;
    public static final int COL_NUM_BIO = 2;
    public static final int COL_NUM_PROFILE_IMAGE = 3;
    public static final int COL_NUM_BG_IMAGE = 4;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);
        toolbar = (Toolbar) findViewById(R.id.toolbarF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mLoaderManager = getSupportLoaderManager();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerViewPortrait = (RecyclerView) findViewById(R.id.recycler_view_portrait);
        mFollowers = new ArrayList<>();
        SharedPrefs = getApplicationContext().getSharedPreferences(PREFERENCE_KEY, 0);
        configViews();

        if (CONSUMER_KEY.trim().isEmpty() || CONSUMER_SECRET.trim().isEmpty()) {
            TweetAlertDialog dialog = new TweetAlertDialog();
            dialog.showAlertDialog(this, getString(R.string.missed_token));
        }
        if (!isNetworkAvailable(this)) {
            TweetAlertDialog dialog = new TweetAlertDialog();
            dialog.showAlertDialog(this, getString(R.string.check_network));
        } else {
            swipeRefreshLayout.setRefreshing(true);
            new GetFollowers().execute();
        }

        mLoaderManager.initLoader(TASK_LOADER_ID, null, this);
    }

    void portraitConfiguration() {
        if (findViewById(R.id.recycler_view_portrait) != null) {
            mRecyclerViewPortrait.setRecycledViewPool(new RecyclerView.RecycledViewPool());
            mRecyclerViewPortrait.setLayoutManager(new GridLayoutManager(this, 3));
            mRecyclerViewPortrait.setItemAnimator(new DefaultItemAnimator());
            mRecyclerViewPortrait.setAdapter(mFollowerAdapter);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        portraitConfiguration();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                if (mFollowers != null && mFollowerAdapter != null) {
                    mFollowers.clear();
                    new GetFollowers().execute();
                }

            }
        });

        mLoaderManager.restartLoader(TASK_LOADER_ID, null, this);

    }

    private void configViews() {
        if (findViewById(R.id.recycler_view) != null) {
            mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mFollowerAdapter = new FollowerAdapter(this, this);
            mRecyclerView.setAdapter(mFollowerAdapter);
        }
    }

    @Override
    public void onClickFollower(long position) {
        Intent intent = new Intent(this, TweetActivity.class);
        Uri uriIdClicked = TweetContract.buildUriWithID(position);
        intent.setData(uriIdClicked);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {

            case TASK_LOADER_ID:
                return new CursorLoader(this, TweetContract.FollowerEntry.CONTENT_URI,
                        FOLLOWER_PROJECTION, null, null, COLUMN_USER_ID);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFollowerAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private class GetFollowers extends AsyncTask<String, String, ArrayList<follower>> {
        @Override
        protected ArrayList<follower> doInBackground(String... strings) {
            try {

                TwitterFactory factory = new TwitterFactory();
                twitter = factory.getInstance();
                twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
                accessToken = new AccessToken(SharedPrefs.getString(PREF_OAUTH_TOKEN, ""),
                        SharedPrefs.getString(PREF_OAUTH_SECRET, ""));
                twitter.setOAuthAccessToken(accessToken);
                String currentUserName = twitter.getScreenName();

                IDs ids;
                long cursor = -1;
                do {
                    ids = twitter.getFollowersIDs(currentUserName, cursor);

                    for (long id : ids.getIDs()) {
                        User user = twitter.showUser(id);
                        follower = new follower(user);
                        mFollowers.add(follower);
                    }

                }
                while ((cursor = ids.getNextCursor()) != 0);

            } catch (TwitterException e) {

                e.getMessage();
            }
            return mFollowers;
        }

        @Override
        protected void onPostExecute(ArrayList<follower> followers) {
            super.onPostExecute(followers);

            swipeRefreshLayout.setRefreshing(false);

            for (int i = 0; i < followers.size(); i++) {
                follower follower = followers.get(i);
                PersistData persistData = new PersistData();
                persistData.execute(follower);
            }

        }
    }

    private class PersistData extends AsyncTask<follower, Void, Void> {
        @Override
        protected final Void doInBackground(follower... data) {

            follower followe = data[0];
            try {
                ContentValues follow = new ContentValues();
                follow.put(COLUMN_USER_ID, followe.getFollowerId());
                follow.put(COLUMN_USER_NAME, followe.getFollowerName());
                follow.put(COLUMN_BIO, followe.getFollowerBio());
                follow.put(COLUMN_PROFILE_IMAGE, followe.getFollowerImageUrl());
                follow.put(COLUMN_BG_IMAGE, followe.getFollowerBackgroundImage());

                getContentResolver().insert(TweetContract.FollowerEntry.CONTENT_URI, follow);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }

            return null;
        }
    }


}
