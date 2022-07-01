package top.lazyr.refactor.algorithm.genetic.singleovjective.select;



import top.lazyr.refactor.algorithm.genetic.singleovjective.model.Individual;

import java.util.List;

/**
 * @author lazyr
 * @created 2022/1/7
 */
public interface Selector {
    /**
     * 在使用setIndividual后调用，根据一定规则选择一个父本
     * @return
     */
    Individual select();

    /**
     * 在使用select方法前调用，必须先设定种群（可以减少计算过程）
     * @param individuals
     */
    void setIndividual(List<Individual> individuals);
}
