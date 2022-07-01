package top.lazyr.smell.detector.hublikedependency;

import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.smell.metrics.ComponentMetricsCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lazyr
 * @created 2022/1/27
 */
public class HubLikeDependencyDetector {
    /**
     * 返回异味检测结果相关信息
     * key为异味组件Node
     * value为key的传入和传出数
     * @param graph
     * @return
     */
    public static Map<Node, List<Integer>> detect(Graph graph) {
        Map<Node, List<Integer>> smellInfo = new HashMap<>();
        List<Node> systemComponentNodes = graph.filterSystemComponentNodes();
        if (systemComponentNodes == null) {
            return smellInfo;
        }
        int[] medianAfferentEfferent = ComponentMetricsCalculator.calHubLikeMetrics(graph);
        int medianAfferent = medianAfferentEfferent[0];
        int medianEfferent = medianAfferentEfferent[1];
        for (Node systemComponentNode : systemComponentNodes) {
            int afferentNum = systemComponentNode.getAfferentNum();
            int efferentNum = systemComponentNode.getEfferentNum();
            if (afferentNum > medianAfferent &&
                    efferentNum > medianEfferent &&
                    Math.abs(afferentNum - efferentNum) <= (afferentNum + efferentNum) / 4) {
                List<Integer> info  = new ArrayList<>();
                info.add(afferentNum);
                info.add(efferentNum);
                smellInfo.put(systemComponentNode, info);
            }
        }
        return smellInfo;
    }
}
