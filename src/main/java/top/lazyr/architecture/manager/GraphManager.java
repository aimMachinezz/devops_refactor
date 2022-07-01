package top.lazyr.architecture.manager;

import top.lazyr.constant.EdgeConstant;
import top.lazyr.constant.NodeConstant;
import top.lazyr.architecture.diagram.Edge;
import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;

import java.util.*;

/**
 * @author lazyr
 * @created 2022/1/27
 */
public class GraphManager {
    /**
     * 构建 (name为componentName) && (level=COMPONENT) && (from=SYSTEM)  的Node
     * @param componentName
     * @return
     */
    public static Node buildSystemComponentNode(String componentName) {
        Node node = new Node(componentName, NodeConstant.LEVEL_COMPONENT, NodeConstant.FROM_SYSTEM);
        return node;
    }

    /**
     * 构建 (name为componentName) && (level=COMPONENT) && (from=NON_SYSTEM)  的Node
     * @param componentName
     * @return
     */
    public static Node buildNonSystemComponentNode(String componentName) {
        Node node = new Node(componentName, NodeConstant.LEVEL_COMPONENT, NodeConstant.FROM_NON_SYSTEM);
        return node;
    }

    /**
     * 构建 (name为componentName) && (level=FILE) && (from=SYSTEM)  的Node
     * @param componentName
     * @return
     */
    public static Node buildSystemFileNode(String componentName) {
        Node node = new Node(componentName, NodeConstant.LEVEL_FILE, NodeConstant.FROM_SYSTEM);
        return node;
    }

    /**
     * 构建 (name为componentName) && (level=FILE) && (from=NON_SYSTEM)  的Node
     * @param componentName
     * @return
     */
    public static Node buildNonSystemFileNode(String componentName) {
        Node node = new Node(componentName, NodeConstant.LEVEL_FILE, NodeConstant.FROM_NON_SYSTEM);
        return node;
    }

    /**
     * 若componentNode不是组件，则返回null
     * 若componentNode下无文件，则返回null
     * @param graph
     * @param componentNode
     * @return
     */
    public static List<Node> findSubFileNodes(Graph graph, Node componentNode) {
        if (graph == null || componentNode == null) {
            return null;
        }
        if (!componentNode.isComponent()) { // 若不是组件Node
            return null;
        }
        List<Edge> belongEdges = graph.filterAllFileBelongEdges();
        if (belongEdges == null) { // 若系统内无belong边
            return null;
        }
        List<Node> fileNodes = new ArrayList<>();
        String componentId = componentNode.getId();
        for (Edge belongEdge : belongEdges) {
            if (belongEdge.getOutNodeId().equals(componentId)) {
                Node fileNode = graph.findNodeById(belongEdge.getInNodeId());
                fileNodes.add(fileNode);
            }
        }
        return fileNodes.size() == 0 ? null : fileNodes;
    }

    /**
     * 获取sourceNode的所有传入Node
     * - 若graph和sourceNode有一个为null，则返回null
     * - 若sourceNode无传入Node，则返回null
     * @param graph
     * @param sourceNode
     * @return
     */
    public static List<Node> findAfferentNodes(Graph graph, Node sourceNode) {
        if (graph == null || sourceNode == null) {
            return null;
        }
        List<Edge> dependEdges = sourceNode.isComponent() ? graph.filterAllComponentDependEdges() : graph.filterAllFileDependEdges();
        if (dependEdges == null) {
            return null;
        }
        List<Node> afferentNodes = new ArrayList<>();
        String sourceNodeId = sourceNode.getId();
        for (Edge dependEdge : dependEdges) {
            if (dependEdge.getOutNodeId().equals(sourceNodeId)) {
                afferentNodes.add(graph.findNodeById(dependEdge.getInNodeId()));
            }
        }
        return afferentNodes.size() == 0 ? null : afferentNodes;
    }

