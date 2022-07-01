package top.lazyr.refactor.algorithm.random.objectivefunction;


import top.lazyr.refactor.algorithm.random.model.Solution;

/**
 * @author lazyr
 * @created 2022/2/12
 */
public abstract class AbstractObjectiveFunction {
    protected String objectiveFunctionTitle;

    public abstract double getValue(Solution solution);

    public String getObjectiveTitle() {
        return this.objectiveFunctionTitle;
    }

    @Override
    public String toString() {
        return this.objectiveFunctionTitle;
    }
}
