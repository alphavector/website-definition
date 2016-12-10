package org.nisnevich.machinelearning.websitedownload.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * Utility to read and write files as fast as possible.
 * @author Nisnevich Arseniy
 * @version 1.0 (10.12.2016)
 */
public class FastFileSystemUtil {

    private static Charset utf8 = Charset.forName("utf-8");
    private static CharsetEncoder utf8Enc = utf8.newEncoder();
    private static CharsetDecoder utf8Dec = utf8.newDecoder();

    public static void writeFile(String fileName, String content) throws IOException {
        CharBuffer stringBuff = CharBuffer.wrap(content.toCharArray());

        // new file, get its channel
        File f = new File(fileName);
        FileChannel fch = new FileOutputStream(f).getChannel();

        ByteBuffer buff = ByteBuffer.allocate(512);
        utf8Enc.encode(stringBuff, buff, false); // write string butes to buffer

        // put some nums to buffer
        buff.putInt(1111);
        buff.putDouble(2.3);
        buff.limit(buff.position()); // cut buffer
        buff.rewind(); // set buffer's position to 0

        // write to channel
        int writed = fch.write(buff);
//        System.out.println(writed + " byted writed.");
        fch.close();
    }

    public static String readFile(String fileName) throws IOException {
        FileChannel fch = new FileInputStream(fileName).getChannel();
        long len = fch.size();
        ByteBuffer buffStr = ByteBuffer.allocate((int)(len - 12)); // allocate buffer for only string
        ByteBuffer buffNums = ByteBuffer.allocate(12); // .. and nums (int:4 bytes + double:8 bytes)
        fch.read(buffStr);
        fch.read(buffNums);

        buffStr.rewind();
        buffNums.rewind();
        CharBuffer chBuff = utf8Dec.decode(buffStr);
//        System.out.printf("Nums: %d, %f\n", buffNums.getInt(), buffNums.getDouble()); // get nums from buffer
        return chBuff.toString();
    }
}
