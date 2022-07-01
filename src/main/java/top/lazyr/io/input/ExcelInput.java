package top.lazyr.io.input;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lazyr
 * @created 2022/6/16
 */
public class ExcelInput {
    private static Logger logger = LoggerFactory.getLogger(ExcelInput.class);
    //    private static String PATH = "./src/main/resources/";
    private static String prePath = "";

    public static Map<String, List<List<String>>> readAllFromExcel(String fileName) {
        Map<String, List<List<String>>> map = new HashMap<>();
        try {
            FileInputStream inputStream = new FileInputStream(prePath + fileName);
            Workbook workbook = new XSSFWorkbook(inputStream);
            if (workbook == null) {
                logger.error("{} is not existed or the format is wrong.", fileName);
                return null;
            }
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                List<List<String>> infos = readSheet(workbook, workbook.getSheetName(i));
                map.put(workbook.getSheetName(i), infos);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static List<List<String>> readSheet(String fileName, String sheetName) {
        FileInputStream inputStream = null;
        Workbook workbook = null;
        File file = new File(prePath + fileName);
        if (!file.exists()) {
            logger.info(fileName + " is not exist");
            return new ArrayList<>();
        }
        try {
            inputStream = new FileInputStream(file);
            workbook = new XSSFWorkbook(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<List<String>> infos = readSheet(workbook, sheetName);
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                logger.error("FileInputStream close failed: " + e.getMessage());
            }
        }
        return infos;
    }

    private static List<List<String>> readSheet(Workbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        List<List<String>> infos = new ArrayList<>();
        if (sheet == null) {
            logger.info("the " + sheetName + " is not exist");
            return new ArrayList<>();
        }

        int lastRowNum = sheet.getLastRowNum();
        for (int i = 0; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            int lastCellNum = row.getLastCellNum();
            List<String> info = new ArrayList<>();
            for (int col = 0; col < lastCellNum; col++) {
                Cell cell = row.getCell(col);
                info.add(cell.getStringCellValue());
            }
            infos.add(info);
        }

        return infos;
    }
}
