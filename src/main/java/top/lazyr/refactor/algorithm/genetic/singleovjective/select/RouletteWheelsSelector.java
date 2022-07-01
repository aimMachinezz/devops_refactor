package top.lazyr.refactor.algorithm.genetic.singleovjective.select;



import top.lazyr.refactor.algorithm.genetic.singleovjective.model.Individual;

import java.util.ArrayList;
import java.util.List;

/**
 * 轮盘赌轮选择
 * @author lazyr
 * @created 2022/1/8
 */
public class RouletteWheelsSelector implements Selector {
    private List<Individual> individuals;
    private List<Double> probs;
    private Double sumProb;

    /**
     * 每次返回一个个体
     * @return
     */
    @Override
    public Individual select() {
        if (individuals == null) {
            return null;
        }
        int i = rotate();
        return individuals.get(i);
    }

    @Override
    public void setIndividual(List<Individual> individuals) {
        // 刷新数据
        this.individuals = individuals;
        this.probs = new ArrayList<>();
        this.sumProb = 0d;


        Double sumScore = 0d;
        for (Individual individual : individuals) {
            sumScore += individual.getScore();
        }
        for (Individual individual : individuals) {
            double prob = individual.getScore() / sumScore;
            this.probs.add(prob);
            this.sumProb += prob;
        }
    }

    /**
     * 轮盘旋转一次，获取对应下标，示例：
     * 下标1概率 => 0.1
     * 下标2概率 => 0.3
     * 下标3概率 => 0.2
     * 下标4概率 => 0.4
     * 则四个下标在0-1区间里可以划分成如下四个区域，左闭右开
     * [[0, 0.1), [0.1, 0.4), [0.4, 0.6), [0.6, 1.0)]
     * 下标1概率 => 0.1 => [0, 0.1)
     * 下标2概率 => 0.3 => [0.1, 0.4)
     * 下标3概率 => 0.2 => [0.4, 0.6)
     * 下标4概率 => 0.4 => [0.6, 1.0)
     * 可以随机生成一个指针pointer(0 <= pointer <= sumProb)，pointer为一个概率
     * 判断选择哪个下标，只需判断pointer落在哪个下标对应的区域即可
     * @return
     */
    public int rotate() {
        // 1、随机确定指针位置
        double pointer = Math.random()  * this.sumProb;
        double part = 0.0;
        // 2、确定每个下标的位置
        for (int i = 0; i < probs.size(); i++) {
            part += probs.get(i); // 计算下标i的区域上限
            if (part > pointer) {
                return i;
            }
        }

        // 由于 0 <= Math.random() < 1，所以该情况不成立，默认为最后一个下标
        return probs.size() - 1;
    }
}
