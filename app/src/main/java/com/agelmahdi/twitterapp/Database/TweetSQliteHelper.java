package com.agelmahdi.twitterapp.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.agelmahdi.twitterapp.Database.TweetContract.FollowerEntry.TABLE_NAME;

/**
 * Created by Ahmed El-Mahdi on 1/21/2018.
 */

public class TweetSQliteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "follower.db";
    private static final int DATABASE_VERSION = 7;

    public TweetSQliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FOLLOWER_TABLE =

                "CREATE TABLE " + TABLE_NAME + " (" +

                        TweetContract.FollowerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        TweetContract.FollowerEntry.COLUMN_USER_ID + " LONG NOT NULL, " +

                        TweetContract.FollowerEntry.COLUMN_USER_NAME + " TEXT," +

                        TweetContract.FollowerEntry.COLUMN_BIO + " TEXT, " +

                        TweetContract.FollowerEntry.COLUMN_PROFILE_IMAGE + " TEXT, " +

                        TweetContract.FollowerEntry.COLUMN_BG_IMAGE + " TEXT, " +

                        " UNIQUE (" + TweetContract.FollowerEntry.COLUMN_USER_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_FOLLOWER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
