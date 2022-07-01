package top.lazyr.refactor.algorithm.random;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.lazyr.architecture.diagram.Graph;
import top.lazyr.refactor.algorithm.random.objectivefunction.AbstractObjectiveFunction;
import top.lazyr.refactor.generator.RefactorListGenerator;

import java.util.Arrays;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/2/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RandomConfiguration {
    /* 每个解的重构操作个数 */
    private int refactorNum;
    /* 种群大小 */
    private int populationSize;
    /* 迭代次数 */
    private int maxGenerationNum;
    /* 解生成器 */
    private RefactorListGenerator refactorListGenerator;
    /* 优化的目标函数 */
    public List<AbstractObjectiveFunction> objectives;
    private String outputCatalog;
    private boolean optimizedHL;
    private boolean optimizedUD;
    private boolean optimizedCD;
    private int currentExpTime;


    /* 源代码模型图 */
    private Graph originGraph;

    public RandomConfiguration(AbstractObjectiveFunction... objectives) {
        this.objectives = Arrays.asList(objectives);
    }

    public RandomConfiguration(List<AbstractObjectiveFunction> objectives) {
        this.objectives = objectives;
    }
}
