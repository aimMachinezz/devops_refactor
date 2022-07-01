package top.lazyr.refactor.algorithm.random.objectivefunction;

import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.refactor.algorithm.random.model.Solution;
import top.lazyr.smell.detector.cyclicdependency.CyclicDependencyDetector;
import top.lazyr.smell.detector.hublikedependency.HubLikeDependencyDetector;
import top.lazyr.smell.detector.unstabledependency.UnstableDependencyDetector;

import java.util.List;
import java.util.Map;

/**
 * @author lazyr
 * @created 2022/2/12
 */
public class SmellEliminationRate extends AbstractObjectiveFunction{
    /* 初始异味个数 */
    private int originSmellNum;
    /* 是否重构枢纽型异味（三种异味至少包含一种） */
    private boolean refactorHL;
    /* 是否重构不稳定依赖异味（三种异味至少包含一种） */
    private boolean refactorUD;
    /* 是否重构环依赖异味（三种异味至少包含一种） */
    private boolean refactorCD;

    public SmellEliminationRate(int originSmellNum, boolean refactorHL, boolean refactorUD, boolean refactorCD) {
        this.originSmellNum = originSmellNum;
        this.refactorHL = refactorHL;
        this.refactorUD = refactorUD;
        this.refactorCD = refactorCD;
        this.objectiveFunctionTitle = (refactorHL ? "枢纽型异味 " : "") +
                (refactorUD ? "不稳定依赖异味 " : "") +
                (refactorCD ? "环依赖异味 " : "") +
                "异味消除率";
    }

    /**
     * 异味消除率：当前异味数 / 初始异味数
     * 由于多目标优化是求最大值，而异味消除率是越小越好，则以 初始异味数 / 当前异味数 为优化目标
     * @param solution
     * @return
     */
    @Override
    public double getValue(Solution solution) {
        Graph graph = solution.getPhenotype();
        int currentSmellNum = 0;
        if (refactorHL) {
            Map<Node, List<Integer>> hlSmellInfo = HubLikeDependencyDetector.detect(graph);
            currentSmellNum += hlSmellInfo.size();
        }

        if (refactorUD) {
            Map<Node, List<Node>> udSmellInfo = UnstableDependencyDetector.detect(graph);
            currentSmellNum += udSmellInfo.size();
        }

        if (refactorCD) {
            List<Node> cdSmellInfo = CyclicDependencyDetector.detect(graph);
            currentSmellNum += cdSmellInfo.size();
        }

//        double eliminationRate = currentSmellNum == 0 ? initSmellNum * 2 : initSmellNum / (double)currentSmellNum;
        double eliminationRate = ((originSmellNum - currentSmellNum) / originSmellNum);
        return eliminationRate;
    }
}
