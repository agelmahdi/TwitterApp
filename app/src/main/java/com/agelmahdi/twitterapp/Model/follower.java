package com.agelmahdi.twitterapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import twitter4j.TwitterException;
import twitter4j.User;

/**
 * Created by Ahmed El-Mahdi on 1/19/2018.
 */

public class follower implements Parcelable {
    private long followerId;
    private String followerName;
    private String followerBio;
    private String followerImageUrl;
    private String followerBackgroundImage;

    protected follower(Parcel in) {
        followerId = in.readLong();
        followerName = in.readString();
        followerBio = in.readString();
        followerImageUrl = in.readString();
        followerBackgroundImage = in.readString();
    }

    public static final Creator<follower> CREATOR = new Creator<follower>() {
        @Override
        public follower createFromParcel(Parcel in) {
            return new follower(in);
        }

        @Override
        public follower[] newArray(int size) {
            return new follower[size];
        }
    };

    public String getFollowerImageUrl() {
        return followerImageUrl;
    }

    public follower(User user) throws TwitterException {
        this.followerId = user.getId();
        this.followerName = user.getName();
        this.followerBio = user.getDescription();
        this.followerImageUrl = user.getBiggerProfileImageURL();
        this.followerBackgroundImage = user.getProfileBannerURL();

    }

    public String getFollowerBackgroundImage() {
        return followerBackgroundImage;
    }

    public long getFollowerId() {
        return followerId;
    }

    public String getFollowerName() {
        return followerName;
    }

    public String getFollowerBio() {
        return followerBio;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(followerId);
        parcel.writeString(followerName);
        parcel.writeString(followerBio);
        parcel.writeString(followerImageUrl);
        parcel.writeString(followerBackgroundImage);
    }
}
