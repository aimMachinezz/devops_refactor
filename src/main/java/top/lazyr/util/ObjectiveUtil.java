package top.lazyr.util;

import top.lazyr.architecture.diagram.Edge;
import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.architecture.manager.GraphManager;

import java.util.*;

/**
 * @author lazyr
 * @created 2022/2/7
 */
public class ObjectiveUtil {

    public static double calCoupling(Graph graph) {
        List<Edge> dependEdges = graph.filterAllComponentDependEdges();
        return dependEdges == null ? 0 : dependEdges.size();

    }

    public static double calCohesion(Graph graph) {
        List<Node> systemComponentNodes = graph.filterSystemComponentNodes();
        double cohesion = 0d;
        for (Node systemComponentNode : systemComponentNodes) {
            cohesion += calCohesion(graph, systemComponentNode);
        }
        return cohesion;
    }

    public static double calSystemCohesion(Graph graph) {
        List<Node> systemComponentNodes = graph.filterSystemComponentNodes();
        double cohesion = 0d;
        for (Node systemComponentNode : systemComponentNodes) {
            cohesion += calSystemCohesion(graph, systemComponentNode);
        }
        return cohesion;
    }

    /**
     * 计算systemComponentNode内聚性
     * cohesion = (cidc + ciuc) / 2
     * @param graph
     * @param systemComponentNode
     * @return
     */
    private static double calSystemCohesion(Graph graph, Node systemComponentNode) {
        List<Node> subFileNodes = GraphManager.findSubFileNodes(graph, systemComponentNode);
        if (subFileNodes == null) { // 空组件，无内聚性
            return 0d;
        }
        // 初始化接口文件、依赖接口文件的外部组件、接口文件依赖的文件
        Map<Node, Set<Node>> interfaceAfferentComponent = new HashMap<>();
        Map<Node, Set<Node>> interfaceSystemEfferentFile = new HashMap<>();
        for (Node subFileNode : subFileNodes) {
            Set<Node> afferentComponentNodes = GraphManager.findAfferentComponentNodes(graph, subFileNode);
            if (afferentComponentNodes == null) {
                continue;
            }
            afferentComponentNodes.remove(systemComponentNode);
            if (afferentComponentNodes.size() > 0) { // 若有外部组件依赖
                interfaceAfferentComponent.put(subFileNode, afferentComponentNodes);
                List<Node> efferentFileNodes = GraphManager.findSystemEfferentNodes(graph, subFileNode);
                interfaceSystemEfferentFile.put(subFileNode, efferentFileNodes == null ? null : new HashSet<>(efferentFileNodes));
            }
        }

        double cidc = calCIDC(interfaceSystemEfferentFile);
        double ciuc = calCIUC(interfaceAfferentComponent);
        double cohesion = (cidc + ciuc) / 2;
        return cohesion;
    }

    /**
     * 计算systemComponentNode内聚性
     * cohesion = (cidc + ciuc) / 2
     * @param graph
     * @param systemComponentNode
     * @return
     */
    private static double calCohesion(Graph graph, Node systemComponentNode) {
        List<Node> subFileNodes = GraphManager.findSubFileNodes(graph, systemComponentNode);
        if (subFileNodes == null) { // 空组件，无内聚性
            return 0d;
        }
        // 初始化接口文件、依赖接口文件的外部组件、接口文件依赖的文件
        Map<Node, Set<Node>> interfaceAfferentComponent = new HashMap<>();
        Map<Node, Set<Node>> interfaceEfferentFile = new HashMap<>();
        for (Node subFileNode : subFileNodes) {
            Set<Node> afferentComponentNodes = GraphManager.findAfferentComponentNodes(graph, subFileNode);
            if (afferentComponentNodes == null) {
                continue;
            }
            afferentComponentNodes.remove(systemComponentNode);
            if (afferentComponentNodes.size() > 0) { // 若有外部组件依赖
                interfaceAfferentComponent.put(subFileNode, afferentComponentNodes);
                List<Node> efferentFileNodes = GraphManager.findEfferentNodes(graph, subFileNode);
                interfaceEfferentFile.put(subFileNode, efferentFileNodes == null ? null : new HashSet<>(efferentFileNodes));
            }
        }


        double cidc = calCIDC(interfaceEfferentFile);
        double ciuc = calCIUC(interfaceAfferentComponent);
        double cohesion = (cidc + ciuc) / 2;
        return cohesion;
    }

