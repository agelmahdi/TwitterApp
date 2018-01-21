package com.agelmahdi.twitterapp.Database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.agelmahdi.twitterapp.Database.TweetContract.FollowerEntry.TABLE_NAME;

/**
 * Created by Ahmed El-Mahdi on 1/21/2018.
 */

public class TweetProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private TweetSQliteHelper mOpenHelper;

    private static final int FOLLOWER = 200;
    private static final int FOLLOWER_WITH_ID = 201;


    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(TweetContract.AUTHORITY, TweetContract.PATH_FOLLOWER, FOLLOWER);

        matcher.addURI(TweetContract.AUTHORITY, TweetContract.PATH_FOLLOWER + "/#", FOLLOWER_WITH_ID);

        return matcher;

    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new TweetSQliteHelper(getContext());
        return true;    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        Cursor retCursor;


        // Query for the tasks directory and write a default case
        switch (match) {
            // Query for the tasks directory
            case FOLLOWER:
                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case FOLLOWER_WITH_ID:

                String normalizedUtcDateString = uri.getLastPathSegment();

                String[] selectionArguments = new String[]{normalizedUtcDateString};
                retCursor = db.query(TABLE_NAME,
                        projection,
                        TweetContract.FollowerEntry.COLUMN_USER_ID+ " = ?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;

            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FOLLOWER:

                long id = db.insert(TABLE_NAME, null, contentValues);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(TweetContract.FollowerEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
