package top.lazyr.smell.detector.unstabledependency;

import top.lazyr.architecture.diagram.Node;
import top.lazyr.constant.ConsolePrinter;
import top.lazyr.smell.metrics.ComponentMetricsCalculator;

import java.util.List;
import java.util.Map;

/**
 * @author lazyr
 * @created 2022/1/27
 */
public class UnstableDependencyConsoleWriter {
    public static void write(Map<Node, List<Node>> smellInfo) {
        ConsolePrinter.printTitle("不稳定异味组件个数: " + smellInfo.size());
        for (Node node : smellInfo.keySet()) {
            List<Node> dependNodes = smellInfo.get(node);
            System.out.println(node + ", instability: " + ComponentMetricsCalculator.calInstability(node) + ", 传出依赖数: " + node.getEfferentNum() + ", 不稳定依赖数: " + dependNodes.size());
            for (Node dependNode : dependNodes) {
                System.out.println("\t\t==depend==> " + dependNode + ", instability: " + ComponentMetricsCalculator.calInstability(dependNode));
            }
        }
    }

}
