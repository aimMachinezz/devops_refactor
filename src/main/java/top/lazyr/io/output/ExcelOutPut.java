package top.lazyr.io.output;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/6/16
 */
public class ExcelOutPut {
    private static Logger logger = LoggerFactory.getLogger(ExcelOutPut.class);
    //    private static String PATH = "./src/main/resources/";
    private static String prePath = "";

    /**
     * 若存在[workbookName].xlsx
     * - 若存在名为[sheetName]的表格，则追加内容
     * - 若不存在名为[sheetName]的表格，则新创建
     *
     * 若不存在[workbookName].xlsx，则创建
     * @param workbookName
     * @param sheetName
     * @param infos
     */
    public static void append2Excel(String workbookName, String sheetName, List<List<String>> infos) {
        Workbook workbook = null;
        File file = new File(prePath + workbookName);
        if (file.exists()) { // 若workbookName存在，则读取
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                workbook = new XSSFWorkbook(inputStream);
            } catch (FileNotFoundException e) {
                logger.error(workbookName + " not found: " + e.getMessage());
            } catch (IOException e) {
                logger.error("read " + workbookName + " error: " + e.getMessage());
            }

        } else { // 若workbookName不存在存在，则创建
            workbook = new XSSFWorkbook();
        }
        boolean isNew = false;
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            isNew = true;
            sheet = workbook.createSheet(sheetName);
        }
        int lastRowNum = isNew ? 0 : sheet.getLastRowNum() + 1;
        for (List<String> info: infos) {
            Row row = sheet.createRow(lastRowNum++);
            for (int i = 0; i < info.size(); i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(info.get(i));
            }
        }
        // 写入数据
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(prePath + workbookName);
            workbook.write(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 无论是否存在[workbookName].xlsx，都清空内容重新写入
     * @param workbookName
     * @param sheetName
     * @param infos
     */
    public static void write2Excel(String workbookName, String sheetName, List<List<String>> infos) {
        // 1、创建一个工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);
        logger.info("创建工作簿({})并创建工作表({})", prePath + workbookName, sheetName);
        int lastRowNum = 0;
        for (List<String> info: infos) {
            Row row = sheet.createRow(lastRowNum++);
            for (int i = 0; i < info.size(); i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(info.get(i));
            }
        }

        // 2、写入数据
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(prePath + workbookName);
            workbook.write(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void updateRow(String fileName, String sheetName, int rowNum, List<String> rowInfo) {
        FileInputStream inputStream = null;
        Workbook workbook = null;
        File file = new File(prePath + fileName);
        if (!file.exists()) {
            logger.info(fileName + " is not exist");
            return;
        }
        try {
            inputStream = new FileInputStream(file);
            workbook = new XSSFWorkbook(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Sheet sheet = workbook.getSheet(sheetName);
        //获取单元格的row和cell
        // 获取行
        Row row = sheet.getRow(rowNum);
        for (int i = 0; i < rowInfo.size(); i++) {
            Cell cell = row.getCell(i);
            cell.setCellValue(rowInfo.get(i));
        }
        //写入数据
        FileOutputStream outPutStream = null;
        try {
            outPutStream = new FileOutputStream(file);
            workbook.write(outPutStream);
            outPutStream.flush();
            outPutStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void updateRowCol(String fileName, String sheetName, int rowNum, int colNum, String info) {
        FileInputStream inputStream = null;
        Workbook workbook = null;
        File file = new File(prePath + fileName);
        if (!file.exists()) {
            logger.info(fileName + " is not exist");
            return;
        }
        try {
            inputStream = new FileInputStream(file);
            workbook = new XSSFWorkbook(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Sheet sheet = workbook.getSheet(sheetName);
        //获取单元格的row和cell
        // 获取行
        Row row = sheet.getRow(rowNum);
        Cell cell = row.getCell(colNum);
        cell.setCellValue(info);
        //写入数据
        FileOutputStream outPutStream = null;
        try {
            outPutStream = new FileOutputStream(file);
            workbook.write(outPutStream);
            outPutStream.flush();
            outPutStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