    /**
     * 获取sourceNode的所有系统内的传入Node
     * - 若graph和sourceNode有一个为null，则返回null
     * - 若没有sourceNode无系统内传入Node，则返回null
     * @param graph
     * @param sourceNode
     * @return
     */
    public static List<Node> findSystemAfferentNodes(Graph graph, Node sourceNode) {
        List<Node> afferentNodes = findAfferentNodes(graph, sourceNode);
        if (afferentNodes == null) {
            return null;
        }
        List<Node> systemAfferentNodes = new ArrayList<>();
        for (Node afferentNode : afferentNodes) {
            if (afferentNode.isSystem()) {
                systemAfferentNodes.add(afferentNode);
            }
        }
        return systemAfferentNodes.size() == 0 ? null : systemAfferentNodes;
    }



    /**
     * 获取sourceNode的所有传出Node
     * - 若graph和sourceNode有一个为null，则返回null
     * - 若没有sourceNode无传出Node，则返回null
     * @param graph
     * @param sourceNode
     * @return
     */
    public static List<Node> findEfferentNodes(Graph graph, Node sourceNode) {
        if (graph == null || sourceNode == null) {
            return null;
        }
        List<Edge> dependEdges = sourceNode.getDependEdges();
        List<Node> efferentNodes = new ArrayList<>();
        for (Edge dependEdge : dependEdges) {
            efferentNodes.add(graph.findNodeById(dependEdge.getOutNodeId()));
        }
        return efferentNodes.size() == 0 ? null : efferentNodes;
    }

    /**
     * 获取sourceNode的所有系统内的传出Node
     * - 若graph和sourceNode有一个为null，则返回null
     * - 若没有sourceNode无系统内传出Node，则返回null
     * @param graph
     * @param sourceNode
     * @return
     */
    public static List<Node> findSystemEfferentNodes(Graph graph, Node sourceNode) {
        List<Node> efferentNodes = findEfferentNodes(graph, sourceNode);
        if (efferentNodes == null) {
            return null;
        }
        List<Node> systemEfferentNodes = new ArrayList<>();
        for (Node efferentNode : efferentNodes) {
            if (efferentNode.isSystem()) {
                systemEfferentNodes.add(efferentNode);
            }
        }
        return systemEfferentNodes.size() == 0 ? null : systemEfferentNodes;
    }

    /**
     * 查询fileNode所属的组件粒度Node
     * - 若fileNode不是文件粒度的节点，则返null
     * - 若graph和fileNode有一个为空，则返回null
     * - 若fileNode无belong边，则返回null
     * - 若fileNode所属组件粒度未创建，则返回null
     * @param graph
     * @param fileNode
     * @return
     */
    public static Node findBelongComponent(Graph graph, Node fileNode) {
        if (graph == null || fileNode == null) {
            return null;
        }
        if (fileNode.isComponent()) {
            return null;
        }
        Edge belongEdge = fileNode.getBelongEdge();
        if (belongEdge == null) {
            return null;
        }
        return graph.findNodeById(belongEdge.getOutNodeId());
    }


