package com.rssfeeder;

import android.os.AsyncTask;
import android.os.StrictMode;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.rssfeeder.VO.FeedVO;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetRssTask extends AsyncTask<String,Void,Void> {

    RssListener listener;
    List<FeedVO> feedList = new ArrayList<>();
    protected Void doInBackground(String[] urls) {

//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        for(String url: urls) {
            try {
                URL feedUrl = new URL(url);
                StringBuilder sb = new StringBuilder();
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(feedUrl));

                for (SyndEntry entry : feed.getEntries()) {
                    FeedVO feedObj = new FeedVO();
                    feedObj.setTitle(entry.getTitle());
                    feedObj.setAuthor(entry.getAuthor());
                    feedObj.setDescription(entry.getDescription().getValue());
                    feedObj.setImageTitle(entry.getSource().getImage().getTitle());
                    feedList.add(feedObj);
                }
                for (FeedVO vo : feedList)
                    sb.append(vo.getTitle() + "\n");
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("ERROR: " + ex.getMessage());
            }
        }
        return null;
    }

    public void setListener(RssListener listener){
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(Void result){
        listener.onFeedReceived(feedList);
    }



    }
