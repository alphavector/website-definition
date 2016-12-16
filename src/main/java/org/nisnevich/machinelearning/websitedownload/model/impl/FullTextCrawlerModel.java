package org.nisnevich.machinelearning.websitedownload.model.impl;

import edu.uci.ics.crawler4j.crawler.Page;
import javafx.util.Pair;
import org.jsoup.nodes.Document;
import org.nisnevich.machinelearning.websitedownload.model.AbstractCrawlerModel;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (10.12.2016)
 */
public class FullTextCrawlerModel extends AbstractCrawlerModel {

    protected void parsePage(Page page, Document doc) {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(doc.select("meta[name=keywords]").attr("content"));
        contentBuilder.append(" ");
        contentBuilder.append(doc.body().text());

        pageContentList.add(new Pair<>(page.getWebURL(), contentBuilder.toString()));

        // TODO see underlying checklist:
        // - don't include separate numbers (not included in strings; e.g. don't ignore 'mobile18', but ignore '18')
    }

    protected boolean shouldContinueVisiting(Page page, Document doc) {
        if (page.getStatusCode() != 200 || doc.body().text().length() < 10) {
            return false;
        }
        return true;
    }
}