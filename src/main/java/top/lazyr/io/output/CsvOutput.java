package top.lazyr.io.output;

import com.csvreader.CsvWriter;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/6/16
 */
public class CsvOutput {
    // 需要写入的 csv 文件路径
//    private static String PATH = "./src/main/resources/";
    private static String PATH = "";


    public static void writeCsvFile(List<String> titles, List<List<String>> infos, String filePath) {
        // 创建 CSV Writer 对象, 参数说明（写入的文件路径，分隔符，编码格式)
        CsvWriter csvWriter = new CsvWriter(PATH + filePath,',', Charset.forName("UTF-8"));
        try {
            // 写入 header 头
            csvWriter.writeRecord(titles.toArray(new String[0]));
            for (int i = 0; i < infos.size(); i++) {
                // 写入行
                csvWriter.writeRecord((String[])(infos.get(i)).toArray(new String[0]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            csvWriter.close();
        }
    }
}
