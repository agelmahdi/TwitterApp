package com.agelmahdi.twitterapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.api.TimelinesResources;

/**
 * Created by Ahmed El-Mahdi on 1/20/2018.
 */

public class tweet implements Parcelable{
    private String followerTweetName;
    private String tweetStatus;

    public tweet(Status resources) {
        this.tweetStatus = resources.getText();

    }

    protected tweet(Parcel in) {
        followerTweetName = in.readString();
        tweetStatus = in.readString();
    }

    public static final Creator<tweet> CREATOR = new Creator<tweet>() {
        @Override
        public tweet createFromParcel(Parcel in) {
            return new tweet(in);
        }

        @Override
        public tweet[] newArray(int size) {
            return new tweet[size];
        }
    };

    public String getFollowerTweetName() {
        return followerTweetName;
    }

    public String getTweetStatus() {
        return tweetStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(followerTweetName);
        parcel.writeString(tweetStatus);
    }
}
