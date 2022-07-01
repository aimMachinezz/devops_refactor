package top.lazyr.architecture.writer;

import top.lazyr.constant.ConsolePrinter;
import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.architecture.manager.GraphManager;

import java.util.List;

/**
 * @author lazyr
 * @created 2022/1/27
 */
public class TestConsoleModelWriter {

    public static void write(Graph graph, String testNodeId) {
        Node node = graph.findNodeById(testNodeId);
        ConsolePrinter.printTitle("测试Node详细信息");
        if (node == null) {
            System.out.println(testNodeId + " 不存在");
            return;
        }
        printDependInfo(graph, node);
        if (node.isComponent()) {
            printSubNode(graph, node);
        } else {
            printBelongComponent(graph, node);
        }

    }

    private static void printBelongComponent(Graph graph, Node node) {
        ConsolePrinter.printTitle("文件所属组件");
        Node componentNode = GraphManager.findBelongComponent(graph, node);
        if (componentNode != null) {
            System.out.println(componentNode);
        } else {
            System.out.println("无");
        }
    }

    private static void printSubNode(Graph graph, Node node) {
        List<Node> subNodes = GraphManager.findSubFileNodes(graph, node);
        ConsolePrinter.printTitle("组件内文件: " + (subNodes == null ? 0 : subNodes.size()));
        if (subNodes != null) {
            for (Node subNode : subNodes) {
                System.out.println(subNode);
            }
        } else {
            System.out.println("无");
        }
    }

    private static void printDependInfo(Graph graph, Node node) {
        if (graph == null) {
            return;
        }
        System.out.println(node.getId());
        ConsolePrinter.printTitle("传入节点个数: " + node.getAfferentNum());
        List<Node> afferentNodes = GraphManager.findAfferentNodes(graph, node);
        if (afferentNodes != null) {
            for (Node afferentNode : afferentNodes) {
                System.out.println(afferentNode);
            }
        }  else {
            System.out.println("无");
        }
        ConsolePrinter.printTitle("传出节点个数: " + node.getEfferentNum());
        List<Node> efferentNodes = GraphManager.findEfferentNodes(graph, node);
        if (efferentNodes != null) {
            for (Node efferentNode : efferentNodes) {
                System.out.println(efferentNode);
            }
        } else {
            System.out.println("无");
        }
    }
}
