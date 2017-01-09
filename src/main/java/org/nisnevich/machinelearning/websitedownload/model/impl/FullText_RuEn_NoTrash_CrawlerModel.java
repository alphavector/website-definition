package org.nisnevich.machinelearning.websitedownload.model.impl;

import edu.uci.ics.crawler4j.crawler.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (10.12.2016)
 */
public class FullText_RuEn_NoTrash_CrawlerModel extends FullTextCrawlerModel {

    protected void parsePage(Page page, Document doc) {
        // Fetching content - meta keywords and body html
        String contentBuilder = doc.select("meta[name=keywords]").attr("content") +
                " " + doc.body().html();
        // Removing ads
        doc.select("a").remove();
        // Filtering content
        String content =
                // Default cleaning (for reliability)
                Jsoup.clean(contentBuilder, Whitelist.simpleText())
                // Replacing all tags by spaces
                .replaceAll("<[^>]*>", " ")
                // Replacing line separators by spaces
                .replaceAll("\n", " ")
                // Replacing special characters by spaces
                .replaceAll("&.+?;", " ")
                // Replacing all remaining trash
                .replaceAll("[^a-zA-Zа-яА-Я ]", " ")
                // Merging multiple spaces, removing unnecessary spaces
                .replaceAll("\\s+", " ").trim();
        pageContentMap.put(page.getWebURL().getURL(), content);
    }
}