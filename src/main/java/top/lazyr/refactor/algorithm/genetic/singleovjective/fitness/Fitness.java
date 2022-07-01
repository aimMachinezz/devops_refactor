package top.lazyr.refactor.algorithm.genetic.singleovjective.fitness;


import top.lazyr.architecture.diagram.Graph;

/**
 * @author lazyr
 * @created 2022/1/7
 */
public interface Fitness {
    /**
     * 计算individual的适应度值
     * @param phenotype
     * @return
     */
    double cal(Graph phenotype);
}
