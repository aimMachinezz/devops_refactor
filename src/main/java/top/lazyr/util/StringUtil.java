package top.lazyr.util;

/**
 * @author lazyr
 * @created 2022/1/22
 */
public class StringUtil {
    public static String getMaxCommonString(String strA, String strB) {
        int strLong = 0;
        if (isNullOrEmpty(strA) || isNullOrEmpty(strB)) {
            return "";
        }

        String shortStr = "";
        String longStr = "";
        if (strA.length() > strB.length())
        {
            shortStr = strB;
            longStr = strA;
        }
        else
        {
            shortStr = strA;
            longStr = strB;
        }

        int startIdx = 0;
        int endIndex = 0;

        for (int i = 0; i < shortStr.length(); i++)
        {
            //从短的字符串开始，获取每一次的固定对比长度，并且长度逐渐递减
            startIdx = 0;
            endIndex = shortStr.length() - i;
            int fixlength = endIndex;

            while (endIndex <= shortStr.length())
            {
                //按固定长度，轮询获取较短字符串中的字符串作为对比串，与长字符串对比
                String compareStr = shortStr.substring(startIdx, fixlength);

                if (longStr.contains(compareStr))
                {
                    strLong = compareStr.length();
//                    System.out.println("compareStr => " + compareStr);
                    return compareStr;
                }

                //按上面的固定长度轮询
                startIdx = startIdx + 1;
                endIndex = endIndex + 1;
            }
        }

        return "";
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

}
