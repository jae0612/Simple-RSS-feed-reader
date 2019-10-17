package com.rssfeeder;

import android.os.AsyncTask;
import android.os.StrictMode;

import com.rometools.rome.feed.rss.Content;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import com.rometools.modules.mediarss.MediaEntryModule;
import com.rometools.modules.mediarss.MediaModule;
import com.rometools.modules.mediarss.types.MediaContent;
import com.rometools.modules.mediarss.types.Rating;
import com.rometools.modules.mediarss.types.Thumbnail;
import com.rssfeeder.VO.FeedVO;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//Jae
public class GetRssTask extends AsyncTask<String,Void,Void> {

    RssListener listener;
    List<FeedVO> feedList = new ArrayList<>();

    @Override
    protected Void doInBackground(String[] urls) {

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
                    feedObj.setPubDate(entry.getPublishedDate().toString());

                    //parse image url
                    MediaEntryModule module = (MediaEntryModule) entry.getModule(MediaModule.URI);
                    Thumbnail[] thumbnails = null;
                    MediaContent[] contents = null;
                    if(module != null) {
                        thumbnails = module.getMetadata().getThumbnail();
                        contents = module.getMediaContents();
                    }
                    if(thumbnails != null && thumbnails.length > 0)
                        feedObj.setImageUrl(thumbnails[0].getUrl().toString());
                    else if(contents != null && contents.length > 0)
                        feedObj.setImageUrl(contents[0].getReference().toString());

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

    // After finishing read, push the feed list to the MainViewModel
    @Override
    protected void onPostExecute(Void result){
        listener.onFeedReceived(feedList);
    }

}
