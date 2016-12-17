package org.nisnevich.machinelearning.websitedownload.util.postprocessing;

import org.nisnevich.machinelearning.websitedownload.controller.CrawlerController;
import org.nisnevich.machinelearning.websitedownload.controller.DatasetPreparator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (17.12.2016)
 */
public class WebCombinerUtil {

    private static final String INPUT_CRAWLER_ID = "";
    private static final String FILE_PAGE_CONTENT = "storage/output/" + INPUT_CRAWLER_ID + "/content.data";
    private static final String FILE_PAGE_LINKS = "storage/output/" + INPUT_CRAWLER_ID + "/links.data";

    static Map<String, Integer> urlMappings = new HashMap<>();
    static Map<String, String> contents = new HashMap<>();
    static Map<String, List<String>> links = new HashMap<>();
    static List<String> defaultUrls = new ArrayList<>();

    private DatasetPreparator datasetPreparator;

    public static void main(String[] args) throws IOException {
        // TODO test this method
        readDataset();
        // TODO test this method
        cleanTrash();
        // Should be called BEFORE cut & merge
        checkDefaultUrlsIntegrity();

        createCuttenLinksDataset();

        createMergedLinksDataset();
        // Should be called AFTER cut & merge
        checkLinksIntegrity();
    }

    /**
     * Reads datasets of links and contents to static variables with same naming
     * @throws IOException
     */
    private static void readDataset() throws IOException {
        BufferedReader contentReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_PAGE_CONTENT)));
        BufferedReader linksReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_PAGE_LINKS)));
        BufferedReader defaultUrlsReader = new BufferedReader(new InputStreamReader(new FileInputStream(CrawlerController.FILE_INPUT)));

        int lineCounter = 0;
        while (contentReader.ready()) {
            String line = contentReader.readLine();
            if (line.isEmpty()) {
                System.err.println("Warning: there is empty line in CONTENTS dataset. " +
                        "Check its correctness! Line number: " + (1 + lineCounter));
                continue;
            }
            // TODO check correctness of url extraction
            String url = line.substring(0, line.indexOf(' ') - 1);
            String content = line.substring(line.indexOf(' '));
            if (url.isEmpty()) {
                System.err.println("Warning: there is empty url in CONTENTS dataset. " +
                        "Check its correctness! Line number: " + (1 + lineCounter));
                continue;
            }
            contents.put(url, content);
            urlMappings.put(url, lineCounter);
            lineCounter++;
        }

        lineCounter = 0;
        while (linksReader.ready()) {
            String line = linksReader.readLine();
            if (line.isEmpty()) {
                System.err.println("Warning: there is empty line in LINKS dataset. " +
                        "Check its correctness! Line number: " + (1 + lineCounter));
                continue;
            }
            // TODO check correctness of url extraction
            String url = line.substring(0, line.indexOf(' ') - 1);
            if (url.isEmpty()) {
                System.err.println("Warning: there is empty url in LINKS dataset. " +
                        "Check its correctness! Line number: " + (1 + lineCounter));
                continue;
            }
            String[] linksArray = line.substring(line.indexOf(' ')).split(" ");
            links.put(url, new ArrayList<>(Arrays.asList(linksArray)));
            lineCounter++;
        }

        while (defaultUrlsReader.ready()) {
            String url = defaultUrlsReader.readLine();
            if (url.isEmpty()) {
                System.err.println("Warning: there is empty url in DEFAULT URLS dataset. " +
                        "Check its correctness! Line number: " + (1 + lineCounter));
                continue;
            }
            defaultUrls.add(url);
        }
    }

    /**
     * Removes 'trash links' from links dataset.
     * Trash links are links that not presented in content dataset (links which urls
     * were marked as 'valid' while crawling websites, but which were refused while
     * parsing page - for some reasons: unsupported page content-type or any other)
     */
    private static void cleanTrash() {
        // TODO check in crawler: if header link (link-key) is always presented in content (this method checks only sublinks)
        Iterator<Map.Entry<String, List<String>>> iterator = links.entrySet().iterator();
        while (iterator.hasNext()) {
            List<String> linksList = iterator.next().getValue();
            Iterator<String> linksIterator = linksList.iterator();
            while (linksIterator.hasNext()) {
                String url = linksIterator.next();
                if (!contents.containsKey(url)) {
                    // TODO check that iterator removes url extracted above (not the next one)
                    linksIterator.remove();
                }
            }
        }
    }

    /**
     * This method checks if all urls presented in default urls dataset are included in links dataset
     */
    private static void checkDefaultUrlsIntegrity() {
        for (String defaultUrl : defaultUrls) {
            if (!links.containsKey(defaultUrl)) {
                System.err.println("Urls integrity issue occurred - " +
                        "url from default dataset is not included into links: " + defaultUrl);

            }
        }
    }

    private static void createCuttenLinksDataset() {
        // TODO check that such constructor call makes deep copy of collection
        Map<String, String> cuttenContents = new HashMap<>(contents);
        Map<String, List<String>> cuttenLinks = new HashMap<>(links);
        // Variable meaning: each link of this list should be removed AFTER adding its links list to all lists that contain this link
        Set<String> urlsToRemove = new HashSet<>();
        for (String defaultUrl : defaultUrls) {
            List<String> currentLinksList = links.get(defaultUrl);
            for (String keyUrl : currentLinksList) {
                if (links.containsKey(keyUrl)) {
                    urlsToRemove.add(keyUrl);
                }
            }
        }
        for (String linkToRemove : urlsToRemove) {
            cuttenLinks.remove(linkToRemove);
            cuttenContents.remove(linkToRemove);
        }

        try {
            saveData(cuttenContents, cuttenLinks);
            createIndexedDataset(cuttenContents, cuttenLinks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createMergedLinksDataset() {
        // TODO check that such constructor call makes deep copy of collection
        Map<String, List<String>> mergedLinks = new HashMap<>(links);
        // Variable meaning: each link of this list should be removed AFTER adding its links list to all lists that contain this link
        Set<String> urlsToRemove = new HashSet<>();
        for (String defaultUrl : defaultUrls) {
            List<String> currentLinksList = links.get(defaultUrl);
            // Variable meaning: it is used to prevent modifying list of links while iterating it
            List<String> linksToAppend = new ArrayList<>();
            for (String keyUrl : currentLinksList) {
                if (links.containsKey(keyUrl)) {
                    linksToAppend.addAll(links.get(keyUrl));
                    urlsToRemove.add(keyUrl);
                }
            }
            currentLinksList.addAll(linksToAppend);
        }
        for (String linkToRemove : urlsToRemove) {
            mergedLinks.remove(linkToRemove);
        }

        try {
            saveData(contents, mergedLinks);
            createIndexedDataset(contents, mergedLinks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createIndexedDataset(Map<String, String> pageContentMap, Map<String, List<String>> linksMap) {
        // TODO implement
    }

    /**
     * Just a crutch, baby. Nothing more
     */
    private static void saveData(Map<String, String> pageContentMap, Map<String, List<String>> linksMap)
            throws IOException {
        // TODO Listen, man. Copy here implementation from DatasetPreparator, set all required constants and be happy of this fucking life
    }

    /**
     * This method checks if all keys of links dataset are presented in default dataset
     */
    private static void checkLinksIntegrity() {
        // Variable meaning: it is used to prevent modifying list of default urls while iterating it
//        List<String> listToRemove = new ArrayList<>();
        for (String keyUrl : links.keySet()) {
            if (!defaultUrls.contains(keyUrl)) {
                System.err.println("Links integrity issue found - " +
                        "links dataset url is not presented in default urls: " + keyUrl);
//                listToRemove.add(keyUrl);
            }
        }

//        for (String keyUrl : listToRemove) {
//            defaultUrls.remove(keyUrl);
//            links.remove(keyUrl);
//        }
    }
}
