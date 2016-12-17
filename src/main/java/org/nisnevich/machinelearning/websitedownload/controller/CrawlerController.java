package org.nisnevich.machinelearning.websitedownload.controller;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.nisnevich.machinelearning.websitedownload.model.AbstractCrawlerModel;
import org.nisnevich.machinelearning.websitedownload.model.impl.FullText_RuEn_NoTrash_CrawlerModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (12.11.2016)
 */
public class CrawlerController {
    private static final Logger logger = LoggerFactory.getLogger(CrawlerController.class);

    // --- CRAWLER SETTINGS
    // if crawler should follow redirects on pages
    private static final boolean IS_FOLLOW_REDIRECTS_ENABLED = true;
    // the maximum crawl depth (-1 for unlimited depth)
    private static final int MAX_DEPTH_OF_CRAWLING = 1;
    // delay between different requests
    private static final int REQUEST_DELAY = 500;
    // the number of concurrent threads that should be initiated for crawling
    private static final int CRAWLERS_NUMBER = 1;
    // the maximum number of pages to crawl (-1 for unlimited number of pages)
    private static final int MAX_PAGES_TO_FETCH = -1;
    // if binary data should also be crawled (example: the contents of pdf, or the metadata of images etc)
    private static final boolean BINARY_CONTENT_CRAWLING_ENABLED = false;
    // if crawling could be resumed after interruption (using cache)
    private static final boolean IS_RESUMABLE_CRAWLING_ENABLED = false;
    // if https pages should be visited
    private static final boolean IS_INCLUDE_HTTPS_PAGES_ENABLED = true;
    // timeout for connection
    private static final int CONNECTION_TIMEOUT = 10000;
    // timeout for socket
    private static final int SOCKET_TIMEOUT = 10000;
    // user agent string to send with requests
    private static final String USER_AGENT_STRING = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";
    // header to manage languages priority
    private static final String HEADER_ACCEPT_LANGUAGE = "ru, en-gb;q=0.5";

    // --- PROGRAM SETTINGS
    // the crawler model which specifies how to parse websites
    private static final Class<FullText_RuEn_NoTrash_CrawlerModel> CRAWLER_MODEL_CLASS = FullText_RuEn_NoTrash_CrawlerModel.class;
    // the maximal count of VALID links to visit per page
    public static final int MAX_LINKS_TO_VISIT_PER_PAGE = 50;
    // id of currently running process (used as names for directories when saving sites cache or )
    public static final long UNIQUE_CRAWLER_ID = System.currentTimeMillis();

    // --- I/O SETTINGS
    // folder name where to store temporary crawler data
    private static final String FOLDER_CACHE_CRAWLER = "storage/cache_crawler";
    // folder name where to store sites cache
    public static final String FOLDER_CACHE_SITES = "storage/cache_sites";
    // input file path (with url dataset)
    public static final String FILE_INPUT = "storage/input/DOMAINS_FULL_NOSUBDOMAINS_EDITED.txt";
    // file path where to store resulting content of pages
    public static final String FILE_PAGE_CONTENT = "storage/output/" + UNIQUE_CRAWLER_ID + "/content.data";
    // file path where to store resulting links between pages
    public static final String FILE_PAGE_LINKS = "storage/output/" + UNIQUE_CRAWLER_ID + "/links.data";

    private static CrawlController controller;
    private static List<String> urls = new ArrayList<>();
    private DatasetPreparator datasetPreparator = new DatasetPreparator();

    /*
      Contains domains which subdomains were impossible to parse.
      The domain is added as seed to web-crawler when its subdomain is unavailable first time.
     */
//    private static Map<String, List<String>> shortenedDomains = new HashMap<>();

