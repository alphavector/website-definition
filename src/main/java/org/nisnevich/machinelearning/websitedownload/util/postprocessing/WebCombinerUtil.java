package org.nisnevich.machinelearning.websitedownload.util.postprocessing;

import org.apache.commons.io.FileUtils;
import org.nisnevich.machinelearning.websitedownload.controller.CrawlerController;
import org.nisnevich.machinelearning.websitedownload.controller.DatasetPreparator;

import java.io.*;
import java.util.*;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (17.12.2016)
 */
public class WebCombinerUtil {

    private static final String INPUT_CRAWLER_ID = "1482102449780";
    private static final String FILE_PAGE_CONTENT = "storage/output/" + INPUT_CRAWLER_ID + "/%scontent.data";
    private static final String FILE_PAGE_LINKS = "storage/output/" + INPUT_CRAWLER_ID + "/%slinks.data";
    private static final String FILE_URL_MAPPING = "storage/output/" + INPUT_CRAWLER_ID + "/%smapping.data";
    private static final String FILE_PREFIX_MERGED = "merged.";
    private static final String FILE_PREFIX_CUTTEN = "cutten.";
    private static final String FILE_PREFIX_INDEXED = "indexed.";

    private static final String SEPARATOR_URL_CONTENT = " ";
    private static final String SEPARATOR_LINKS = " ";

    static Map<String, Integer> urlMappings = new HashMap<>();
    static Map<String, String> contents = new HashMap<>();
    static Map<String, List<String>> links = new HashMap<>();
    static List<String> defaultUrls = new ArrayList<>();

    private DatasetPreparator datasetPreparator;

    public static void main(String[] args) throws IOException {
        readDataset(); // some urls are 'crossed' (prevents from duplicates)
        cleanTrash(); // cleans nothing as no 'trash' links are presented in default dataset

        createIndexedDatasetOnce(contents, links, "");

        // Reason for commenting: decision not to use cut/merge
        /*
        // Should be called BEFORE cut & merge
        checkDefaultUrlsIntegrity();// logs links that were not visited by crawler
        createCuttenLinksDataset();
        createMergedLinksDataset();
        // Should be called AFTER cut & merge
        checkLinksIntegrity();
        */
    }

