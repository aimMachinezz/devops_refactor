package top.lazyr.smell.detector.cyclicdependency;


import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.architecture.manager.GraphManager;

import java.util.*;

/**
 * @author lazyr
 * @created 2022/1/27
 */
public class CyclicDependencyDetector {
    /**
     * 返回异味检测结果相关信息
     * 返回有环依赖的组件Node
     * @param graph
     * @return
     */
    public static List<Node> detect(Graph graph) {
        List<Node> systemComponentNodes = graph.filterSystemComponentNodes();
        // 将结点链表转换为邻接矩阵，提升执行效率
        int[][] adjMatrix = convertAdjMatrix(systemComponentNodes, graph);
        // 从出发节点到当前节点的轨迹
        Stack<Integer> trace = new Stack<>();
        // 存储要打印输出的环回路
        Set<Integer> cycleNodeIndex = new HashSet<>();
        // 判断节点是否已访问
        Set<Integer> visited = new HashSet<>();
        // 记录节点的相邻节点
        Map<Integer, Integer> edgeTo = new HashMap<>();
        for (int i = 0; i < adjMatrix.length; i++) {
            if (!visited.contains(i)) {
                // DFS遍历查找环结构
                findCycle(i, trace, edgeTo, cycleNodeIndex, visited, adjMatrix);
            }
        }
        // 获取异味节点并返回
        List<Node> smellNodes = new ArrayList<>();
        for (Integer nodeIndex : cycleNodeIndex) {
            smellNodes.add(systemComponentNodes.get(nodeIndex));
        }
        return smellNodes;
    }

    public  static int[][] convertAdjMatrix(List<Node> nodes, Graph graph) {
        int[][] adjMatrix = new int[nodes.size()][nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            List<Node> efferentNodes = GraphManager.findSystemEfferentNodes(graph, nodes.get(i));
            if (efferentNodes == null) {
                continue;
            }
            for (Node efferentNode : efferentNodes) {
                adjMatrix[i][nodes.indexOf(efferentNode)] = 1;
            }
        }
        return adjMatrix;
    }

    public void findCycle(int n, List<Integer> trace, Set<Integer> cycleNodeIndex, Set<Integer> visited, int[][] adjMatrix) {
        // 标记该节点已访问
        int cycleStartIndex = trace.indexOf(n);
        if (cycleStartIndex != -1) { // 若当前位置已经被访问过，则认为存在环
            for (int i = cycleStartIndex; i < trace.size(); i++) {
                cycleNodeIndex.add(trace.get(i));
            }
            return;
        }

        trace.add(n);
        for (int i = 0; i < adjMatrix.length; i++) {
            if (adjMatrix[n][i] == 1 && visited.contains(i)) {
                findCycle(i, trace, cycleNodeIndex, visited, adjMatrix);
            }
        }
        trace.remove(trace.size() - 1);
        visited.add(n); // 该节点已访问完
    }

    public  static void findCycle(int nodeIndex, Stack<Integer> trace, Map<Integer, Integer> edgeTo, Set<Integer> cycleNodeIndex, Set<Integer> visited, int[][] adjMatrix) {
        visited.add(nodeIndex);
        trace.push(nodeIndex);
        // 获取n的邻接节点
        List<Integer> neighIndexes = new ArrayList<>();
        for (int i = 0; i < adjMatrix[nodeIndex].length; i++) {
            if (adjMatrix[nodeIndex][i] == 1) {
                neighIndexes.add(i);
            }
        }
        for (int neighIndex : neighIndexes) {
            if (!visited.contains(neighIndex)) { // 若该节点未访问
                edgeTo.put(neighIndex, nodeIndex);
                findCycle(neighIndex, trace, edgeTo, cycleNodeIndex, visited, adjMatrix);
            } else if (trace.contains(neighIndex)) { // 该节点的下一个节点已和现存路径中的节点重复，表示存在环
                for (int i = nodeIndex; i != neighIndex; i = edgeTo.get(i)) {
                    cycleNodeIndex.add(i);
                }
                cycleNodeIndex.add(neighIndex);
            }
        }
        trace.pop();
    }

}
