package top.lazyr.refactor.algorithm.genetic.singleovjective.mutation;

import java.util.List;

/**
 * @author lazyr
 * @created 2022/1/7
 */
public interface Mutation {
    /**
     * 若genotype发生了变异，则返回true
     * 若genotype未发生了变异，则返回false
     * @param genotype
     * @return
     */
    boolean mutate(List<String> genotype);
}
