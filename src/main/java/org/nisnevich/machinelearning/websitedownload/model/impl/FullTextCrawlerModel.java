package org.nisnevich.machinelearning.websitedownload.model.impl;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;
import javafx.util.Pair;
import org.jsoup.nodes.Document;
import org.nisnevich.machinelearning.websitedownload.model.AbstractCrawlerModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (10.12.2016)
 */
public class FullTextCrawlerModel extends AbstractCrawlerModel {
    private static final Logger filterLogger = LoggerFactory.getLogger("file_filter");

    private static final String ALLOWED_CONTENT_TYPE = "text/html";
    private static final int ALLOWED_STATUS_CODE = 200;
    private static final int ALLOWED_MIN_BODY_CONTENT_LENGTH = 10;

    protected void parsePage(Page page, Document doc) {
        String contentBuilder = doc.select("meta[name=keywords]").attr("content") +
                " " + doc.body().text();
        
        pageContentList.add(new Pair<>(page.getWebURL(), contentBuilder));
    }

    protected boolean onPageVisited(Page page) {
        if (page.getStatusCode() != ALLOWED_STATUS_CODE) {
            filterLogger.info(String.format("Page blocked, reason: returned denied status code." +
                            " Status code: %s. Url: %s",
                    page.getStatusCode(), page.getWebURL().getURL()));
            return false;
        }
        if (!page.getContentType().contains(ALLOWED_CONTENT_TYPE)) {
            filterLogger.info(String.format("Page blocked, reason: denied content type. Content-type: %s. Url: %s",
                    page.getContentType(), page.getWebURL().getURL()));
            return false;
        }
        return true;
    }

    protected boolean onPageParsed(WebURL url, Document doc) {
        if (doc.body().text().length() < ALLOWED_MIN_BODY_CONTENT_LENGTH) {
            filterLogger.info(String.format("Page blocked, reason: body content length is smaller than %s. " +
                            "Content length: %s. Url: %s",
                    ALLOWED_MIN_BODY_CONTENT_LENGTH, doc.body().text().length(), url.getURL()));
            return false;
        }
        return true;
    }
}