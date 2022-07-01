package top.lazyr.smell.detector.unstabledependency;

import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.architecture.manager.GraphManager;
import top.lazyr.smell.metrics.ComponentMetricsCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lazyr
 * @created 2022/1/27
 */
public class UnstableDependencyDetector {
    /**
     * 返回异味检测结果相关信息
     * key为异味组件Node
     * value为key所依赖的不稳定组件Node
     * @param graph
     * @return
     */
    public static Map<Node, List<Node>> detect(Graph graph) {
        Map<Node, List<Node>> smellInfo = new HashMap<>();
        Map<String, Double> metrics = ComponentMetricsCalculator.calUnstableMetrics(graph);
        List<Node> systemComponentNodes = graph.filterSystemComponentNodes();
        if (systemComponentNodes == null) {
            return smellInfo;
        }
        for (Node systemComponentNode : systemComponentNodes) {
            List<Node> efferentNodes = GraphManager.findEfferentNodes(graph, systemComponentNode);
            if (efferentNodes == null) {
                continue;
            }
            double size = efferentNodes.size();
            double unstableNum = 0;
            double instability = metrics.get(systemComponentNode.getId());
            List<Node> info = new ArrayList<>();
            for (Node efferentNode : efferentNodes) {
                Double dependInstability = metrics.get(efferentNode.getId());
                if (instability < dependInstability) {
                    unstableNum++;
                    info.add(efferentNode);
                }
            }
            if (unstableNum / size > 0.3) {
                smellInfo.put(systemComponentNode, info);
            }
        }
        return smellInfo;
    }
}
