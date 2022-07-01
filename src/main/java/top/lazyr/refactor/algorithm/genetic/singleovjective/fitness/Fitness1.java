package top.lazyr.refactor.algorithm.genetic.singleovjective.fitness;

import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.smell.detector.hublikedependency.HubLikeDependencyDetector;

import java.util.List;
import java.util.Map;

/**
 * @author lazyr
 * @created 2022/1/7
 */
public class Fitness1 implements Fitness {
    private double initialSmellNum;

    public Fitness1(double initialSmellNum) {
        this.initialSmellNum = initialSmellNum;
    }

    @Override
    public double cal(Graph phenotype) {
        double fitness = eliminationRate(phenotype);
        return fitness;
    }

    private double eliminationRate(Graph graph) {
        Map<Node, List<Integer>> smellNodes = HubLikeDependencyDetector.detect(graph);
        if (smellNodes.size() == 0){
            return 2 * initialSmellNum;
        }
        return initialSmellNum / smellNodes.size();
    }

}
