package top.lazyr.refactor.algorithm.random.objectivefunction;


import top.lazyr.architecture.diagram.Edge;
import top.lazyr.architecture.diagram.Graph;
import top.lazyr.refactor.algorithm.random.model.Solution;

import java.util.List;

/**
 * @author lazyr
 * @created 2022/2/12
 */
public class Coupling extends AbstractObjectiveFunction{
    private double originCoupling;

    public Coupling(double originCoupling) {
        this.originCoupling = originCoupling;
        this.objectiveFunctionTitle = "耦合提升率";
    }

    /**
     * 耦合度：组件之间的依赖关系之和，越小越好
     * 优化目标： 耦合度提升率
     * @param solution
     * @return
     */
    @Override
    public double getValue(Solution solution) {
        Graph graph = solution.getPhenotype();
        List<Edge> componentDependEdges = graph.filterAllComponentDependEdges();
        double currentCoupling = componentDependEdges == null ? 0 : componentDependEdges.size();
        return ((originCoupling - currentCoupling) / originCoupling);
    }

}
