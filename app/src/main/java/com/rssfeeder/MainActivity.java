package com.rssfeeder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.rssfeeder.Handler.DbHandler;
import com.rssfeeder.VO.FeedVO;
import com.rssfeeder.RssFeedContract.FeedEntry;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    // Jihwan
    private RecyclerView mRecyclerView;
    private ArticleAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar progressBar;
    private MainViewModel viewModel;
    private RelativeLayout relativeLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        progressBar = findViewById(R.id.progressBar);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        relativeLayout = findViewById(R.id.root_layout);

        viewModel.getArticleList().observe(this, new Observer<List<FeedVO>>() {
            @Override
            public void onChanged(List<FeedVO> articles) {
                if (articles != null) {
                    mAdapter = new ArticleAdapter(articles, MainActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        viewModel.getSnackbar().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    Snackbar.make(relativeLayout, s, Snackbar.LENGTH_LONG).show();
                    viewModel.onSnackbarShowed();
                }
            }
        });

        mSwipeRefreshLayout = findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.canChildScrollUp();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mAdapter.getArticleList().clear();
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(true);
                viewModel.fetchFeed();
            }
        });


        if (!isNetworkAvailable()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.alert_message)
                    .setTitle(R.string.alert_title)
                    .setCancelable(false)
                    .setPositiveButton(R.string.alert_positive,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });

            AlertDialog alert = builder.create();
            alert.show();

        } else if (isNetworkAvailable()) {
            viewModel.fetchFeed();
        }

    }

    public boolean isNetworkAvailable() {
        /*
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
         */
        return true;
    }

    //SQLITE DB access
    //Jae
    //DB write
    public void writeArchieve(FeedVO vo){
        DbHandler dbHandler = new DbHandler(getApplicationContext());
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, vo.getTitle());
        values.put(FeedEntry.COLUMN_NAME_LINK, vo.getLink());
        values.put(FeedEntry.COLUMN_NAME_DESCRIPTION, vo.getDescription());
        values.put(FeedEntry.COLUMN_NAME_PUBLISH_DATE, vo.getPubDate());
        values.put(FeedEntry.COLUMN_NAME_AUTHOR, vo.getAuthor());
        values.put(FeedEntry.COLUMN_NAME_IMAGE_URL, vo.getImageUrl());

        long rowId = db.insert(FeedEntry.TABLE_NAME, null,values);
    }

    //DB read
    public List<FeedVO> readArchieve(){
        DbHandler dbHandler = new DbHandler(getApplicationContext());
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String order = FeedEntry.COLUMN_NAME_PUBLISH_DATE + " DESC";
        Cursor cursor = db.query(FeedEntry.TABLE_NAME,null,null,null,null,null,order,null);
        //Just link
        List<FeedVO> items = new ArrayList<>();
        while(cursor.moveToNext()) {
            String title = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE));
            String description = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_DESCRIPTION));
            String link = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_LINK));
            String author = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_AUTHOR));
            String imageUrl = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_IMAGE_URL));

            FeedVO item = new FeedVO();
            item.setTitle(title);
            item.setDescription(description);
            item.setLink(link);
            item.setAuthor(author);
            item.setImageUrl(imageUrl);

            items.add(item);
        }
        return items;
    }
    //DB delete
    public void delArchieve(String[] delItemsKeys){
        DbHandler dbHandler = new DbHandler(getApplicationContext());
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String selection = FeedEntry.COLUMN_NAME_LINK+ " LIKE ?";
        int delRows = db.delete(FeedEntry.TABLE_NAME, selection, delItemsKeys);
    }

    //DB Close
    @Override
    protected void onDestroy() {
        DbHandler dbHandler = new DbHandler(getApplicationContext());
        dbHandler.close();
        super.onDestroy();
    }

}
