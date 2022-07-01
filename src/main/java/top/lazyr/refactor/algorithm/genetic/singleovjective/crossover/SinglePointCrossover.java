package top.lazyr.refactor.algorithm.genetic.singleovjective.crossover;

import top.lazyr.util.ArrayUtil;
import top.lazyr.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/1/8
 */
public class SinglePointCrossover implements Crossover{
    private int Pc;

    public SinglePointCrossover(int pc) {
        Pc = pc;
    }

    @Override
    public List<List<String>> cross(List<String> parent1, List<String> parent2) {
        List<List<String>> children = new ArrayList<>();
        List<String> child1 = new ArrayList<>();
        List<String> child2 = new ArrayList<>();

        // 生成该次交叉概率
        int p = MathUtil.randomFromOne2N(100);

        if (p > Pc) { // 不交叉
            ArrayUtil.moveArr2Arr(child1, parent1, 0, parent1.size());
            ArrayUtil.moveArr2Arr(child2, parent2, 0, parent2.size());
        } else { // 交叉
            // TODO: 可能交叉后会生成无效的重构操作
            // 生成交叉点，0 <= k < n
            int k = MathUtil.randomFromZero(parent1.size());
            // 保留父本k点之前的基因
            ArrayUtil.moveArr2Arr(child1, parent1, 0, k);
            ArrayUtil.moveArr2Arr(child2, parent2, 0, k);
            // 交换父本k点之后的基因
            ArrayUtil.moveArr2Arr(child1, parent2, k, parent2.size());
            ArrayUtil.moveArr2Arr(child2, parent1, k, parent1.size());
        }
        children.add(child1);
        children.add(child2);

        return children;
    }

}
