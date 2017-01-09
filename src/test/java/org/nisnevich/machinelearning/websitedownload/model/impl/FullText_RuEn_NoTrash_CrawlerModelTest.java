package org.nisnevich.machinelearning.websitedownload.model.impl;

import org.jsoup.Jsoup;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by arseniy on 08.01.17.
 */
public class FullText_RuEn_NoTrash_CrawlerModelTest {
    @Test
    public void parsePage() throws Exception {
        System.out.println("<div>qwe</div>asd"
                .replaceAll("<[^>]*>", " ")
                .replaceAll("\n", " ")
                .replaceAll("[^a-zA-Zа-яА-Я ]", " ")
                .replaceAll("\\s+", " ").trim()
        );
    }

}