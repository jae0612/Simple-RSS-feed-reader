/**
 *
 *
 * @author Jae Shin (u6858132)
 * @author Jihwan Bae (u6871659)
 */


package com.rssfeeder;

import android.os.AsyncTask;

import com.rometools.rome.io.ParsingFeedException;
import com.rssfeeder.VO.FeedVO;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class GetRssTaskTest {
    private int res;
    @Test
    public void doInBackground() throws Throwable{
        final CountDownLatch signal = new CountDownLatch(1);

            GetRssTask rssTask = new GetRssTask();
            class Listener implements RssListener{
                @Override
                public void onFeedReceived(List<FeedVO> result) {
                    res = result.size();
                   signal.countDown();

                }
            }
            Listener feedListen = new Listener();
            rssTask.setListener(feedListen);
            rssTask.doInBackground(new String[]{"https://lorem-rss.herokuapp.com/feed?unit=year"});
            signal.await();

            assertEquals(10, res);

    }



}