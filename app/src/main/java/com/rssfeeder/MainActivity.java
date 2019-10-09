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


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

    }

    public void testFeed(View view){
        TextView tv = (TextView)findViewById(R.id.window);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        List<FeedVO> feedList = new ArrayList<>();
        try {
            URL feedUrl = new URL("https://www.cnet.com/rss/news/");
            StringBuilder sb = new StringBuilder();
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));


            for(SyndEntry entry: feed.getEntries()){
                FeedVO feedObj = new FeedVO();
                feedObj.setTitle(entry.getTitle());
                feedList.add(feedObj);
            }
            for(FeedVO vo : feedList)
                sb.append(vo.getTitle() + "\n");

            tv.setText(sb);



        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: "+ex.getMessage());
        }

    }
}
