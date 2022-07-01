package top.lazyr.constant;

import java.util.List;

/**
 * 控制台打印
 * @author lazyr
 * @created 2022/1/27
 */
public interface ConsolePrinter {
    String SEPARATOR = "==========================";
    String SHORT_SEPARATOR = "============";

    static void printTitle(String title) {
        System.out.println(SEPARATOR + title + SEPARATOR);
    }

    static String createTitle(String title) {
        return SEPARATOR + title + SEPARATOR;
    }

    static void printSubTitle(String title) {
        System.out.println(title + SEPARATOR);
    }

    static void printList(List list) {
        if (list == null) {
            System.out.println("无");
            return;
        }
        for (Object o : list) {
            System.out.println(o.toString());
        }

    }
}
