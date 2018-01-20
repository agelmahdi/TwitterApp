package com.agelmahdi.twitterapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.agelmahdi.twitterapp.Adapters.FollowerAdapter;
import com.agelmahdi.twitterapp.Model.follower;
import com.agelmahdi.twitterapp.Model.tweet;
import com.agelmahdi.twitterapp.Utils.TweetAlertDialog;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import twitter4j.IDs;
import twitter4j.Paging;
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
import static com.agelmahdi.twitterapp.Utils.Utils.isNetworkAvailable;

public class FollowerActivity extends AppCompatActivity implements FollowerAdapter.FollowerOnClickHandler {
    Twitter twitter;
    AccessToken accessToken;
    SharedPreferences SharedPrefs;
    ArrayList<follower> mFollowers;
    private FollowerAdapter mFollowerAdapter;


    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);
        mFollowers = new ArrayList<>();
        ButterKnife.bind(this);
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                if (mFollowers != null && mFollowerAdapter != null) {
                    mFollowers.clear();
                    mFollowerAdapter.clear();
                    new GetFollowers().execute();
                }

            }
        });
    }

    private void configViews() {
        mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mFollowerAdapter = new FollowerAdapter(this, this);
        mRecyclerView.setAdapter(mFollowerAdapter);
    }

    @Override
    public void onClickFollower(follower follower, int position) {
        Intent intent = new Intent(this, TweetActivity.class);
        intent.putExtra(FOLLOWER, follower);
        startActivity(intent);
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
            if (mFollowerAdapter != null) {
                mFollowerAdapter.addFollower(followers);
            }
            swipeRefreshLayout.setRefreshing(false);

        }
    }

}
