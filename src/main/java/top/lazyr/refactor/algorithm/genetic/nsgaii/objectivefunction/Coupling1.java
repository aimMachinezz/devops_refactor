package top.lazyr.refactor.algorithm.genetic.nsgaii.objectivefunction;


import top.lazyr.architecture.diagram.Edge;
import top.lazyr.architecture.diagram.Graph;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.Chromosome;
import top.lazyr.refactor.algorithm.genetic.nsgaii.model.chromosome.RefactorChromosome;
import top.lazyr.refactor.algorithm.genetic.nsgaii.plugin.fitnesscalculator.FitnessCalculator;

import java.util.List;

/**
 * @author lazyr
 * @created 2022/1/29
 */
public class Coupling1 extends AbstractObjectiveFunction {
    private double originCoupling;

    public Coupling1(double originCoupling) {
        this.originCoupling = originCoupling;
        this.objectiveFunctionTitle = "耦合提升率";
    }


    public Coupling1(FitnessCalculator fitnessCalculator) {
        super(fitnessCalculator);
    }

    /**
     * 耦合度：组件之间的依赖关系之和，越小越好
     * 优化目标： 耦合度提升率
     * @param chromosome
     * @return
     */
    @Override
    public double getValue(Chromosome chromosome) {
        RefactorChromosome myChromosome = (RefactorChromosome) chromosome;
        Graph graph = myChromosome.getPhenotype();
        List<Edge> componentDependEdges = graph.filterAllComponentDependEdges();
        double currentCoupling = componentDependEdges == null ? 0 : componentDependEdges.size();
//        return 1 + ((originCoupling - currentCoupling) / originCoupling);
        return ((originCoupling - currentCoupling) / originCoupling);
    }
}
