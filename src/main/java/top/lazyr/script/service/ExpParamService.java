package top.lazyr.script.service;

import cn.hutool.poi.excel.ExcelUtil;
import top.lazyr.io.output.ExcelOutPut;
import top.lazyr.io.output.TxtOutput;
import top.lazyr.script.dao.ExpParamDao;
import top.lazyr.script.model.ExpParam;
import top.lazyr.util.FileUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/2/23
 */
public class ExpParamService {
    public static List<ExpParam> getUnFinishedExpParams(String configFilePath) {
        List<ExpParam> expParams = ExpParamDao.getExpParams(configFilePath);
        List<ExpParam> unFinishedExpParams = new ArrayList<>();
        for (ExpParam expParam : expParams) {
            if (!expParam.isFinished()) {
                unFinishedExpParams.add(expParam);
            }
        }
        return unFinishedExpParams;
    }

    public static void finishExpParam(ExpParam expParam, int currentExpTime, String outputCatalog) {
        String configFilePath = expParam.getConfigFilePath();
        expParam.setFinished(true);
        ExcelOutPut.updateRowCol(configFilePath, "data", expParam.getIndex(), 0, "true");
        List<String> finishFilePaths = FileUtil.getFilesAbsolutePath(outputCatalog, "finish");
        FileUtil.deleteFiles(finishFilePaths);
        TxtOutput.write2File(outputCatalog + currentExpTime + ".finish", "");
    }

    /**
     * 若不存在n.finish, 则返回0
     * 若存在n.finish, 则返回n
     * 并删除indicators_[n + 1].xlsx、accuracies_[n + 1].xlsx、paretoFronts_[n + 1].xlsx、refactors_[n + 1].txt文件
     * @param outputCatalog
     * @return
     */
    public static int clear(String outputCatalog) {
        List<String> finishFileNames = FileUtil.getFileNames(outputCatalog, ".finish");
        int expTime = 0;
        for (String finishFileName : finishFileNames) {
            int tempExpTime = extractFinishedExpTime(finishFileName);
            expTime = expTime > tempExpTime ? expTime : tempExpTime;
        }


        List<String> indicatorsFile = FileUtil.getFilesAbsolutePath(outputCatalog, "indicators_" + (expTime + 1) + ".xlsx");
        List<String> accuraciesFile = FileUtil.getFilesAbsolutePath(outputCatalog, "accuracies_" + (expTime + 1) + ".xlsx");
        List<String> paretoFrontsFile = FileUtil.getFilesAbsolutePath(outputCatalog, "fronts_" + (expTime + 1) + ".xlsx");
        List<String> refactorsFile = FileUtil.getFilesAbsolutePath(outputCatalog, "refactors_" + (expTime + 1) + ".txt");
        // 若存在上次未完成的实验，则删除上次实验数据
        FileUtil.deleteFiles(indicatorsFile);
        FileUtil.deleteFiles(accuraciesFile);
        FileUtil.deleteFiles(paretoFrontsFile);
        FileUtil.deleteFiles(refactorsFile);
        return expTime;
    }

    private static int extractFinishedExpTime(String finishFileName) {
        return Integer.parseInt(finishFileName.split("\\.")[0]);
    }
}
