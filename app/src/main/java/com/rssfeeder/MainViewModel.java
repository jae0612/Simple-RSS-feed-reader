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
    private String urlString = "https://www.cnet.com/rss/news/";

    private MutableLiveData<String> snackbar = new MutableLiveData<>();

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
        rssTask.execute(urlString);


    }

    //callback function from GetRssTask
    @Override
    public void onFeedReceived(List<FeedVO> feedList) {
        setArticleList(feedList);
        System.out.println("call back called");
    }
}
