package top.lazyr.smell.metrics;


import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;

import java.util.*;

/**
 * @author lazyr
 * @created 2022/1/27
 */
public class ComponentMetricsCalculator {

    /**
     * 返回所有组件的不稳定指标
     * 格式: nodeId => instability
     * @param graph
     * @return
     */
    public static Map<String, Double> calUnstableMetrics(Graph graph) {
        Map<String, Double> metrics = new HashMap<>();
        List<Node> componentNodes = graph.getComponentNodes();

        for (Node componentNode : componentNodes) {
            double instability = calInstability(componentNode);
            metrics.put(componentNode.getId(), instability);
        }
        return metrics;
    }


    /**
     * 返回graph中所有系统内组件的传入and传出依赖数的中位数
     * 格式: [medianAfferentNum, medianEfferentNum]
     * 若无系统内组件，则返回[0, 0]
     * @param graph
     * @return
     */
    public static int[] calHubLikeMetrics(Graph graph) {
        List<Node> systemComponentNodes = graph.filterSystemComponentNodes();
        if (systemComponentNodes == null) {
            return new int[]{0, 0};
        }
        List<Integer> afferentNums = new ArrayList<>();
        List<Integer> efferentNums = new ArrayList<>();

        for (Node systemComponentNode : systemComponentNodes) {
            afferentNums.add(systemComponentNode.getAfferentNum());
            efferentNums.add(systemComponentNode.getEfferentNum());
        }

        afferentNums.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });
        efferentNums.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });
        return new int[]{
                afferentNums.get(afferentNums.size() / 2),
                efferentNums.get(efferentNums.size() / 2)
        };
    }


    public static double calInstability(Node node) {
        int efferentNum = node.getEfferentNum();
        int afferentNum = node.getAfferentNum();
        double instability = efferentNum / ((double)(afferentNum + efferentNum));
        return instability;
    }
}
