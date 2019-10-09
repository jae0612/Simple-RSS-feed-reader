package com.rssfeeder.VO;

import java.util.List;

import lombok.Data;

@Data
public class FeedVO {
    String title;
    String link;
    String uri;
    String author;
    String description;
    String pubDate;
    String imageUrl;
    String imageTitle;
    String imageLink;
    String imageDescription;
}
