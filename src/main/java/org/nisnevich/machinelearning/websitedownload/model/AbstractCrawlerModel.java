package org.nisnevich.machinelearning.websitedownload.model;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.regex.Pattern;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (10.12.2016)
 */
public abstract class AbstractCrawlerModel extends WebCrawler {

    private static final Pattern PAGE_EXTENSIONS = Pattern.compile(".*\\.(htm|html|jsp|php)$");

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        // Ignore the url if it has an extension that matches our defined set
        if (PAGE_EXTENSIONS.matcher(href).matches()) {
            return false;
        }
        // Only accept the url if it is in the basic domain
        return href.startsWith(referringPage.getWebURL().getURL());

        // For debugging
//        if (href.startsWith(referringPage.getWebURL().getURL())) {
//            System.out.println("Good url: " + href);
//            return true;
//        } else {
//            System.out.println("Bad url: " + href);
//            return false;
//        }
    }
}