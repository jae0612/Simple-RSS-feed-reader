package com.rssfeeder.VO;

import com.rometools.rome.feed.synd.SyndCategory;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
//Jae
@Data
public class FeedVO {

    String title;
    String link;
    String author;
    String description;
    LocalDateTime pubDate;
    List<String> categories;
    String imageUrl;
    String imageTitle;
    String imageLink;
    String imageDescription;



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getPubDate() {
        return pubDate;
    }

    public void setPubDate(LocalDateTime pubDate) {
        this.pubDate = pubDate;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) { this.categories = categories; }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(String imageDescription) {
        this.imageDescription = imageDescription;
    }


}
