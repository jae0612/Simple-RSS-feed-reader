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
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    // layouts
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

        // The view model reads & shows feed articles
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        progressBar = findViewById(R.id.progressBar);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        relativeLayout = findViewById(R.id.root_layout);

        // whenever viewModel updates articles, create new article adapter
        viewModel.getArticleList().observe(this, new Observer<List<FeedVO>>() {
            @Override
            public void onChanged(List<FeedVO> articles) {
                if (articles != null) {
                    // check if new articles received, and add to the DB
                    // read articles from DB
                    List<FeedVO> list = readArchieve();
                    boolean isNew=true;
                    for(FeedVO fvo : articles){
                        // check if new article read
                        for(FeedVO comp : list){
                            if(fvo.equals(comp)){
                                // this  article is already in the DB
                                isNew=false;
                                break;
                            }
                        }

                        if(isNew){
                            // save the new article in the DB
                            writeArchieve(fvo);
                            list.add(fvo);
                        }
                    }


                    // create article adapter
                    mAdapter = new ArticleAdapter(list, MainActivity.this);
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
                mAdapter.getArticleList().clear(); // clear the previous articles list when refreshed
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(true);
                viewModel.fetchFeed(); //read articles
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
            // first read feed
            viewModel.fetchFeed();
        }

    }

    // Check the network
    public boolean isNetworkAvailable() {
        /*
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
         */
        return true;
    }


    // Add Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Menu item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            // 'About' clicked
            androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle(R.string.app_name);
            alertDialog.setMessage(Html.fromHtml(MainActivity.this.getString(R.string.info_text) +
                    MainActivity.this.getString(R.string.author)));
            alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            ((TextView) alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        }else if (id == R.id.add_feeder) {
            // 'Add a Feeder' clicked
            androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Add a Feeder");
            alertDialog.setMessage("Enter a feeder URL: ");

            final EditText input = new EditText(MainActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input); // uncomment this line

            alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String url = input.getText().toString();
                            viewModel.addFeeder(url);
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            ((TextView) alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        }else if (id == R.id.delete_feeder) {
            // 'Delete Feeders' clicked

            final String[] feedersList = viewModel.getFeeders();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select feeders to delete");

            final boolean[] checkedItems = new boolean[feedersList.length]; //this will checked the items when user open the dialog
            builder.setMultiChoiceItems(feedersList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    //Toast.makeText(this, "Position: " + which + " Value: " + feedersList[which] + " State: " + (isChecked ? "checked" : "unchecked"), Toast.LENGTH_LONG).show();
                }
            });

            builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // delete selected feeder from the list
                    for(int i=0; i<checkedItems.length; i++){
                        if(checkedItems[i]){
                            viewModel.removeFeeder(feedersList[i]);
                        }
                    }

                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            // ---------------------------
        }else if (id == R.id.show_favorite) {
            // 'Favorite' clicked
            final String[] feedersList = viewModel.getFeeders();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select feeders to delete");

            final boolean[] checkedItems = new boolean[feedersList.length]; //this will checked the items when user open the dialog
            builder.setMultiChoiceItems(feedersList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    //Toast.makeText(this, "Position: " + which + " Value: " + feedersList[which] + " State: " + (isChecked ? "checked" : "unchecked"), Toast.LENGTH_LONG).show();
                }
            });

            builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // delete selected feeder from the list
                    for(int i=0; i<checkedItems.length; i++){
                        if(checkedItems[i]){
                            viewModel.removeFeeder(feedersList[i]);
                        }
                    }

                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            // ---------------------------
        }



        return super.onOptionsItemSelected(item);
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
        values.put(FeedEntry.COLUMN_NAME_FAVORITE, vo.isFavorite());

        long rowId = db.insert(FeedEntry.TABLE_NAME, null,values);
    }

    //DB read
    public List<FeedVO> readArchieve(){
        DbHandler dbHandler = new DbHandler(getApplicationContext());
        SQLiteDatabase db = dbHandler.getReadableDatabase();

        // Reset DB
        //db.execSQL("delete from "+ FeedEntry.TABLE_NAME);

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
            boolean favorite = Boolean.parseBoolean(cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_FAVORITE)));

            // Restore a FeedVO object
            FeedVO item = new FeedVO();
            item.setTitle(title);
            item.setDescription(description);
            item.setLink(link);
            item.setAuthor(author);
            item.setImageUrl(imageUrl);
            item.setFavorite(favorite);

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
