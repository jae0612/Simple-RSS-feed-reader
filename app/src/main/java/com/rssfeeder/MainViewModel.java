package com.rssfeeder;

import android.os.StrictMode;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.rssfeeder.VO.FeedVO;
//Jae
public class MainViewModel extends ViewModel implements RssListener{

    private MutableLiveData<List<FeedVO>> articleListLive = null;
    private ArrayList<String> feeders = new ArrayList<>();


    private MutableLiveData<String> snackbar = new MutableLiveData<>();

    public MainViewModel(){
        super();
        // Test feeders
        addFeeder("https://www.androidauthority.com/feed");
        addFeeder("https://www.cnet.com/rss/news/");
    }


    // add URL if not already exists
    public void addFeeder(String url){
        boolean exists = false;
        for(String s : feeders){
            if(s.equals(url)) {
                exists = true;
                break;
            }
        }

        if(!exists) {
            feeders.add(url);
        }
    }
    // delete a feeder from the list
    public void removeFeeder(String url){
        int index = -1;
        for(int i=0; i<feeders.size(); i++){
            if(feeders.get(i).equals(url)){
                index = i;
                break;
            }
        }

        //remove from the list
        if(index>=0) feeders.remove(index);
    }

    public String[] getFeeders(){
        return feeders.toArray(new String[feeders.size()]);
    }

    public MutableLiveData<List<FeedVO>> getArticleList() {
        if (articleListLive == null) {
            articleListLive = new MutableLiveData<>();
        }
        return articleListLive;
    }

    private void setArticleList(List<FeedVO> articleList) {
        this.articleListLive.postValue(articleList);
    }

    public LiveData<String> getSnackbar() {
        return snackbar;
    }

    public void onSnackbarShowed() {
        snackbar.setValue(null);
    }

    public void fetchFeed() {

        GetRssTask rssTask = new GetRssTask();
        rssTask.setListener(this);
        rssTask.execute(feeders.toArray(new String[feeders.size()])); // read feed on the background


    }

    //callback function from GetRssTask
    @Override
    public void onFeedReceived(List<FeedVO> feedList) {
        setArticleList(feedList);
        System.out.println("call back called");
    }
}
