/**
 *
 *
 * @author Jae Shin (u6858132)
 * @author Jihwan Bae (u6871659)
 */


package com.rssfeeder;

import android.provider.BaseColumns;
//Jae
public class RssFeedContract {

    // make the constructor private.
    private RssFeedContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "archive";
        public static final String TABLE_NAME_FAVORITE = "archive_favorite";
        public static final String TABLE_NAME_FEEDER = "archive_feeder";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_LINK = "link";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_PUBLISH_DATE = "publishDate";
        public static final String COLUMN_NAME_IMAGE_URL = "imageUrl";
        public static final String COLUMN_NAME_IMAGE_TITLE = "imageTitle";
        public static final String COLUMN_NAME_IMAGE_LINK = "imageLink";
        public static final String COLUMN_NAME_IMAGE_DESCRIPTION = "imageDescription";
    }
}
