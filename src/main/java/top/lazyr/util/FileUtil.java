package top.lazyr.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyr
 * @created 2021/11/4
 */
public class FileUtil {
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
//    private static String prefixPath = "./src/main/resources/";
    private static String prefixPath = "";

    /**
     * 获取传入路径 catalog 下的所有后缀为 content 文件的绝对路径
     * @param catalog
     * @return
     */
    public static List<String> getFilesAbsolutePath(String catalog, String content) {
        List<String> filesAbsolutePath = new ArrayList<>();
        getFilesAbsolutePath(catalog, content,filesAbsolutePath);
        return filesAbsolutePath;
    }

    /**
     * 获取传入路径 catalog 下的所有文件名包含 suffix 文件的绝对路径
     * @param catalog
     * @return
     */
    public static List<String> getFileNames(String catalog, String suffix) {
        List<File> files = new ArrayList<>();
        getFiles(catalog, suffix, files);
        List<String> fileNames = new ArrayList<>();
        for (File file : files) {
            fileNames.add(file.getName());
        }
        return fileNames;
    }

    private static void getFiles(String path, String content, List<File> files) {
        File catalog = new File(path);
        if (!catalog.exists()) {
            logger.info(path + "不存在");
            return;
        }

        if (catalog.isDirectory()) { // 若是文件夹
            for (File subFile : catalog.listFiles()) {
                getFiles(subFile.getAbsolutePath(), content, files);
            }
        } else if (catalog.isFile() && catalog.getName().contains(content)) { // TODO: 优化文件名后缀判断逻辑
            files.add(catalog);
        }
    }

    public static File[] getSubFiles(String catalogPath) {
        File catalog = new File(prefixPath + catalogPath);
        if (!catalog.isDirectory()) {
            return null;
        }
        return catalog.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return !file.isDirectory();
            }
        });
    }

    public static void deleteFiles(List<String> filePaths) {
        if (filePaths == null || filePaths.size() == 0) {
            return;
        }
        for (String filePath : filePaths) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        }
    }

    private static void getFilesAbsolutePath(String path, String suffix, List<String> filesAbsolutePath) {
        File file = new File(path);
        if (!file.exists()) {
            logger.info(path + "不存在");
            return;
        }

        if (file.isDirectory()) { // 若是文件夹
            for (File subFile : file.listFiles()) {
                getFilesAbsolutePath(subFile.getAbsolutePath(), suffix, filesAbsolutePath);
            }
        } else if (file.isFile() && file.getName().contains(suffix)) { // TODO: 优化文件名后缀判断逻辑
            filesAbsolutePath.add(file.getAbsolutePath());
        }
    }

    public static int catalogNum(String path) {
        File catalog = new File(prefixPath + path);
        if (!catalog.isDirectory()) {
            return 0;
        }
        File[] files = catalog.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });
        return catalog.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        }).length;
    }

    public static void createCatalog(String path) {
        File catalog = new File(prefixPath + path);
        if (catalog.exists()) {
            return;
        }
        catalog.mkdirs();
    }

    /**
     * 删除目录下的所有文件
     * @param path 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    public static boolean clearDir(String path) {
        File directory = new File(prefixPath + path);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files == null || files.length == 0) {
                return true;
            }
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * 修改文件后缀名
     * @param filePath
     * @param newSuffix
     * @return
     */
    public static String changeFileSuffix(String filePath, String newSuffix) {
        if (!filePath.contains(".")) {
            return filePath + "." + newSuffix;
        }
        return filePath.substring(0, filePath.lastIndexOf(".")) + "." + newSuffix;
    }


}
