package com.rssfeeder.Handler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rssfeeder.RssFeedContract.FeedEntry;
//Jae
public class DbHandler extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                    FeedEntry.COLUMN_NAME_LINK + " TEXT PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_AUTHOR + " TEXT," +
                    FeedEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    FeedEntry.COLUMN_NAME_PUBLISH_DATE + " TEXT," +
                    FeedEntry.COLUMN_NAME_IMAGE_URL + " TEXT," +
                    FeedEntry.COLUMN_NAME_IMAGE_TITLE + " TEXT," +
                    FeedEntry.COLUMN_NAME_IMAGE_LINK + " TEXT," +
                    FeedEntry.COLUMN_NAME_IMAGE_DESCRIPTION + " TEXT)";

    public DbHandler(Context context){
        super(context ,"Rss.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

}
