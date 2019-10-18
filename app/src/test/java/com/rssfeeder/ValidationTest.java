/**
 *
 *
 * @author Jae Shin (u6858132)
 * @author Jihwan Bae (u6871659)
 */

package com.rssfeeder;

import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertEquals;

public class ValidationTest {

    @Before
    public void initialize(){
    }


    @Test
    public void RSS_Valid_URL_Test() {
        // 1. URL must start with either http:// or https://
        assertEquals(false, Validation.isValueFeeder("This.is.an.invalid.URL"));

        // valid RSS feeders
        assertEquals(true, Validation.isValueFeeder("http://feeds.bbci.co.uk/news/rss.xml"));
        assertEquals(true, Validation.isValueFeeder("https://www.espn.com/espn/rss/news"));

    }
}
