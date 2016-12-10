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

    // the folder where to store temporary crawler data
    private static final String FOLDER_CRAWLER = "crawler_storage";
    // the number of concurrent threads that should be initiated for crawling
    private static final int CRAWLERS_NUMBER = 2;
    // the maximum number of pages to crawl (-1 for unlimited number of pages)
    private static final int MAX_PAGES_TO_FETCH = 1000;
    // the maximum crawl depth (-1 for unlimited depth)
    private static final int MAX_DEPTH_OF_CRAWLING = 3;
    // delay between requests
    private static final int REQUEST_DELAY = 300;
    // If binary data should also be crawled (example: the contents of pdf, or the metadata of images etc)
    private static final boolean BINARY_CONTENT_CRAWLING_ENABLED = false;

    private DatasetPreparator datasetPreparator;

    public void start() throws Exception {

        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(FOLDER_CRAWLER);

        config.setPolitenessDelay(REQUEST_DELAY);

        config.setMaxDepthOfCrawling(MAX_DEPTH_OF_CRAWLING);

        config.setMaxPagesToFetch(MAX_PAGES_TO_FETCH);

        config.setIncludeBinaryContentInCrawling(BINARY_CONTENT_CRAWLING_ENABLED);

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

    public static void main(String[] args) {
        try {
            new CrawlerController().start();
        } catch (Exception e) {
            // TODO think about start point architecture
            e.printStackTrace();
        }
    }
}