    /**
     * 计算CIDC（Component Interface Data Cohesion，组件接口数据内聚度）
     * CIDC(C_i) = |common(depend(interfaceFile_ik)}| / totalFileTypes , interfaceFile_ik为组件C_i中有其他组件传入依赖的文件
     * - {common(depend(interfaceFile_ik))} : 依赖文件的交集集合数
     * - totalFileTypes : 所有依赖文件类型的集合数
     * @param interfaceEfferentFile
     * @return
     */
    private static double calCIDC(Map<Node, Set<Node>> interfaceEfferentFile) {
        Set<Node> intersection = new HashSet<>();
        Set<Node> union = new HashSet<>();
        List<Node> interfaceFileNodes = new ArrayList<>(interfaceEfferentFile.keySet());
        int firstNotNull = 0;
        while (intersection.size() == 0 && firstNotNull < interfaceFileNodes.size()) { // 获取第一个至少有一个传出依赖的接口文件
            Node interfaceFileNode = interfaceFileNodes.get(firstNotNull++);
            Set<Node> efferentFileNodes = interfaceEfferentFile.get(interfaceFileNode);
            if (efferentFileNodes != null) {
                intersection.addAll(efferentFileNodes);
                union.addAll(efferentFileNodes);
            }
        }

        for (int i = firstNotNull; i < interfaceFileNodes.size(); i++) {
            Node interfaceFileNode = interfaceFileNodes.get(i);
            Set<Node> efferentFileNodes = interfaceEfferentFile.get(interfaceFileNode);
            if (efferentFileNodes == null) {
                continue;
            }
            intersection.retainAll(efferentFileNodes);
            union.addAll(efferentFileNodes);
        }

        // 若无接口文件或接口文件无传出依赖，则认为高度内聚
        return union.size() == 0 ? 1 : intersection.size() / ((double)union.size());
    }

    /**
     * 计算CIUC（Component Interface Usage Cohesion，组件接口使用内聚度）
     * CIUC(C_i) = |{invoke(Clients, interfaceFile_ik)}| / |{clients} * {interfaceFile_ik}|，interfaceFile_ik为组件C_i中有其他组件传入依赖的文件
     * - {invoke(Clients, interfaceFile_ik)} : 调用文件k的外部组件数量 - 1
     * - {clients} * {interfaceFile_ik} : 调用该组件的组件数量 * 对外提供服务文件的数量
     * @param interfaceAfferentComponent
     * @return
     */
    private static double calCIUC(Map<Node, Set<Node>> interfaceAfferentComponent) {
        // 总调用次数
        double totalCall = 0d;
        // 调用接口的组件并集个数
        double afferentComponents = 0d;
        // 接口总个数
        double totalInterfaces = interfaceAfferentComponent.size();

        Set<Node> unionAfferentComponentNodes = new HashSet<>();
        for (Node interfaceFileNode : interfaceAfferentComponent.keySet()) {
            Set<Node> afferentComponentNodes = interfaceAfferentComponent.get(interfaceFileNode);
            totalCall += afferentComponentNodes.size();
            unionAfferentComponentNodes.addAll(afferentComponentNodes);
        }
        afferentComponents = unionAfferentComponentNodes.size();

        // 若totalInterfaces == 0，则认为无接口，则接口使用度为0
        return totalInterfaces == 0 ? 0 : (totalCall - 1) / (afferentComponents * totalInterfaces);
    }

}
