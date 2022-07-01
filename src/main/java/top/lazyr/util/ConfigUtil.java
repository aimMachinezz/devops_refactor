package top.lazyr.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lazyr
 * @created 2022/2/21
 */
public class ConfigUtil {
    /**
     * currentExpTime_maxGenerationCounts_planNum_populationSize_crossoverProbability_mutationProbability_chromosomeLengthRatio_
     * optimizedHL_optimizedUD_optimizedCD_optimizedCohesion_optimizedCoupling_projectPath
     *
     * @return
     */
    public static String createConfigFileName(int currentExpTime,
                                              int maxGenerationCount,
                                              int planNum,
                                              int populationSize,
                                              float crossoverProbability,
                                              float mutationProbability,
                                              float chromosomeLengthRatio,
                                              boolean optimizedHL,
                                              boolean optimizedUD,
                                              boolean optimizedCD,
                                              boolean optimizedCohesion,
                                              boolean optimizedCoupling,
                                              String projectPath,
                                              String projectName) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder configFileName = new StringBuilder();
        configFileName.append(dateFormat.format(date) + "_");
        configFileName.append(currentExpTime + "_");
        configFileName.append(maxGenerationCount + "_");
        configFileName.append(planNum + "_");
        configFileName.append(populationSize + "_");
        configFileName.append(crossoverProbability + "_");
        configFileName.append(mutationProbability + "_");
        configFileName.append(chromosomeLengthRatio + "_");
        configFileName.append(optimizedHL + "_");
        configFileName.append(optimizedUD + "_");
        configFileName.append(optimizedCD + "_");
        configFileName.append(optimizedCohesion + "_");
        configFileName.append(optimizedCoupling + "_");
        configFileName.append(projectPath.replaceAll("/", "=") + "_");
        configFileName.append(projectName + "_config.txt");
        return configFileName.toString();
    }


}
