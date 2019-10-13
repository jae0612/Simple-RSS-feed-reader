package com.rssfeeder;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.rssfeeder.VO.FeedVO;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements RssListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

    }

    public void testFeed(View view){
        GetRssTask rssTask = new GetRssTask();
        rssTask.setListener(this);
        rssTask.execute("https://www.cnet.com/rss/news/");



    }

    @Override
    public void onFeedReceived(List<FeedVO> feedList) {
        StringBuilder sb = new StringBuilder();
        TextView tv = (TextView)findViewById(R.id.window);
        for (FeedVO vo : feedList)
            sb.append(vo.getTitle() + "\n");
        tv.setText(sb);
    }
}
