package top.lazyr.refactor.algorithm.genetic.nsgaii.objectivefunction;

import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.Chromosome;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.RefactorChromosome;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.fitnesscalculator.FitnessCalculator;
import top.lazyr.smell.detector.cyclicdependency.CyclicDependencyDetector;
import top.lazyr.smell.detector.hublikedependency.HubLikeDependencyDetector;
import top.lazyr.smell.detector.unstabledependency.UnstableDependencyDetector;

import java.util.List;
import java.util.Map;

/**
 * @author lazyr
 * @created 2022/1/29
 */
public class SmellEliminationRate extends AbstractObjectiveFunction {
    /* 初始异味个数 */
    private double originSmellNum;
    /* 是否重构枢纽型异味（三种异味至少包含一种） */
    private boolean refactorHL;
    /* 是否重构不稳定依赖异味（三种异味至少包含一种） */
    private boolean refactorUD;
    /* 是否重构环依赖异味（三种异味至少包含一种） */
    private boolean refactorCD;

    public SmellEliminationRate(int initSmellNum, boolean refactorHL, boolean refactorUD, boolean refactorCD) {
        this.originSmellNum = initSmellNum;
        this.refactorHL = refactorHL;
        this.refactorUD = refactorUD;
        this.refactorCD = refactorCD;
        this.objectiveFunctionTitle = (refactorHL ? "枢纽型异味 " : "") +
                                      (refactorUD ? "不稳定依赖异味 " : "") +
                                      (refactorCD ? "环依赖异味 " : "") +
                "异味消除率";
    }

    public SmellEliminationRate(FitnessCalculator fitnessCalculator) {
        super(fitnessCalculator);
    }

    /**
     * 异味消除率：1 + ((初始异味 - 当前异味数) / 初始异味数)
     * @param chromosome
     * @return
     */
    @Override
    public double getValue(Chromosome chromosome) {
        RefactorChromosome myChromosome = (RefactorChromosome) chromosome;
        Graph refactoredGraph = myChromosome.getPhenotype();
        double currentSmellNum = 0;
        if (refactorHL) {
            Map<Node, List<Integer>> hlSmellInfo = HubLikeDependencyDetector.detect(refactoredGraph);
            currentSmellNum += hlSmellInfo.size();
        }

        if (refactorUD) {
            Map<Node, List<Node>> udSmellInfo = UnstableDependencyDetector.detect(refactoredGraph);
            currentSmellNum += udSmellInfo.size();
        }

        if (refactorCD) {
            List<Node> cdSmellInfo = CyclicDependencyDetector.detect(refactoredGraph);
            currentSmellNum += cdSmellInfo.size();
        }

//        double eliminationRate = 1 + ((originSmellNum - currentSmellNum) / originSmellNum);
        double eliminationRate = ((originSmellNum - currentSmellNum) / originSmellNum);
        return eliminationRate;
    }
}
