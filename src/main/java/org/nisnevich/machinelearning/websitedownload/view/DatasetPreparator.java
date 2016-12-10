package org.nisnevich.machinelearning.websitedownload.view;

import edu.uci.ics.crawler4j.url.WebURL;
import javafx.util.Pair;
import org.nisnevich.machinelearning.websitedownload.util.FastFileSystemUtil;

import java.io.*;
import java.util.*;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (12.11.2016)
 */
public class DatasetPreparator {

    // TODO implement interaction with Linked LDA and name files
    private static final String FILE_PAGE_CONTENT = "";
    private static final String FILE_PAGE_LINKS = "";

    private static final String SEPARATOR_URL_CONTENT = " ";
    private static final String SEPARATOR_LINKS = " ";

    public List<String> getUrls() {
        List<String> urlList = new ArrayList<String>();
        // read url list here

        return urlList;
    }

    public void saveData(List<Pair<WebURL, String>> pageContentList, Map<WebURL, List<WebURL>> linksMap)
            throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        for (Pair<WebURL, String> urlContentPair : pageContentList) {
            String pageUrl = urlContentPair.getKey().getURL();
            String pageContent = urlContentPair.getValue();
            stringBuilder.append(pageUrl).append(SEPARATOR_URL_CONTENT).append(pageContent);
        }

        FastFileSystemUtil.writeFile(FILE_PAGE_CONTENT, stringBuilder.toString());

        stringBuilder = new StringBuilder();
        for (Map.Entry<WebURL, List<WebURL>> entry : linksMap.entrySet()) {
            String pageUrl = entry.getKey().getURL();
            stringBuilder.append(pageUrl).append(SEPARATOR_URL_CONTENT);

            List<WebURL> pageLinks = entry.getValue();
            for (WebURL pageLink : pageLinks) {
                stringBuilder.append(pageLink.getURL()).append(SEPARATOR_LINKS);
            }
        }

        FastFileSystemUtil.writeFile(FILE_PAGE_LINKS, stringBuilder.toString());
    }
}
