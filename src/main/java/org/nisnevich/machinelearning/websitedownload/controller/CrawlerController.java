package org.nisnevich.machinelearning.websitedownload.controller;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.nisnevich.machinelearning.websitedownload.model.FullTextCrawlerModel;


/**
 * @author Nisnevich Arseniy
 * @version 1.0 (12.11.2016)
 */
public class CrawlerController {
    public static final String DEFAULT_SEED = "http://www.gimn3.ru/";
    private static final String STORAGE = "storage";
    private static final int CRAWLERS_NUMBER = 2;
    private static final int MAX_PAGES_TO_FETCH = 1000;
    private static final int MAX_DEPTH_OF_CRAWLING = 3;
    private static final int REQUEST_DELAY = 300;

    public static void main(String[] args) throws Exception {

    /*
     * numberOfCrawlers shows the number of concurrent threads that should
     * be initiated for crawling.
     */

        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(STORAGE);

    /*
     * Be polite: Make sure that we don't send more than 1 request per
     * second (1000 milliseconds between requests).
     */
        config.setPolitenessDelay(REQUEST_DELAY);

    /*
     * You can set the maximum crawl depth here. The default value is -1 for
     * unlimited depth
     */
        config.setMaxDepthOfCrawling(MAX_DEPTH_OF_CRAWLING);

    /*
     * You can set the maximum number of pages to crawl. The default value
     * is -1 for unlimited number of pages
     */
        config.setMaxPagesToFetch(MAX_PAGES_TO_FETCH);

        /**
         * Do you want crawler4j to crawl also binary data ?
         * example: the contents of pdf, or the metadata of images etc
         */
        config.setIncludeBinaryContentInCrawling(false);

    /*
     * Do you need to set a proxy? If so, you can use:
     * config.setProxyHost("proxyserver.example.com");
     * config.setProxyPort(8080);
     *
     * If your proxy also needs authentication:
     * config.setProxyUsername(username); config.getProxyPassword(password);
     */

    /*
     * This config parameter can be used to set your crawl to be resumable
     * (meaning that you can resume the crawl from a previously
     * interrupted/crashed crawl). Note: if you enable resuming feature and
     * want to start a fresh crawl, you need to delete the contents of
     * rootFolder manually.
     */
        config.setResumableCrawling(false);

    /*
     * Instantiate the controller for this crawl.
     */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

    /*
     * For each crawl, you need to add some seed urls. These are the first
     * URLs that are fetched and then the crawler starts following links
     * which are found in these pages
     */
        controller.addSeed(DEFAULT_SEED);

    /*
     * Start the crawl. This is a blocking operation, meaning that your code
     * will reach the line after this only when crawling is finished.
     */
        controller.start(FullTextCrawlerModel.class, CRAWLERS_NUMBER);
    }
}
