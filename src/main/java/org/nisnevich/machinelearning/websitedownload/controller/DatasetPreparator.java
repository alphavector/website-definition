package org.nisnevich.machinelearning.websitedownload.controller;

import edu.uci.ics.crawler4j.url.WebURL;
import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.nisnevich.machinelearning.websitedownload.controller.CrawlerController.FILE_INPUT;
import static org.nisnevich.machinelearning.websitedownload.controller.CrawlerController.FILE_PAGE_CONTENT;
import static org.nisnevich.machinelearning.websitedownload.controller.CrawlerController.FILE_PAGE_LINKS;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (12.11.2016)
 */
public class DatasetPreparator {
    private static final Logger logger = LoggerFactory.getLogger(DatasetPreparator.class);

    private static final String SEPARATOR_URL_CONTENT = " ";
    private static final String SEPARATOR_LINKS = " ";

    public List<String> getUrls() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_INPUT)));
        List<String> urlList = new ArrayList<>();
        int lineCounter = 0;
        while (bufferedReader.ready()) {
            lineCounter++;
            String url = bufferedReader.readLine();
            if (url.isEmpty()) {
                logger.error("Warnining: there is empty url in dataset. " +
                        "Check its correctness! Line number: " + lineCounter);
                continue;
            }
            urlList.add(url);
        }

        return urlList;
    }

    /**
     * Writes data (content of pages and links between pages) to specified file.
     * The finish point of this program.
     * @param pageContentList list of page contents
     * @param linksMap map of links between pages
     * @throws IOException
     */
    public void saveData(List<Pair<WebURL, String>> pageContentList, Map<WebURL, List<WebURL>> linksMap)
            throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        for (Pair<WebURL, String> urlContentPair : pageContentList) {
            String pageUrl = urlContentPair.getKey().getURL();
            String pageContent = urlContentPair.getValue();
            stringBuilder.append(pageUrl).append(SEPARATOR_URL_CONTENT).append(pageContent).append("\n");
        }

        FileUtils.writeByteArrayToFile(new File(FILE_PAGE_CONTENT), stringBuilder.toString().getBytes());

        stringBuilder = new StringBuilder();
        for (Map.Entry<WebURL, List<WebURL>> entry : linksMap.entrySet()) {
            String pageUrl = entry.getKey().getURL();
            stringBuilder.append(pageUrl).append(SEPARATOR_URL_CONTENT);

            List<WebURL> pageLinks = entry.getValue();
            for (WebURL pageLink : pageLinks) {
                stringBuilder.append(pageLink.getURL()).append(SEPARATOR_LINKS);
            }
            stringBuilder.append("\n");
        }

        FileUtils.writeByteArrayToFile(new File(FILE_PAGE_LINKS), stringBuilder.toString().getBytes());
    }
}
