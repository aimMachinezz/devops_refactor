package top.lazyr.refactor.algorithm.random.objectivefunction;


import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.architecture.manager.GraphManager;
import top.lazyr.refactor.algorithm.random.model.Solution;

import java.util.*;

/**
 * @author lazyr
 * @created 2022/2/12
 */
public class Cohesion extends AbstractObjectiveFunction {
    /**
     * key为系统内文件id
     * value为key的所有传入文件id
     */
    private Map<String, List<String>> afferentFileNodeIds;
    /**
     * key为系统内文件id
     * value为key的所有传出文件id
     */
    private Map<String, List<String>> efferentFileNodeIds;
    /* 初始内聚值 */
    private double originCohesion;

    public Cohesion(double originCohesion) {
        this.objectiveFunctionTitle = "所有组件内聚";
        this.originCohesion = originCohesion;
    }

    /**
     * 内聚值：越大越好
     * @param solution
     * @return
     */
    @Override
    public double getValue(Solution solution) {
        Graph graph = solution.getPhenotype();

        initAfferentEfferent(graph);

        List<Node> systemComponentNodes = graph.filterSystemComponentNodes();

        double currentCohesion = 0d;
        for (Node systemComponentNode : systemComponentNodes) {
            currentCohesion += calCohesion(graph, systemComponentNode);
        }

        return ((currentCohesion - originCohesion) / originCohesion);
    }

    private void initAfferentEfferent(Graph graph) {
        if (this.afferentFileNodeIds != null && this.efferentFileNodeIds != null) {
            return;
        }
        this.afferentFileNodeIds = new HashMap<>();
        this.efferentFileNodeIds = new HashMap<>();

        List<Node> systemFileNodes = graph.filterSystemFileNodes();
        if (systemFileNodes == null) {
            return;
        }
        for (Node systemFileNode : systemFileNodes) {
            List<Node> afferentNodes = GraphManager.findAfferentNodes(graph, systemFileNode);
            afferentFileNodeIds.put(systemFileNode.getId(), nodes2Ids(afferentNodes));
            List<Node> efferentNodes = GraphManager.findEfferentNodes(graph, systemFileNode);
            efferentFileNodeIds.put(systemFileNode.getId(), nodes2Ids(efferentNodes));
        }

    }

    /**
     * 计算systemComponentNode内聚性
     * cohesion = (cidc + ciuc) / 2
     * @param graph
     * @param systemComponentNode
     * @return
     */
    public double calCohesion(Graph graph, Node systemComponentNode) {
        List<Node> subFileNodes = GraphManager.findSubFileNodes(graph, systemComponentNode);
        if (subFileNodes == null) { // 空组件，无内聚性
            return 0d;
        }
        // 初始化接口文件、依赖接口文件的外部组件、接口文件依赖的文件
        Map<Node, Set<Node>> interfaceAfferentComponent = new HashMap<>();
        Map<Node, Set<Node>> interfaceEfferentFile = new HashMap<>();
        for (Node subFileNode : subFileNodes) {

            Set<Node> afferentComponentNodes = findAfferentComponentNodes(graph, subFileNode);
            if (afferentComponentNodes == null) {
                continue;
            }
            afferentComponentNodes.remove(systemComponentNode); // 移除自己依赖自己的组件
            if (afferentComponentNodes.size() > 0) { // 若有外部组件依赖
                interfaceAfferentComponent.put(subFileNode, afferentComponentNodes);
                List<Node> efferentFileNodes = GraphManager.findEfferentNodes(graph, subFileNode);
                interfaceEfferentFile.put(subFileNode, efferentFileNodes == null ? null : new HashSet<>(efferentFileNodes));
            }
        }


//        Contem.out.println("接口文件: " + interfaceAfferentComponent.keySet());
        double cidc = calCIDC(interfaceEfferentFile);
        double ciuc = calCIUC(interfaceAfferentComponent);
        double cohesion = (cidc + ciuc) / 2;
        return cohesion;
    }

    /**
     * 获取fileNode的传入文件Node所对应的组件Node
     * - 若fileNode为null或graph为null，则返回null
     * - 若fileNode不是文件粒度，则返回null
     * - 若无入出文件Node，则返回null
     * - 返回结果包含fileNode所在的组件Node
     * @param graph
     * @param fileNode
     * @return
     */
    private Set<Node> findAfferentComponentNodes(Graph graph, Node fileNode) {
        Set<Node> afferentComponentNodes = new HashSet<>();
        List<String> afferentFileNodeIds = this.afferentFileNodeIds.get(fileNode.getId());
        if (afferentFileNodeIds == null) {
            return null;
        }

        for (String afferentFileNodeId : afferentFileNodeIds) {
            afferentComponentNodes.add(GraphManager.findBelongComponent(graph, graph.findNodeById(afferentFileNodeId)));
        }
        return afferentComponentNodes;
    }

    /**
     * 计算CIDC（Component Interface Data Cohesion，组件接口数据内聚度）
     * CIDC(C_i) = |common(depend(interfaceFile_ik)}| / totalFileTypes , interfaceFile_ik为组件C_i中有其他组件传入依赖的文件
     * - {common(depend(interfaceFile_ik))} : 依赖文件的交集集合数
     * - totalFileTypes : 所有依赖文件类型的集合数
     * @param interfaceEfferentFile
     * @return
     */
    private double calCIDC(Map<Node, Set<Node>> interfaceEfferentFile) {
        if (interfaceEfferentFile.size() == 0) { // 若无接口文件，则认为高度内聚
            return 1;
        }

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
            if (efferentFileNodes != null) {
                intersection.retainAll(efferentFileNodes);
                union.addAll(efferentFileNodes);
            }

        }

        // 若接口文件无传出依赖，则认为高度内聚
        return union.size() == 0 ? 1 : intersection.size() / ((double)union.size());
    }

    /**
     * 计算CIUC（Component Interface Usage Cohesion，组件接口使用内聚度）
     * CIUC(C_i) = |{invoke(Clients, interfaceFile_ik)}| / |{clients} * {interfaceFile_ik}|，interfaceFile_ik为组件C_i中有其他组件传入依赖的文件
     * - {invoke(Clients, interfaceFile_ik)} : 调用文件k的外部组件数量 - 1
     * - {clients} * {interfaceFile_ik} : 调用该接口文件的组件数量 * 接口文件的数量
     * @param interfaceAfferentComponent
     * @return
     */
    private double calCIUC(Map<Node, Set<Node>> interfaceAfferentComponent) {
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

    /**
     * 将一组Node转换为一组id
     * 若nodes为null或size=0，则返回null
     * @param nodes
     * @return
     */
    private List<String> nodes2Ids(List<Node> nodes) {
        if (nodes == null || nodes.size() == 0) {
            return null;
        }
        List<String> ids = new ArrayList<>();
        for (Node node : nodes) {
            ids.add(node.getId());
        }
        return ids;
    }
}
