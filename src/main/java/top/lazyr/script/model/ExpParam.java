package top.lazyr.script.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lazyr
 * @created 2022/2/23
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpParam {
    /* 该实验参数是否完成 */
    private boolean finished;
    /* 实验序号 */
    private int index;
    /* 每个实验参数重复次数 */
    private int maxExpTime;
    /* 迭代最大次数 */
    private int maxGenerationCount;
    /* 重构操作列表生成方案: 1表示使用RefactorListGenerator1生成、2表示使用RefactorListGenerator2生成 */
    private int planNum;
    /* 种群大小 */
    private int populationSize;
    /* 交叉概率 */
    private float crossoverProbability;
    /* 变异概率 */
    private float mutationProbability;
    /* 染色体长度比例 */
    private float chromosomeLengthRatio;
    /* 是否优化枢纽型异味 */
    private boolean optimizedHL;
    /* 是否优化不稳定异味 */
    private boolean optimizedUD;
    /* 是否优化环依赖异味 */
    private boolean optimizedCD;
    /* 是否优化内聚指标 */
    private boolean optimizedCohesion;
    /* 是否优化耦合指标 */
    private boolean optimizedCoupling;
    /* 项目源代码 */
    private String projectPath;
    /* 项目名 */
    private String projectName;
    /* 配置文件的绝对路径 */
    private String configFilePath;

}
