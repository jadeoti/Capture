package com.jadeoti.capture.data.database.person_repo;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.jadeoti.capture.provider.person.PersonColumns;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import timber.log.Timber;

/**
 * Created by Morph-Deji on 23-Mar-17.
 */

public class LoaderProvider {

    @NonNull
    private final Context mContext;

    public LoaderProvider(@NonNull Context context) {
        if(context == null) throw new NullPointerException("context cannot be null");
        mContext = context;
    }

    public Loader<Cursor> createFilteredProfileLoader(@TypeFilter String typeFilter) {
        String selection = null;
        String[] selectionArgs = null;

        switch (typeFilter) {
            case ALL:
                selection = null;
                selectionArgs = null;
                break;
            case PENDING:
                selection = PersonColumns.SYNC_FAILED + " = ? ";
                selectionArgs = new String[]{"1"};
                break;
            case SYNCED:
                selection = PersonColumns.SYNC_FAILED + " = ? ";
                selectionArgs = new String[]{"0"};
                break;
        }

        Timber.d("provider loading: %s", typeFilter);

        return new CursorLoader(
                mContext,
                PersonColumns.CONTENT_URI,
                PersonColumns.ALL_COLUMNS, selection, selectionArgs, null
        );


    }

    public Loader<Cursor> createProfileLoader(String personId) {
        Uri uri = PersonColumns.CONTENT_URI.buildUpon().appendPath(personId).build();
        return new CursorLoader(mContext, uri,
                null,
                null,
                new String[]{String.valueOf(personId)}, null
        );
    }


    public final static String ALL = "all";
    public final static String SYNCED = "synced";
    public final static String PENDING = "pending";

    @StringDef({ALL,
            SYNCED,
            PENDING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TypeFilter {
    }


}
