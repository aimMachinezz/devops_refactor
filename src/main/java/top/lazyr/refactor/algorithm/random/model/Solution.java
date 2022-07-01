package top.lazyr.refactor.algorithm.random.model;


import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.manager.GraphManager;
import top.lazyr.refactor.actuator.RefactorActuator;
import top.lazyr.refactor.algorithm.genetic.nsgaii.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/2/12
 */
public class Solution {
    private List<String> refactors;
    private final List<Double> objectiveValues;
    private final List<Double> normalizedObjectiveValues;
    private  List<Solution> dominatedSolutions;
    private double crowdingDistance = 0;
    private int dominatedCount = 0;
    private double fitness = Double.MIN_VALUE;
    private int rank = -1;
    /* 初始graph，用于克隆时还传递初始graph */
    private Graph originGraph;
    /* 表现型：克隆graph后根据基因型进行重构后 */
    private Graph refactoredGraph;
    private int succeed;
    private int failed;

    public Solution(List<String> refactors, Graph originGraph) {
        this.refactors = refactors;
        this.objectiveValues = new ArrayList<>();
        this.normalizedObjectiveValues = new ArrayList<>();
        this.dominatedSolutions = new ArrayList<>();
        this.originGraph = originGraph;
    }

    public Graph getPhenotype() {
        if (refactoredGraph == null) {
            refactor();
        }
        return refactoredGraph;
    }

    /**
     * 深度克隆initGraph后，进行重构生成表现型
     */
    private void refactor() {
        this.refactoredGraph = GraphManager.cloneGraph(originGraph);
        int[] succeedFailed = RefactorActuator.refactor(refactoredGraph, refactors);
        succeed = succeedFailed[0];
        failed = succeedFailed[1];
    }

    public void addObjectiveValue(int index, double value) {

        double roundedValue = Service.roundOff(value, 4);

        if(this.getObjectiveValues().size() <= index) this.objectiveValues.add(index, roundedValue);
        else this.objectiveValues.set(index, roundedValue);
    }

    public List<Double> getObjectiveValues() {
        return objectiveValues;
    }

    public List<String> getRefactors() {
        return refactors;
    }

    public void setRefactors(List<String> refactors) {
        this.refactors = refactors;
    }

    public List<Double> getNormalizedObjectiveValues() {
        return normalizedObjectiveValues;
    }

    public List<Solution> getDominatedSolutions() {
        return dominatedSolutions;
    }

    public void setDominatedSolutions(List<Solution> dominatedSolutions) {
        this.dominatedSolutions = dominatedSolutions;
    }

    public double getCrowdingDistance() {
        return crowdingDistance;
    }

    public void setCrowdingDistance(double crowdingDistance) {
        this.crowdingDistance = crowdingDistance;
    }

    public int getDominatedCount() {
        return dominatedCount;
    }

    public void setDominatedCount(int dominatedCount) {
        this.dominatedCount = dominatedCount;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Graph getOriginGraph() {
        return originGraph;
    }

    public void setOriginGraph(Graph originGraph) {
        this.originGraph = originGraph;
    }

    public Graph getRefactoredGraph() {
        return refactoredGraph;
    }

    public void setRefactoredGraph(Graph refactoredGraph) {
        this.refactoredGraph = refactoredGraph;
    }

    public int getSucceed() {
        return succeed;
    }

    public void setSucceed(int succeed) {
        this.succeed = succeed;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public void reset() {
        this.dominatedCount = 0;
        this.rank = -1;
        this.dominatedSolutions = new ArrayList<>();
    }

    public void addDominatedChromosome(Solution refactors) {
        this.dominatedSolutions.add(refactors);
    }

    public void incrementDominatedCount(int incrementValue) {
        this.dominatedCount += incrementValue;
    }

    public void setNormalizedObjectiveValue(int index, double value) {
        if(this.getNormalizedObjectiveValues().size() <= index) this.normalizedObjectiveValues.add(index, value);
        else this.normalizedObjectiveValues.set(index, value);
    }

    public double getObjectiveValue(int index) {

        if(index > (this.objectiveValues.size() - 1))
            throw new UnsupportedOperationException("Chromosome does not have " + (index + 1) + " objectives!");

        return this.objectiveValues.get(index);
    }
}