    public void start() throws Exception {

        urls = datasetPreparator.getUrls();

        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(FOLDER_CACHE_CRAWLER);
        config.setPolitenessDelay(REQUEST_DELAY);
        config.setMaxDepthOfCrawling(MAX_DEPTH_OF_CRAWLING);
        config.setMaxPagesToFetch(MAX_PAGES_TO_FETCH);
        config.setIncludeBinaryContentInCrawling(BINARY_CONTENT_CRAWLING_ENABLED);
        config.setConnectionTimeout(CONNECTION_TIMEOUT);
        config.setSocketTimeout(SOCKET_TIMEOUT);
        config.setFollowRedirects(IS_FOLLOW_REDIRECTS_ENABLED);
        config.setResumableCrawling(IS_RESUMABLE_CRAWLING_ENABLED);
        config.setIncludeHttpsPages(IS_INCLUDE_HTTPS_PAGES_ENABLED);
        config.setUserAgentString(USER_AGENT_STRING);

        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Accept", "text/html,application/xhtml+xml," +
                "application/xml;q=0.9,image/webp,*/*;q=0.8"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip, deflate, sdch, br"));
        headers.add(new BasicHeader("Accept-Language", HEADER_ACCEPT_LANGUAGE));
        headers.add(new BasicHeader("Cookie", "MC1=GUID=66140ef2b7d04a458f0c73fde656b2af&HASH=f20e&LV=201602&V=4&LU=1455999491591; A=I&I=AxUFAAAAAACZBgAAwrWDkeNNLzx8tDac2mvJkQ!!&V=4; MSFPC=ID=fc3c21ca430d4af89846cae3f471cfcc&CS=2&LV=201602&V=1; msresearch=%7B%22version%22%3A%225.0%22%2C%22state%22%3A%7B%22name%22%3A%22IDLE%22%2C%22url%22%3Aundefined%2C%22timestamp%22%3A1455999423942%7D%2C%22lastinvited%22%3A1455999423943%2C%22userid%22%3A%2214559994239424100508063565939%22%2C%22vendorid%22%3A1%2C%22surveys%22%3A%5B%22p329970507%22%5D%2C%22graceperiod%22%3A5%2C%22trackertimestamp%22%3A0%7D; RioTracking.SessionASID=AxUFAAAAAACZBgAAwrWDkeNNLzx8tDac2mvJkQ!!; _wt.mode-311121=WT3-hMX1BJffuI~; PRUM_EPISODES=s=1457563133657&r=https%3A//www.microsoft.com/ru-ru/evalcenter/evaluate-office-professional-plus-2013; WT_NVR_RU=0=msdn|technet:1=:2=; TocPosition=1; WT_FPC=id=20516d2f391d9ee63bf1456058149054:lv=1465887338539:ss=1465887338539; omniID=1464424825461_3d2e_80ac_8545_1fbbc17f6951; s_cc=true; s_sq=%5B%5BB%5D%5D; optimizelyEndUserId=oeu1467583392105r0.21802814225441436; MUID=212A72E4A0A3683901247A44A4A36B5B; smc_m=1; smc_dc=0; ANON=A=7F7F18466C1B97EC055C5673FFFFFFFF&E=12da&W=1; NAP=V=1.9&E=1280&C=EcVjMaoGs4PfEvScqNt1n395aad4rhloR6CkmRhbwIzfMXhvHDpZ4Q&W=1; AMCVS_EA76ADE95776D2EC7F000101%40AdobeOrg=1; RioTracking.EndActionCode=300217353; OnlineTrackingV2.0=CATC=300217353&CTC=300217353; utag_main=v_id:0157dd9a023d0001a92e638be8210506e00250660086e$_sn:1$_ss:0$_st:1476893473959$ses_id:1476891640386%3Bexp-session$_pn:2%3Bexp-session$dc_visit:1$dc_event:4%3Bexp-session$dc_region:eu-central-1%3Bexp-session; AMCV_EA76ADE95776D2EC7F000101%40AdobeOrg=-179204249%7CMCMID%7C32048135946368244683933808200852552920%7CMCAAMLH-1477496483%7C6%7CMCAAMB-1477496483%7CNRX38WO0n5BH8Th-nqAG_A%7CMCOPTOUT-1476898883s%7CNONE%7CMCAID%7CNONE%7CMCCIDH%7C756031497; R=200840196-10/19/2016 18:41:23|300217353-10/19/2016 18:40:40; RioTracking.CellCode=200840196; RioTracking.Organic=1; trwv.uid=windows-server-1476897299360-97b775c1%3A1; _mkto_trk=id:157-GQE-382&token:_mch-microsoft.com-1476891679901-25097; display-culture=ru-RU; market=US; smcflighting=100; ak_bmsc=A5D50B6939B134F59A265969AEEAE0CC5C7AD72E1C2B000003B653580A94B375~plD2/KTvn7w4umPyCwMCIvQHDRy94SWt/dcKRQvqskC/1pr9nVa1Wd973mgSryJmX3iRPE42PHXCwteqAHGdIcHAiKXA4cldVHR3ggAdT0VyPJlRBdZv8EkaYOQjfXcdmws/nS9Y4MKtuNzNrHsTs3zTAtqiOBrqmCErbS9IFpGptpua2MJ/EXz9VRnB5tmZMQx1TtuO+aa45IExsgKiY9qQ==; MS-CV=GLhHcxlga63w%2ByYb.1; akacd_OneRF=1489657991~rv=80~id=7009382f72ccc452769b830c4ad4cb1b; optimizelySegments=%7B%223045291179%22%3A%22search%22%2C%223058720953%22%3A%22false%22%2C%223070871035%22%3A%22gc%22%2C%226245642064%22%3A%22referral%22%2C%226254200234%22%3A%22false%22%2C%226261880250%22%3A%22none%22%2C%226263930174%22%3A%22gc%22%2C%226190090623%22%3A%22gc%22%2C%226213440327%22%3A%22none%22%2C%226201801690%22%3A%22search%22%2C%226204850164%22%3A%22false%22%2C%224500910081%22%3A%22none%22%2C%224500450172%22%3A%22referral%22%2C%224495240141%22%3A%22gc%22%2C%224504320076%22%3A%22false%22%2C%227961301167%22%3A%22false%22%2C%227927848602%22%3A%22none%22%2C%227962561100%22%3A%22gc%22%2C%227951071293%22%3A%22referral%22%2C%226202010951%22%3A%22direct%22%2C%226206680296%22%3A%22gc%22%2C%226183560892%22%3A%22none%22%2C%226208020262%22%3A%22false%22%7D; optimizelyBuckets=%7B%7D; graceIncr=0; __CT_Data=gpv=3&apv_32434_www07=1&apv_32388_www07=1&apv_32260_www07=1; WRUID=0; OpenIdConnect.nonce.OpenIdConnect=V0RkNUhIUENQT2V6ZllkXzdSWlBXbXJZcWxSRXNuMThzM2gwalp2TVo0UXU2eE5yRUhMbnlsb2ZlZVJySE9hcjk1d2dycDAtU2xqZ0Y4ZWItZ3FLTzZOTGs3OVppaER1NmxVcmlNMm1jQ0ZzZDBic3U2OTlJREtLeVUwNGp3QkxmM1hZSjNzZXFQcHQ1dFlIVS1peDdGdnNIbDcySURhRk80dVgxazJTaWlRNW96bk5OQ1ZlcnB1UGx6QVliRnNUV2wtbkhIUmMyay13RkRyVTBCSUpQUnpycmQ4; MS-CV=GLhHcxlga63w%2ByYb.7; MS0=2d6ffb7d8f2a4ae5946784c7be99f9f2"));
        headers.add(new BasicHeader("Upgrade-insecure-requests", "1"));
        config.setDefaultHeaders(headers);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        controller = new CrawlController(config, pageFetcher, robotstxtServer);

        for (String url : urls) {
            controller.addSeed(url);
        }

        logger.info("Crawler started");
        // crawler could be started blocking or non-blocking (to add multiple seeds we use non-blocking method)
        controller.start(CRAWLER_MODEL_CLASS, CRAWLERS_NUMBER);

        datasetPreparator.saveData(
                AbstractCrawlerModel.getPageContentMap(),
                AbstractCrawlerModel.getLinksMap());
    }

    public static void main(String[] args) {
        try {
            new CrawlerController().start();
        } catch (Exception e) {
            logger.error("Caught exception in \"main\" method", e);
        }
    }

//    public static void addBadUrl(WebURL webURL) {
//        String subDomain = webURL.getSubDomain();
//        if (subDomain == null || subDomain.length() == 0 || subDomain.equals("www")) {
//            return;
//        }
//        if (shortenedDomains.containsKey(webURL.getDomain())) {
//            List<String> domainList = shortenedDomains.get(webURL.getDomain());
//            domainList.add(webURL.getSubDomain());
//            return;
//        }
//
//        String urlWithoutSubdomain = webURL.getURL().replaceFirst(subDomain, "www");
//        controller.addSeed(urlWithoutSubdomain);
//
//        List<String> domainList = new ArrayList<>();
//        domainList.add(webURL.getSubDomain());
//        shortenedDomains.put(webURL.getDomain(), domainList);
//    }
}
