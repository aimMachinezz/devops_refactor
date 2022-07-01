package top.lazyr.io.output;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author lazyr
 * @created 2022/6/16
 */
public class TxtOutput {
    private static String prePath = "";

    public static void append2File(String filePath, String content) {
        write2File(filePath, content, true);
    }

    public static void write2File(String filePath, String content) {
        write2File(filePath, content, false);
    }

    private static void write2File(String filePath, String content, boolean append) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(prePath + filePath, append));
            out.write(content);
            out.close();
        } catch (IOException e) {
            System.out.println(filePath + " 写入失败: " + e.getMessage());
        }
    }

}
