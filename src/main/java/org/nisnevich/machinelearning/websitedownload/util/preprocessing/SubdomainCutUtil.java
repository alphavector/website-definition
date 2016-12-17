package org.nisnevich.machinelearning.websitedownload.util.preprocessing;

import edu.uci.ics.crawler4j.url.WebURL;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (16.12.2016)
 */
public class SubdomainCutUtil {

    private static String inputFileName = "input/DOMAINS_FULL.txt";
    private static String outputFileName = "input/DOMAINS_FULL_NOSUBDOMAINS.txt";

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName)));
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFileName)));

        WebURL webURL = new WebURL();
        Map<String, Boolean> domains = new HashMap<>();
        while (bufferedReader.ready()) {
            String url = bufferedReader.readLine();
            if (url == null || url.length() == 0) {
                return;
            }

            webURL.setURL(url);
            String subdomain = webURL.getSubDomain();
            String urlWithoutSubdomain = url;

            if (domains.containsKey(webURL.getDomain())) {
                continue;
            }
            if (!(subdomain.length() == 0 || subdomain.equals("www") || webURL.getDomain().matches("[0-9.]+"))) {
                urlWithoutSubdomain = url.replaceFirst(subdomain + "\\.", "www.");
            }
            printWriter.println(urlWithoutSubdomain);
            domains.put(webURL.getDomain(), true);
        }
        bufferedReader.close();
        printWriter.close();
    }
}
