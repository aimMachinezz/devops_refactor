package top.lazyr.io.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/6/16
 */
public class TxtInput {
    private static String prePath = "";

    public static List<String> readFileByLine(String fileName) {
        File file = new File(prePath + fileName);
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        List<String> content = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                content.add(tempStr);
            }
            reader.close();
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return content;
    }
}
