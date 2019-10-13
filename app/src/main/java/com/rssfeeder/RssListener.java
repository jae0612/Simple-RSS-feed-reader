package com.rssfeeder;

import com.rssfeeder.VO.FeedVO;

import java.util.List;

public interface RssListener {
    public void onFeedReceived(List<FeedVO> result);
}

