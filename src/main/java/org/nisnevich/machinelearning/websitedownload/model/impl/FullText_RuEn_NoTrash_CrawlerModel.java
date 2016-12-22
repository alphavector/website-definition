package org.nisnevich.machinelearning.websitedownload.model.impl;

import edu.uci.ics.crawler4j.crawler.Page;
import org.jsoup.nodes.Document;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (10.12.2016)
 */
public class FullText_RuEn_NoTrash_CrawlerModel extends FullTextCrawlerModel {

    private static final String ALLOWED_CONTENT_REGEX = "[^a-zA-Zа-яА-Я ]";

    protected void parsePage(Page page, Document doc) {
        String contentBuilder = doc.select("meta[name=keywords]").attr("content") +
                " " +
                doc.body().text();

        String content = contentBuilder.replaceAll("\n", " ").replaceAll(ALLOWED_CONTENT_REGEX, "");

        pageContentMap.put(page.getWebURL().getURL(), content);
    }
}