package top.lazyr.io.output;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvWriterTest {

    @Test
    public void writeCsvFile() {
        List<String> titles = Arrays.asList("a", "b");
        List<List<String>> infos = new ArrayList<>();
        infos.add(Arrays.asList("1", "2"));
        infos.add(Arrays.asList("1", "2"));
        infos.add(Arrays.asList("1", "2"));
        CsvOutput.writeCsvFile(titles, infos, "t.csv");
    }
}
