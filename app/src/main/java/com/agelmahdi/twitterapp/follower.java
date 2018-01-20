package com.agelmahdi.twitterapp;

import android.os.Parcel;
import android.os.Parcelable;

import twitter4j.TwitterException;
import twitter4j.User;

/**
 * Created by Ahmed El-Mahdi on 1/19/2018.
 */

 class follower  {
    private long followerId;
    private String followerName;
    private String followerBio;
    private String followerImageUrl;

     String getFollowerImageUrl() {
        return followerImageUrl;
    }

    follower(User user)throws TwitterException{
        this.followerId = user.getId();
        this.followerName = user.getName();
        this.followerBio=user.getDescription();
        this.followerImageUrl = user.getProfileImageURL();

    }

     long getFollowerId() {
        return followerId;
    }

     String getFollowerName() {
        return followerName;
    }

     String getFollowerBio() {
        return followerBio;
    }

}
