package top.lazyr.script.dao;

import top.lazyr.io.input.ExcelInput;
import top.lazyr.script.model.ExpParam;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/2/23
 */
public class ExpParamDao {

    public static List<ExpParam> getExpParams(String configFilePath) {
        List<List<String>> expParamInfos = ExcelInput.readSheet(configFilePath, "data");
        List<ExpParam> expParams = new ArrayList<>();
        for (int i = 1; i < expParamInfos.size(); i++) {
            List<String> expParamInfo = expParamInfos.get(i);
            ExpParam expParam = ExpParam.builder()
                    .finished(toBool(expParamInfo.get(0)))
                    .index(toInt(expParamInfo.get(1)))
                    .maxGenerationCount(toInt(expParamInfo.get(2)))
                    .planNum(toInt(expParamInfo.get(3)))
                    .populationSize(toInt(expParamInfo.get(4)))
                    .crossoverProbability(toFloat(expParamInfo.get(5)))
                    .mutationProbability(toFloat(expParamInfo.get(6)))
                    .chromosomeLengthRatio(toFloat(expParamInfo.get(7)))
                    .optimizedHL(toBool(expParamInfo.get(8)))
                    .optimizedUD(toBool(expParamInfo.get(9)))
                    .optimizedCD(toBool(expParamInfo.get(10)))
                    .optimizedCohesion(toBool(expParamInfo.get(11)))
                    .optimizedCoupling(toBool(expParamInfo.get(12)))
                    .projectPath(expParamInfo.get(13))
                    .projectName(expParamInfo.get(14))
                    .configFilePath(configFilePath).build();
            expParams.add(expParam);
        }
        return expParams;
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
