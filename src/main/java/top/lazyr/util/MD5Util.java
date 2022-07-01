package top.lazyr.util;

import java.security.MessageDigest;

/**
 * @author lazyr
 * @created 2022/2/22
 */
public class MD5Util {
    private static MessageDigest md5;

    static {
        try {
            //生成实现指定摘要算法的 MessageDigest 对象。
            md5 = MessageDigest.getInstance("MD5");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String toMD5(String plainText) {
        md5.update(plainText.getBytes());
        byte[] md5Bytes = md5.digest();
        StringBuilder md5Str = new StringBuilder();
        //生成具体的md5密码到buf数组
        int i;
        for (int offset = 0; offset < md5Bytes.length; offset++) {
            i = md5Bytes[offset];
            if (i < 0)
                i += 256;
            if (i < 16)
                md5Str.append("0");
            md5Str.append(Integer.toHexString(i));
        }
        return md5Str.toString();
    }
}
