package top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome;


import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.manager.GraphManager;
import top.lazyr.refactor.actuator.RefactorActuator;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.allele.AbstractAllele;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/1/29
 */
public class RefactorChromosome extends Chromosome {
    /* 初始graph，用于克隆时还传递初始graph */
    private Graph originGraph;
    /* 表现型：克隆graph后根据基因型进行重构后 */
    private Graph refactoredGraph;
    private int succeed;
    private int failed;

    public RefactorChromosome(List<? extends AbstractAllele> geneticCode, Graph graph) {
        super(geneticCode);
        this.originGraph = graph;
    }

    public RefactorChromosome(RefactorChromosome chromosome) {
        super(chromosome);
        this.originGraph = chromosome.originGraph;
    }

    public Graph getPhenotype() {
        if (refactoredGraph == null) {
            refactor();
        }
        return refactoredGraph;
    }

    public Graph getOriginGraph() {
        return originGraph;
    }

    /**
     * 深度克隆initGraph后，进行重构生成表现型
     */
    private void refactor() {
        List<String> refactors = getRefactors();
        this.refactoredGraph = GraphManager.cloneGraph(originGraph);
        int[] succeedFailed = RefactorActuator.refactor(refactoredGraph, refactors);
        succeed = succeedFailed[0];
        failed = succeedFailed[1];
    }

    public int getSucceed() {
        if (refactoredGraph == null) {
            refactor();
        }
        return succeed;
    }

    public int getFailed() {
        return failed;
    }

    public List<String> getRefactors() {
        List<AbstractAllele> geneticCode = getGeneticCode();
        List<String> refactors = new ArrayList<>();
        for (AbstractAllele abstractAllele : geneticCode) {
            refactors.add((String) abstractAllele.getGene());
        }
        return refactors;
    }

    @Override
    public Chromosome getCopy() {
        return new RefactorChromosome(this);
    }


}
