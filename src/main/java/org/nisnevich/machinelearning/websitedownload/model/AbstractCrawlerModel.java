package org.nisnevich.machinelearning.websitedownload.model;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.nisnevich.machinelearning.websitedownload.controller.CrawlerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static org.nisnevich.machinelearning.websitedownload.controller.CrawlerController.FOLDER_CACHE_SITES;
import static org.nisnevich.machinelearning.websitedownload.controller.CrawlerController.UNIQUE_CRAWLER_ID;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (10.12.2016)
 */
public abstract class AbstractCrawlerModel extends WebCrawler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractCrawlerModel.class);
    private static final Logger filterLogger = LoggerFactory.getLogger("file_filter");

    // List of internet file extensions: https://www.file-extensions.org/filetype/extension/name/internet-related-files
    private static final Pattern VALID_PAGE_EXTENSIONS = Pattern.compile(
            ".*" +
                    "(\\.(htm|html|jsp|php|asp|xml|xhtml|shtml|cgi))" +
                    "|([^.]+)$");
    private static Map<WebURL, List<WebURL>> linksMap = new HashMap<>();
    protected static List<Pair<WebURL, String>> pageContentList = new ArrayList<>();

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL webURL) {

        String href = webURL.getURL().toLowerCase();
        // Ignore the url if it has NOT an extension that matches our defined set
        // Ignore the url if it IS NOT in the basic domain
        if (!VALID_PAGE_EXTENSIONS.matcher(webURL.getPath()).matches()
                || !webURL.getDomain().equals(referringPage.getWebURL().getDomain())) {
            filterLogger.info(String.format("[%s] - REFUSED - %s",
                    referringPage.getWebURL().getURL(), webURL.getURL()));
            return false;
        }
        filterLogger.info(String.format("[%s] - ALLOWED - %s",
                referringPage.getWebURL().getURL(), webURL.getURL()));
        List<WebURL> visitedURLs = linksMap.get(referringPage.getWebURL());
        if (visitedURLs == null) {
            List<WebURL> urlList = new ArrayList<>();
            urlList.add(webURL);
            linksMap.put(referringPage.getWebURL(), urlList);
        } else {
            if (visitedURLs.size() < CrawlerController.MAX_LINKS_TO_VISIT_PER_PAGE) {
                List<WebURL> urlList = linksMap.get(referringPage.getWebURL());
                if (!urlList.contains(referringPage.getWebURL())) {
                    urlList.add(webURL);
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {
        logger.info("Visiting " + page.getWebURL().getURL());
        if (!onPageVisited(page)) {
            return;
        }

        String folderName = page.getWebURL().getDomain();
        String fileName = String.valueOf(page.getWebURL().getURL().hashCode());
        File pageFile = new File(String.format("%s/%s/%s/%s",
                FOLDER_CACHE_SITES, UNIQUE_CRAWLER_ID, folderName, fileName));
        try {
            FileUtils.writeByteArrayToFile(pageFile, page.getContentData());
            String pageCharset = page.getContentCharset();
            Document doc = Jsoup.parse(pageFile, pageCharset);

            if (!onPageParsed(page.getWebURL(), doc)) {
                return;
            }
            parsePage(page, doc);
        } catch (IOException e) {
            logger.error("Error occurred while parsing page " + page.getWebURL().getURL(), e);
        }
    }

    public static List<Pair<WebURL, String>> getPageContentList() {
        return Collections.unmodifiableList(pageContentList);
    }

    public static Map<WebURL, List<WebURL>> getLinksMap() {
        return Collections.unmodifiableMap(linksMap);
    }

    protected abstract boolean onPageVisited(Page page);

    protected abstract boolean onPageParsed(WebURL url, Document doc);

    protected abstract void parsePage(Page page, Document doc);
}