package top.lazyr.architecture.diagram;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lazyr.constant.EdgeConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lazyr
 * @created 2022/1/27
 */
@Data
public class Graph {
    private static Logger logger = LoggerFactory.getLogger(Graph.class);
    /* 保存软件架构图中的所有文件节点 */
    private List<Node> fileNodes;
    /* 保存软件架构图中的所有组件节点 */
    private List<Node> componentNodes;
    /**
     * 保存软件架构图中的所有节点和其id的映射关系
     * { [id] : [Node]}
     */
    private Map<String, Node> nodeMap;

    public Graph() {
        this.fileNodes = new ArrayList<>();
        this.componentNodes = new ArrayList<>();
        this.nodeMap = new HashMap<>();
    }

    public Graph(List<Node> fileNodes, List<Node> componentNodes) {
        if (fileNodes == null || componentNodes == null) {
            logger.warn("fileNodes or componentNodes is null.");
            return;
        }
        this.fileNodes = fileNodes;
        this.componentNodes = componentNodes;
        this.nodeMap = new HashMap<>();
        for (Node fileNode : fileNodes) {
            nodeMap.put(fileNode.getId(), fileNode);
        }
        for (Node componentNode : componentNodes) {
            nodeMap.put(componentNode.getId(), componentNode);
        }
    }

    /**
     * 往componentNodes添加componentNode，若componentNode已存在，则不添加
     * - 返回false，表示componentNode已存在或componentNode为null，未添加到graph中
     * - 返回true，表示componentNode成功添加到graph中
     * @param componentNode
     */
    public boolean addComponentNode(Node componentNode) {
        if (componentNode == null) {
            return false;
        }
        if (nodeMap.containsKey(componentNode.getId())) { // 若已存在，则拒绝添加
            return false;
        }
        nodeMap.put(componentNode.getId(), componentNode);
        return componentNodes.add(componentNode);
    }

    /**
     * 往fileNodes添加fileNode,若fileNode已存在，则不添加
     * - 返回false，表示fileNode已存在或fileNode为null，未添加到graph中
     * - 返回true，表示fileNode成功添加到graph中
     * @param fileNode
     */
    public boolean addFileNode(Node fileNode) {
        if (fileNode == null) {
            return false;
        }
        if (nodeMap.containsKey(fileNode.getId())) { // 若已存在，则拒绝添加
            return false;
        }
        nodeMap.put(fileNode.getId(), fileNode);
        return fileNodes.add(fileNode);
    }

    /**
     * 删除fileNodes中fileNode
     * 删除nodeMap中value为fileNode的键值对
     * - 返回false，表示fileNode为null或不存在
     * - 返回true，表示fileNode存在并删除
     * @param fileNode
     */
    public boolean removeFileNode(Node fileNode) {
        if (fileNode == null) {
            return false;
        }
        nodeMap.remove(fileNode.getId());
        return fileNodes.remove(fileNode);
    }


    /**
     * 删除fileNodes中id为fileNodeId的Node
     * 删除nodeMap中key为fileNodeId的键值对
     * - 返回false，表示fileNodeId为null或不存在
     * - 返回true，表示fileNodeId存在并删除
     * @param fileNodeId
     */
    public boolean removeFileNode(String fileNodeId) {
        Node fileNode = nodeMap.get(fileNodeId);
        return removeFileNode(fileNode);
    }


    /**
     * 删除componentNodes中componentNode
     * 删除nodeMap中value为componentNode的键值对
     * - 返回false，表示componentNode为null或不存在
     * - 返回true，表示componentNode存在并删除
     * @param componentNode
     */
    public boolean removeComponentNode(Node componentNode) {
        if (componentNode == null) {
            return false;
        }
        nodeMap.remove(componentNode.getId());
        return componentNodes.remove(componentNode);
    }

    /**
     * 删除componentNodes中id为componentNodeId的Node
     * 删除nodeMap中key为componentNodeId的键值对
     * - 返回false，表示componentNode为null或不存在
     * - 返回true，表示componentNode存在并删除
     * @param componentNodeId
     */
    public boolean removeComponentNode(String componentNodeId) {
        Node componentNode = nodeMap.get(componentNodeId);
        return removeComponentNode(componentNode);
    }

    /**
     * 若inNode和outNode为不同粒度，则不进行操作
     * 在inNode中添加一条depend边，指向outNode
     * 设置outNode中的afferentNum加一
     * - 返回false，表示未添加
     * - 返回true，表示已添加
     * @param inNode
     * @param outNode
     */
    public boolean addDepend(Node inNode, Node outNode) {
        if (inNode == null || outNode == null) {
            return false;
        }
        if (inNode.isComponent() != outNode.isComponent()) { // 依赖关系只发生在同一粒度的节点中
            return false;
        }
        if (inNode.equals(outNode)) { // 不添加自己依赖自己的Node
            return false;
        }

        Edge dependEdge = new Edge(inNode, outNode, EdgeConstant.DEPEND);
        boolean succeed = inNode.addDependEdge(dependEdge);
        if (succeed) { // 若添加成功
            outNode.increaseAfferent();
        }
        return succeed;
    }

