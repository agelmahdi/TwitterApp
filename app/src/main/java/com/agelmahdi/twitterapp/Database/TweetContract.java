package com.agelmahdi.twitterapp.Database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ahmed El-Mahdi on 1/21/2018.
 */

public class TweetContract {

     static final String AUTHORITY = "com.agelmahdi.twitterapp";

     private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

     static final String PATH_FOLLOWER = "follower";


     public static final class FollowerEntry implements BaseColumns {

         public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FOLLOWER).build();

         static final String TABLE_NAME = "follower";

         public static final String COLUMN_USER_ID = "user_id";

         public static final String COLUMN_USER_NAME = "user_name";

         public static final String COLUMN_BIO = "bio";

         public static final String COLUMN_PROFILE_IMAGE = "profile_image";

         public static final String COLUMN_BG_IMAGE = "bg_image";

     }

    public static Uri buildUriWithID(long id) {
        return FollowerEntry.CONTENT_URI.buildUpon()
                .appendPath(Long.toString(id))
                .build();
    }
}
