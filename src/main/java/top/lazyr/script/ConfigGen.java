package top.lazyr.script;

import cn.hutool.poi.excel.ExcelUtil;
import top.lazyr.io.input.TxtInput;
import top.lazyr.io.output.ExcelOutPut;
import top.lazyr.util.FileUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/2/23
 */
public class ConfigGen {
    /* 每个实验参数重复次数 */
    static int maxExpTime;
    /* 迭代最大次数 */
    static int maxGenerationCount;
    /* 重构操作列表生成方案: 1表示使用RefactorListGenerator1生成、2表示使用RefactorListGenerator2生成 */
    static List<Integer> planNums;
    /* 种群大小 */
    static List<Integer> populationSizes;
    /* 交叉概率 */
    static List<Float> crossoverProbabilities;
    /* 变异概率 */
    static List<Float> mutationProbabilities;
    /* 染色体长度比例 */
    static List<Float> chromosomeLengthRatios;
    /* 是否优化枢纽型异味 */
    static boolean optimizedHL;
    /* 是否优化不稳定异味 */
    static boolean optimizedUD;
    /* 是否优化环依赖异味 */
    static boolean optimizedCD;
    /* 是否优化内聚指标 */
    static boolean optimizedCohesion;
    /* 是否优化耦合指标 */
    static boolean optimizedCoupling;
    /* 项目源代码 */
    static String projectPath;
    /* 项目名 */
    static String projectName;

    public static void createConfigExcel(String configFilePath) {
        readConfig(configFilePath);

        List<List<String>> expParams = new ArrayList<>();
        expParams.add(Arrays.asList("finished", "index",
                "maxGenerationCount", "planNum", "populationSize", "crossoverProbability", "mutationProbability", "chromosomeLengthRatio",
                "optimizedHL", "optimizedUD", "optimizedCD", "optimizedCohesion", "optimizedCoupling",
                "projectPath", "projectName"));

        int index = 1;
        for (int expTime = 1; expTime <= maxExpTime; expTime++) { // 6、实验重复次数
            for (Integer planNum : planNums) { // 5、重构操作列表生成方案
                for (int populationSize : populationSizes) { // 4、种群大小
                    for (float crossoverProbability : crossoverProbabilities) {  // 3、交叉概率
                        for (float mutationProbability : mutationProbabilities) { // 2、变异概率
                            for (Float chromosomeLengthRatio : chromosomeLengthRatios) { // 1、染色体长度比例
                                expParams.add(Arrays.asList("false", index + "",
                                        maxGenerationCount + "", planNum + "", populationSize + "", crossoverProbability + "", mutationProbability + "", chromosomeLengthRatio + "",
                                        optimizedHL + "", optimizedUD + "", optimizedCD + "", optimizedCohesion + "", optimizedCoupling + "",
                                        projectPath, projectName));
                                index++;
                            }
                        }
                    }
                }
            }
        }

        ExcelOutPut.write2Excel(FileUtil.changeFileSuffix(configFilePath, "xlsx"), "data", expParams);
    }

    private static void readConfig(String configPath) {
        List<String> lines = TxtInput.readFileByLine(configPath);
        maxExpTime = toInt(lines.get(1));
        maxGenerationCount = toInt(lines.get(3));
        planNums = toIntList(lines.get(5));
        populationSizes = toIntList(lines.get(7));
        crossoverProbabilities = toFloatList(lines.get(9));
        mutationProbabilities = toFloatList(lines.get(11));
        chromosomeLengthRatios = toFloatList(lines.get(13));
        optimizedHL = toBool(lines.get(15));
        optimizedUD = toBool(lines.get(17));
        optimizedCD = toBool(lines.get(19));
        optimizedCohesion = toBool(lines.get(21));
        optimizedCoupling = toBool(lines.get(23));
        projectPath = lines.get(25);
        projectName = lines.get(27);
    }

    private static int toInt(String numStr) {
        return Integer.parseInt(numStr);
    }

    private static boolean toBool(String boolStr) {
        return Boolean.parseBoolean(boolStr);
    }

    private static float toFloat(String numStr) {
        return Float.parseFloat(numStr);
    }

    private static List<Integer> toIntList(String numStrs) {
        String[] numSplits = numStrs.split(",");
        List<Integer> nums = new ArrayList<>();
        for (String numSplit : numSplits) {
            nums.add(toInt(numSplit));
        }
        return nums;
    }

    private static List<Float> toFloatList(String numStrs) {
        String[] numSplits = numStrs.split(",");
        List<Float> nums = new ArrayList<>();
        for (String numSplit : numSplits) {
            nums.add(toFloat(numSplit));
        }
        return nums;
    }

}