    /**
     * 若inNode和outNode为不同粒度，则不进行操作
     * 删除inNode中的一条depend边
     * 设置outNode中的afferentNum减一
     * - 返回false，表示不存在该边，无法删除
     * - 返回true，表示存在该边，已删除
     * @param inNode
     * @param outNode
     */
    public boolean removeDepend(Node inNode, Node outNode) {
        if (inNode == null || outNode == null) {
            return false;
        }
        if (inNode.isComponent() != outNode.isComponent()) { // 依赖关系只发生在同一粒度的节点中
            return false;
        }
        Edge dependEdge = new Edge(inNode, outNode, EdgeConstant.DEPEND);
        boolean succeed = inNode.removeDependEdge(dependEdge);
        if (succeed) { // 若删除边存在
            outNode.decayAfferent();
        }
        return succeed;
    }

    /**
     * 若fileNode不是文件粒度或componentNode不是组件粒度，则拒绝操作
     * 在fileNode中添加一个belong边，指向componentNode
     * @param fileNode
     * @param componentNode
     */
    public void updateBelong(Node fileNode, Node componentNode) {
        if (fileNode == null || componentNode == null) {
            return;
        }
        if (fileNode.isComponent() || !componentNode.isComponent()) {
            return;
        }
        Edge belong = new Edge(fileNode, componentNode, EdgeConstant.BELONG);
        fileNode.setBelongEdge(belong);
    }

    /**
     * 返回所有项目内的组件节点
     * - 若结果为空，则返回null
     * @return
     */
    public List<Node> filterSystemComponentNodes() {
        return filterNodes(true, true);
    }

    /**
     * 返回所有项目外的组件节点
     * - 若结果为空，则返回null
     * @return
     */
    public List<Node> filterNonSystemComponentNodes() {
        return filterNodes(true, false);
    }

    /**
     * 返回所有项目内的文件节点
     * - 若结果为空，则返回null
     * @return
     */
    public List<Node> filterSystemFileNodes() {
        return filterNodes(false, true);
    }

    /**
     * 返回所有项目外的文件节点
     * - 若结果为空，则返回null
     * @return
     */
    public List<Node> filterNonSystemFileNodes() {
        return filterNodes(false, false);
    }

    /**
     * 返回所有文件的depend边
     * @return
     */
    public List<Edge> filterAllFileDependEdges() {
        return filterEdges(false, true);
    }

    /**
     * 返回所有文件belong边
     * @return
     */
    public List<Edge> filterAllFileBelongEdges() {
        return filterEdges(false, false);
    }

    /**
     * 返回所有组件depend边
     * @return
     */
    public List<Edge> filterAllComponentDependEdges() {
        return filterEdges(true, true);
    }

    /**
     * 返回符合条件的Edge集合
     * 若无符合条件的Edge，则返回null
     * @param isComponent
     * @param isDepend
     * @return
     */
    private List<Edge> filterEdges(boolean isComponent, boolean isDepend) {
        List<Node> nodes = isComponent ? componentNodes : fileNodes;
        List<Edge> filterEdges = new ArrayList<>();
        for (Node node : nodes) {
            if (isDepend) {
                filterEdges.addAll(node.getDependEdges());
            } else if (node.getBelongEdge() != null) {
                filterEdges.add(node.getBelongEdge());
            }
        }
        return filterEdges.size() == 0 ? null : filterEdges;
    }

    /**
     * 获取组件间依赖边的出节点和入节点都为项目内节点的边集合
     * 若过滤后无相关节点，则返回null
     * @return
     */
    public List<Edge> filterAllComponentSystemEdges() {
        List<Node> nodes = componentNodes;
        List<Edge> filterEdges = new ArrayList<>();
        for (Node node : nodes) {
            if (!node.isSystem()) {
                continue;
            }
            List<Edge> dependEdges = node.getDependEdges();
            if (dependEdges == null) {
                continue;
            }
            for (Edge dependEdge : dependEdges) {
                if (!findNodeById(dependEdge.getOutNodeId()).isSystem()) {
                    continue;
                }
                filterEdges.add(dependEdge);
            }
        }
        return filterEdges.size() == 0 ? null : filterEdges;
    }


    /**
     * 返回符合条件的Node集合
     * 若无符合条件的Node，则返回null
     * @param isComponent
     * @param isSystem
     * @return
     */
    private List<Node> filterNodes(boolean isComponent, boolean isSystem) {
        List<Node> nodes = isComponent ? componentNodes : fileNodes;
        List<Node> filterNodes = new ArrayList<>();
        for (Node node : nodes) {
            if (node.isSystem() == isSystem) {
                filterNodes.add(node);
            }
        }
        return filterNodes.size() == 0 ? null : filterNodes;
    }

    /**
     * 查找id为nodeId的Node
     * - 若未查到则返回null
     * @param nodeId
     * @return
     */
    public Node findNodeById(String nodeId) {
        return nodeMap.get(nodeId);
    }

}
