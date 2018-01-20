package com.agelmahdi.twitterapp;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.agelmahdi.twitterapp.Utils.TweetAlertDialog;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import static com.agelmahdi.twitterapp.Utils.Constant.CONSUMER_KEY;
import static com.agelmahdi.twitterapp.Utils.Constant.CONSUMER_SECRET;
import static com.agelmahdi.twitterapp.Utils.Constant.PREFERENCE_KEY;
import static com.agelmahdi.twitterapp.Utils.Constant.PREF_LOGIN;
import static com.agelmahdi.twitterapp.Utils.Constant.PREF_OAUTH_SECRET;
import static com.agelmahdi.twitterapp.Utils.Constant.PREF_OAUTH_TOKEN;
import static com.agelmahdi.twitterapp.Utils.Constant.TWITTER_OAUTH_VERIFIER;
import static com.agelmahdi.twitterapp.Utils.Utils.isNetworkAvailable;


public class MainActivity extends AppCompatActivity {

    TweetAlertDialog dialog = new TweetAlertDialog();

    private static SharedPreferences preferences;

    ImageView login;
    Dialog d;
    WebView webView;

    // Twitter
    Twitter twitter;
    RequestToken requestToken;
    AccessToken accessToken;

    String auth_url;
    String verifier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Shared Pref Initialization
        preferences = getApplicationContext().getSharedPreferences(PREFERENCE_KEY, 0);
        login = (ImageView) findViewById(R.id.login);
        //Check Network Availability
        if (!isNetworkAvailable(this)) {
            dialog.showAlertDialog(this, getString(R.string.check_network));
        }
        //Check CONSUMER KEY & SECRET KEY Availability

        if (CONSUMER_KEY.trim().isEmpty() || CONSUMER_SECRET.trim().isEmpty()) {
            dialog.showAlertDialog(this, getString(R.string.missed_token));
            //Get Consumer key & Secret and pass it to twitter instance
        }
        if (!isLoggedIn()) {
            twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        } else {
            // user already logged into twitter
            Intent intent = new Intent(MainActivity.this, FollowerActivity.class);
            startActivity(intent);
            finish();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoginTwitter().execute();
            }
        });
    }

    private class TokenAuthentication extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                //Get Access Token & Save It To Shared Preference
                accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(PREF_OAUTH_TOKEN, accessToken.getToken());
                editor.putString(PREF_OAUTH_SECRET, accessToken.getTokenSecret());
                editor.putBoolean(PREF_LOGIN, true);
                editor.apply();
            } catch (TwitterException e) {
                e.getMessage();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            //Navigate to Follower Screen And Finish activity
            Intent intent = new Intent(MainActivity.this, FollowerActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private class LoginTwitter extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            //Request Token & return auther url
            if (!isLoggedIn()) {
                try {
                    requestToken = twitter.getOAuthRequestToken();
                    auth_url = requestToken.getAuthorizationURL();

                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            } else {
                Intent intent = new Intent(MainActivity.this, FollowerActivity.class);
                startActivity(intent);
                finish();
            }
            return auth_url;
        }

        @Override
        protected void onPostExecute(String s) {
            if (auth_url != null) {
                Log.e("URL", auth_url);
                d = new Dialog(MainActivity.this);
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //Set web view layout to Dialog
                d.setContentView(R.layout.auth_web_view);
                webView = d.findViewById(R.id.web_view);
                webView.getSettings().setJavaScriptEnabled(true);
                //load url to web view
                webView.loadUrl(auth_url);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (url.contains(TWITTER_OAUTH_VERIFIER)) {
                            Log.e("Url", url);
                            Uri uri = Uri.parse(url);
                            verifier = uri.getQueryParameter(TWITTER_OAUTH_VERIFIER);
                            d.dismiss();
                            new TokenAuthentication().execute();
                        } else if (url.contains("denied")) {
                            d.dismiss();
                            dialog.showAlertDialog(MainActivity.this, getString(R.string.access_denied));
                        }
                    }
                });
                d.show();
                d.setCancelable(true);
            }
        }
    }

    private boolean isLoggedIn() {
        // return login Shared Preferences
        return preferences.getBoolean(PREF_LOGIN, false);
    }
}