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
public class ClassificationTransformatorUtil {

    private static String FILE_NAME_TEMPLATE = "model-test-2-%s.%s";
    private static String FILE_SUFFIX_PHI = "phi";
    // How important other links are [0,1]
    private static String FILE_SUFFIX_CHI = "chi";
    private static String FILE_SUFFIX_THETA = "theta";

    private DatasetPreparator datasetPreparator;

    public static void main(String[] args) throws IOException {

    }

}
