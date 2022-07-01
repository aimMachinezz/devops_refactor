package top.lazyr.refactor.algorithm.genetic.singleovjective.crossover;

import java.util.List;

/**
 * @author lazyr
 * @created 2022/1/7
 */
public interface Crossover {
    /**
     * 将genotype1、genotype2两个基因型进行交叉
     * - 不改变genotype1、genotype2基因型，返回两个新的基因型
     * @param genotype1
     * @param genotype2
     * @return
     */
    List<List<String>> cross(List<String> genotype1, List<String> genotype2);
}
