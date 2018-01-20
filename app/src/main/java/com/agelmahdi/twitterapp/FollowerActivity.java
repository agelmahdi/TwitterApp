package com.agelmahdi.twitterapp;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

import static com.agelmahdi.twitterapp.Constant.CONSUMER_KEY;
import static com.agelmahdi.twitterapp.Constant.CONSUMER_SECRET;
import static com.agelmahdi.twitterapp.Constant.PREFERENCE_KEY;
import static com.agelmahdi.twitterapp.Constant.PREF_OAUTH_SECRET;
import static com.agelmahdi.twitterapp.Constant.PREF_OAUTH_TOKEN;
import static com.agelmahdi.twitterapp.Utils.isNetworkAvailable;

public class FollowerActivity extends AppCompatActivity {
    Twitter twitter;
    AccessToken accessToken;
    SharedPreferences SharedPrefs;
    ArrayList<follower> mFollowers;
    private FollowerAdapter mFollowerAdapter;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);
        ButterKnife.bind(this);
        SharedPrefs = getApplicationContext().getSharedPreferences(PREFERENCE_KEY, 0);
        configViews();

        if (isNetworkAvailable(this)) {
            if (!CONSUMER_KEY.trim().isEmpty() || !CONSUMER_SECRET.trim().isEmpty()) {
                new GetFollowers().execute();
            }
        } else {
            Toast.makeText(this, "NO Connection", Toast.LENGTH_SHORT).show();
        }
    }



    private void configViews() {
        mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mFollowerAdapter = new FollowerAdapter(this);
        mRecyclerView.setAdapter(mFollowerAdapter);
    }

    private class GetFollowers extends AsyncTask<String, String, ArrayList<follower>> {
        @Override
        protected ArrayList<follower> doInBackground(String... strings) {
            mFollowers = new ArrayList<>();
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
                        final follower follower = new follower(user);
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
            if (mFollowerAdapter!=null){
                mFollowerAdapter.addFollower(followers);
            }
        }
    }

}
