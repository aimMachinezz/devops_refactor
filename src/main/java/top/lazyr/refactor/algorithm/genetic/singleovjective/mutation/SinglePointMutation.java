package top.lazyr.refactor.algorithm.genetic.singleovjective.mutation;

import top.lazyr.refactor.generator.RefactorListGenerator;
import top.lazyr.util.MathUtil;

import java.util.List;

/**
 * @author lazyr
 * @created 2022/1/8
 */
public class SinglePointMutation implements Mutation{
    private RefactorListGenerator refactorList;
    private int Pm;

    public SinglePointMutation(RefactorListGenerator refactorList, int Pm) {
        this.refactorList = refactorList;
        this.Pm = Pm;
    }

    @Override
    public boolean mutate(List<String> genotype) {
        // 生成变异概率p, 1 <= p <= 100
        int p = MathUtil.randomFromOne2N(100);
        if (p > Pm) { // 不变异
            return false;
        }

        // 生成变异位置k, 0 <= k < genotype.size()
        int len = genotype.size();
        int k = MathUtil.randomFromZero(len);

        // TODO: 可能会生成无效后代
        // 随机生成一个新的重构操作进行替换
        String newRefactor = refactorList.generateOne(len);
        genotype.set(k, newRefactor);
        return true;
    }
}
