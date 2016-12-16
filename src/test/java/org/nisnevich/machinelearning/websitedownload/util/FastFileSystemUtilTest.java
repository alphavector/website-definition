package org.nisnevich.machinelearning.websitedownload.util;

import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (16.12.2016)
 */
public class FastFileSystemUtilTest {
    @org.junit.Test
    public void writeFileSpeedTest() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            stringBuilder.append("1234567890");
        }
        String content = stringBuilder.toString();

        stringBuilder = new StringBuilder();
        long time = System.currentTimeMillis();
        FastFileSystemUtil.writeFile("cache/fast3", content);
        stringBuilder.append("FastUtil result: ").append((double) (System.currentTimeMillis() - time) / 1000).append("\n");

        time = System.currentTimeMillis();
        FileUtils.writeByteArrayToFile(new File("cache/futil3"), content.getBytes());
        stringBuilder.append("FileUtils result: ").append((double) (System.currentTimeMillis() - time) / 1000);

        System.out.println(stringBuilder.toString());
    }

}