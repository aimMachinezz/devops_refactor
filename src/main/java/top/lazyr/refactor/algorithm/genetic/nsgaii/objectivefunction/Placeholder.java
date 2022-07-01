package top.lazyr.refactor.algorithm.genetic.nsgaii.objectivefunction;


import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.Chromosome;

/**
 * @author lazyr
 * @created 2022/2/7
 */
public class Placeholder extends AbstractObjectiveFunction{
    public Placeholder() {
        this.objectiveFunctionTitle = "占位目标函数";
    }

    /**
     * 不做任何处理，该目标无法优化，可以使用该目标函数将NSGA2转换为单目标优化
     * @param chromosome
     * @return
     */
    @Override
    public double getValue(Chromosome chromosome) {
        return 0;
    }
}