    /**
     * Reads datasets of links and contents to static variables with same naming
     * @throws IOException
     */
    private static void readDataset() throws IOException {
        BufferedReader contentReader = new BufferedReader(new InputStreamReader(new FileInputStream(String.format(FILE_PAGE_CONTENT,""))));
        BufferedReader linksReader = new BufferedReader(new InputStreamReader(new FileInputStream(String.format(FILE_PAGE_LINKS,""))));
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
            String url = line.substring(0, line.indexOf(' '));
            String content = line.substring(line.indexOf(' '));
            if (url.isEmpty()) {
                System.err.println("Warning: there is empty url in CONTENTS dataset. " +
                        "Check its correctness! Line number: " + (1 + lineCounter));
                continue;
            }
            contents.put(url, content);
            // Reason for commenting: decision not to use cut/merge
//            urlMappings.put(url, lineCounter);
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
            String url = line.substring(0, line.indexOf(' '));
            if (url.isEmpty()) {
                System.err.println("Warning: there is empty url in LINKS dataset. " +
                        "Check its correctness! Line number: " + (1 + lineCounter));
                continue;
            }
            String[] linksArray = line.substring(line.indexOf(' ')).split(" ");
            LinkedList<String> listToAdd = new LinkedList<>(Arrays.asList(linksArray));
            // Adding url dublication (required for linked LDA)
            listToAdd.addFirst(url);
            links.put(url, new ArrayList<>(listToAdd));
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
     * Removes all unused urls
     */
    private static void checkDefaultUrlsIntegrity() {
        Set<String> urlsToRemove = new HashSet<>();
        for (String defaultUrl : defaultUrls) {
            if (!links.containsKey(defaultUrl)) {
                urlsToRemove.add(defaultUrl);
                // Reason for commenting: decision not to use cut/merge
//                urlMappings.remove(defaultUrl);
                System.err.println("Urls integrity issue occurred - " +
                        "url from default dataset is not included into links: " + defaultUrl);

            }
        }
        defaultUrls.removeAll(urlsToRemove);
    }

    private static void createCuttenLinksDataset() {
        // TODO check that such constructor call makes deep copy of collection
        Map<String, String> cuttenContents = new HashMap<>(contents);
        Map<String, List<String>> cuttenLinks = new HashMap<>(links);
        // Variable meaning: each link of this list should be removed AFTER adding its links list to all lists that contain this link
        Set<String> urlsToRemove = new HashSet<>();
        for (List<String> currentLinksList : links.values()) {
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
            saveData(cuttenContents, cuttenLinks, FILE_PREFIX_CUTTEN);
            createIndexedDataset(cuttenContents, cuttenLinks, FILE_PREFIX_CUTTEN);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createMergedLinksDataset() {
        // TODO check that such constructor call makes deep copy of collection
        Map<String, List<String>> mergedLinks = new HashMap<>(links);
        // Variable meaning: each link of this list should be removed AFTER adding its links list to all lists that contain this link
        Set<String> urlsToRemove = new HashSet<>();
        for (List<String> currentLinksList : links.values()) {
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
            saveData(contents, mergedLinks, FILE_PREFIX_MERGED);
            createIndexedDataset(contents, mergedLinks, FILE_PREFIX_MERGED);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createIndexedDataset(Map<String, String> pageContentMap, Map<String, List<String>> linksMap, String filePrefix)
            throws IOException {
        Map<String, String> indexedContentMap = new HashMap<>();
        Map<String, List<String>> indexedLinksMap = new HashMap<>();
        for (Map.Entry<String, String> entry : pageContentMap.entrySet()) {
            Integer indexKey = urlMappings.get(entry.getKey());
            if (indexKey == null) {
                System.err.println("Error while creating indexed dataset: " +
                        "found key in CONTENTS that should not exist. Ignoring. Url: " + entry.getKey());
                continue;
            }
            indexedContentMap.put(indexKey.toString(), entry.getKey());
        }

        for (Map.Entry<String, List<String>> entry : linksMap.entrySet()) {
            Integer indexKey = urlMappings.get(entry.getKey());
            if (indexKey == null) {
                System.err.println("Error while creating indexed dataset: " +
                        "found key in LINKS that should not exist. Ignoring. Url: " + entry.getKey());
                continue;
            }
            List<String> indexList = new ArrayList<>();
            for (String keyUrl : entry.getValue()) {
                Integer subIndexKey = urlMappings.get(keyUrl);
                if (subIndexKey == null) {
                    System.err.println(String.format("Error while creating indexed dataset: " +
                            "found key in SUBLINKS of LINK that should not exist. " +
                            "Ignoring. Key url: %s. Wrong url: %s", entry.getKey(), keyUrl));
                    continue;
                }
                indexList.add(subIndexKey.toString());
            }
            indexedLinksMap.put(indexKey.toString(), indexList);
        }

        saveData(indexedContentMap, indexedLinksMap, FILE_PREFIX_INDEXED + filePrefix);
    }

    private static void createIndexedDatasetOnce(Map<String, String> pageContentMap, Map<String, List<String>> linksMap, String filePrefix)
            throws IOException {
        Map<String, String> mappingValues = new HashMap<>();
        Map<String, String> indexedContentMap = new HashMap<>();
        Map<String, List<String>> indexedLinksMap = new HashMap<>();

        for (Map.Entry<String, String> entry : pageContentMap.entrySet()) {
            Integer indexKey = urlMappings.get(entry.getKey());
            if (indexKey == null) {
                int value = 1 + urlMappings.size();
                urlMappings.put(entry.getKey(), value);
                indexKey = value;
            }
            mappingValues.put(indexKey.toString(), entry.getKey());
            indexedContentMap.put(indexKey.toString(), entry.getValue());
        }

        addOneToOneLinks();

        Iterator<Map.Entry<String, List<String>>> iterator = linksMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<String>> entry = iterator.next();
            Integer indexKey = urlMappings.get(entry.getKey());
            if (indexKey == null) {
                // TODO check removing
                iterator.remove();
                System.err.println("Error while creating indexed dataset: " +
                        "found key in LINKS that is not represented in CONTENTS. Removing. Url: " + entry.getKey());
                continue;
            }
            List<String> indexList = new ArrayList<>();
            Iterator<String> linksIterator = entry.getValue().iterator();
            while (linksIterator.hasNext()) {
                String keyUrl = linksIterator.next();
                Integer subIndexKey = urlMappings.get(keyUrl);
                if (subIndexKey == null) {
                    // TODO check removing
                    linksIterator.remove();
                    System.err.println(String.format("Error while creating indexed dataset: " +
                            "found key in SUBLINKS of LINK that is not represented in CONTENTS. " +
                            "Removing. Key url: %s. Wrong url: %s", entry.getKey(), keyUrl));
                    continue;
                }
                indexList.add(subIndexKey.toString());
            }
            indexedLinksMap.put(indexKey.toString(), indexList);
        }

        saveData(indexedContentMap, indexedLinksMap, FILE_PREFIX_INDEXED + filePrefix);
        saveUrlMapping(mappingValues, FILE_PREFIX_INDEXED);
    }

    /**
     * Just a crutch, baby. Nothing more
     */
    private static void saveData(Map<String, String> pageContentMap, Map<String, List<String>> linksMap, String filePrefix)
            throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<String, String> urlContentPair : pageContentMap.entrySet()) {
            String pageUrl = urlContentPair.getKey();
            String pageContent = urlContentPair.getValue();
            stringBuilder.append(pageUrl).append(SEPARATOR_URL_CONTENT).append(pageContent).append("\n");
        }

        FileUtils.writeByteArrayToFile(new File(String.format(FILE_PAGE_CONTENT, filePrefix)),
                stringBuilder.toString().getBytes());

        stringBuilder = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : linksMap.entrySet()) {
            String pageUrl = entry.getKey();
            stringBuilder.append(pageUrl).append(SEPARATOR_URL_CONTENT);

            List<String> pageLinks = entry.getValue();
            for (String pageLink : pageLinks) {
                stringBuilder.append(pageLink).append(SEPARATOR_LINKS);
            }
            stringBuilder.append("\n");
        }

        FileUtils.writeByteArrayToFile(new File(String.format(FILE_PAGE_LINKS, filePrefix)),
                stringBuilder.toString().getBytes());
    }

    private static void saveUrlMapping(Map<String, String> urlMapping, String filePrefix)
            throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<String, String> indexUrlPair : urlMapping.entrySet()) {
            String index = indexUrlPair.getKey();
            String url = indexUrlPair.getValue();
            stringBuilder.append(index).append(SEPARATOR_URL_CONTENT).append(url).append("\n");
        }

        FileUtils.writeByteArrayToFile(new File(String.format(FILE_URL_MAPPING, filePrefix)),
                stringBuilder.toString().getBytes());
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

    /**
     * This method adds links that are not already represented as keys for another links.
     * Should be used immediately before saving links (to prevent errors while working with links map)
     */
    private static void addOneToOneLinks() {
        for (String key : urlMappings.keySet()) {
            if (!links.containsKey(key)) {
                links.put(key, new ArrayList<>(Arrays.asList(key)));
            }
        }
    }
}
