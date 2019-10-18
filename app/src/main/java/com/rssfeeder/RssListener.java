/**
 *
 *
 * @author Jae Shin (u6858132)
 * @author Jihwan Bae (u6871659)
 */

package com.rssfeeder;

import com.rssfeeder.VO.FeedVO;

import java.util.List;

//Jae
public interface RssListener {
    void onFeedReceived(List<FeedVO> result);
}