    /**
     * 获取依赖fileNode的所有文件Node对应的传入组件，并获取这些传入组件依赖fileNode所对应组件Node中的fileNode，按这些传入组件分组
     * - 若fileNode不是文件Node，则返回null
     * - 若fileNode无传入文件Node，则返回null
     * - 返回结果的key包含fileNode所在的组件Node
     * @param graph
     * @param fileNode
     * @return
     */
    public static Map<Node, Set<Node>> groupAfferentNodesByComponentNode(Graph graph, Node fileNode) {
        if (fileNode.isComponent()) {
            return null;
        }
        // 获取fileNode所在的组件Node
        Node targetComponentNode = findBelongComponent(graph, fileNode);

        // 获取fileNode的所有传入文件Node
        List<Node> afferentNodes = findAfferentNodes(graph, fileNode);
        if (afferentNodes == null) {
            return null;
        }
        // 获取fileNode的所有传入组件Node
        Set<Node> afferentComponentNodes = new HashSet<>();
        for (Node afferentNode : afferentNodes) {
            Node belongComponent = findBelongComponent(graph, afferentNode);
            afferentComponentNodes.add(belongComponent);
        }

        // 获取所有afferentComponentNodes依赖targetComponentNode中的文件Node，并根据组件Node聚合
        Map<Node, Set<Node>> groups = new HashMap<>();
        for (Node afferentComponentNode : afferentComponentNodes) {
            List<Node> subFileNodes = findSubFileNodes(graph, afferentComponentNode);
            if (subFileNodes == null) {
                continue;
            }
            Set<Node> groupNodes = new HashSet<>();
            for (Node subFileNode : subFileNodes) {
                List<Node> efferentNodes = findEfferentNodes(graph, subFileNode);
                if (efferentNodes == null) {
                    continue;
                }
                for (Node efferentNode : efferentNodes) {
                    Node belongComponent = findBelongComponent(graph, efferentNode);
                    if (belongComponent.equals(targetComponentNode)) {
                        groupNodes.add(efferentNode);
                    }
                }
            }
            groups.put(afferentComponentNode, groupNodes);
        }

        return groups;
    }

    /**
     * 获取fileNode的传出文件Node所对应的组件Node
     * - 若fileNode为null或graph为null，则返回null
     * - 若fileNode不是文件粒度，则返回null
     * - 若无传出文件Node，则返回null
     * - 返回结果包含fileNode所在的组件Node
     * @param graph
     * @param fileNode
     * @return
     */
    public static Set<Node> findEfferentComponentNodes(Graph graph, Node fileNode) {
        if (graph == null || fileNode == null) {
            return null;
        }
        if (fileNode.isComponent()) {
            return null;
        }
        List<Node> efferentFileNodes = findEfferentNodes(graph, fileNode);
        if (efferentFileNodes == null) {
            return null;
        }
        Set<Node> efferentComponentNodes = new HashSet<>();
        for (Node efferentFileNode : efferentFileNodes) {
            Node belongComponent = findBelongComponent(graph, efferentFileNode);
            if (belongComponent == null) {
                continue;
            }
            efferentComponentNodes.add(belongComponent);
        }
        return efferentComponentNodes;
    }

