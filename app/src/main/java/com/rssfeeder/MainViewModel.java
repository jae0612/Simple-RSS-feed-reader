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

public class MainViewModel extends ViewModel {

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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        List<FeedVO> feedList = new ArrayList<>();
        try {
            // test url
            URL feedUrl = new URL(urlString);
            StringBuilder sb = new StringBuilder();
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));

            // read entries and add to the list<FeedVO>
            for(SyndEntry entry: feed.getEntries()){
                FeedVO feedObj = new FeedVO();
                feedObj.setTitle(entry.getTitle());

                feedList.add(feedObj);
            }

            setArticleList(feedList);

        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: "+ex.getMessage());
        }
    }
}