    /**
     * 获取fileNode的系统内传出文件Node所对应的组件Node
     * - 若fileNode为null或graph为null，则返回null
     * - 若fileNode不是文件粒度，则返回null
     * - 若无传出文件Node，则返回null
     * - 返回结果包含fileNode所在的组件Node
     * @param graph
     * @param fileNode
     * @return
     */
    public static Set<Node> findSystemEfferentComponentNodes(Graph graph, Node fileNode) {
        Set<Node> efferentComponentNodes = findEfferentComponentNodes(graph, fileNode);
        if (efferentComponentNodes == null) {
            return null;
        }
        Set<Node> systemEfferentComponentNodes = new HashSet<>();
        for (Node efferentComponentNode : efferentComponentNodes) {
            if (efferentComponentNode.isSystem()) {
                systemEfferentComponentNodes.add(efferentComponentNode);
            }
        }
        return systemEfferentComponentNodes;
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
    public static Set<Node> findAfferentComponentNodes(Graph graph, Node fileNode) {
        if (graph == null || fileNode == null) {
            return null;
        }
        if (fileNode.isComponent()) {
            return null;
        }
        List<Node> afferentFileNodes = findAfferentNodes(graph, fileNode);
        if (afferentFileNodes == null) {
            return null;
        }
        Set<Node> afferentComponentNodes = new HashSet<>();
        for (Node afferentFileNode : afferentFileNodes) {
            Node belongComponent = findBelongComponent(graph, afferentFileNode);
            afferentComponentNodes.add(belongComponent);
        }
        return afferentComponentNodes;

    }

    /**
     * 返回一个深度clone的Graph
     * @param graph
     * @return
     */
    public static Graph cloneGraph(Graph graph) {
        List<Node> cloneFileNodes = null;
        List<Node> cloneComponentNodes = null;
        // 克隆fileNodes
        List<Node> fileNodes = graph.getFileNodes();
        if (fileNodes != null) {
            cloneFileNodes = new ArrayList<>();
            for (Node fileNode : fileNodes) {
                Node cloneFileNode = cloneNode(fileNode);
                cloneFileNodes.add(cloneFileNode);
            }
        }

        // 克隆componentNodes
        List<Node> componentNodes = graph.getComponentNodes();
        if (componentNodes != null) {
            cloneComponentNodes = new ArrayList<>();
            for (Node componentNode : componentNodes) {
                Node cloneComponentNode = cloneNode(componentNode);
                cloneComponentNodes.add(cloneComponentNode);
            }
        }
        Graph cloneGraph = new Graph(cloneFileNodes, cloneComponentNodes);
        return cloneGraph;
    }

    /**
     * 返回一个深度clone的Node
     * @param node
     * @return
     */
    private static Node cloneNode(Node node) {
        Node cloneNode = node.isComponent() ?
                node.isSystem() ?
                        buildSystemComponentNode(node.getName()) :
                        buildNonSystemComponentNode(node.getName()) :
                node.isSystem() ?
                        buildSystemFileNode(node.getName()) :
                        buildNonSystemFileNode(node.getName());
        // 克隆depend边
        List<Edge> dependEdges = node.getDependEdges();
        List<Edge> cloneDependEdges = new ArrayList<>();
        for (Edge dependEdge : dependEdges) {
            Edge cloneDependEdge = new Edge(dependEdge.getInNodeId(), dependEdge.getOutNodeId(), EdgeConstant.DEPEND);
            cloneDependEdges.add(cloneDependEdge);
        }
        cloneNode.setDependEdges(cloneDependEdges);

        // 克隆belong边
        Edge belongEdge = node.getBelongEdge();
        if (belongEdge != null) {
            Edge cloneBelongEdge = new Edge(belongEdge.getInNodeId(), belongEdge.getOutNodeId(), EdgeConstant.BELONG);
            cloneNode.setBelongEdge(cloneBelongEdge);
        }

        // 克隆afferent
        cloneNode.setAfferentNum(node.getAfferentNum());
        return cloneNode;
    }

    /**
     * 将一组Node转换为一组id
     * 若nodes为null或size=0，则返回null
     * @param nodes
     * @return
     */
    public static List<String> nodes2Ids(List<Node> nodes) {
        if (nodes == null || nodes.size() == 0) {
            return null;
        }
        List<String> ids = new ArrayList<>();
        for (Node node : nodes) {
            ids.add(node.getId());
        }
        return ids;
    }

    /**
     * 将一组Node转换为一组id
     * 若nodes为null或size=0，则返回null
     * @param nodes
     * @return
     */
    public static List<String> nodes2Ids(Set<Node> nodes) {
        if (nodes == null || nodes.size() == 0) {
            return null;
        }
        List<String> ids = new ArrayList<>();
        for (Node node : nodes) {
            ids.add(node.getId());
        }
        return ids;
    }

    /**
     * 将一组id转换为一组Node
     * 若ids为null或size=0，则返回null
     * @param graph
     * @param ids
     * @return
     */
    public static List<Node> ids2Nodes(Graph graph, List<String> ids) {
        if (ids == null || ids.size() == 0) {
            return null;
        }
        List<Node> nodes = new ArrayList<>();
        for (String id : ids) {
            nodes.add(graph.findNodeById(id));
        }
        return nodes;
    }


    public static List<Node> ids2Nodes(Graph graph, Set<String> ids) {
        if (ids == null || ids.size() == 0) {
            return null;
        }
        List<Node> nodes = new ArrayList<>();
        for (String id : ids) {
            nodes.add(graph.findNodeById(id));
        }
        return nodes;
    }

}
