package top.lazyr.architecture.diagram;

import lombok.Data;
import top.lazyr.constant.NodeConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author lazyr
 * @created 2022/1/27
 */
@Data
public class Node {
    /* 节点的名字 */
    private String name;
    /* 节点粒度: COMPONENT; FILE */
    private String level;
    /* 节点的来源: SYSTEM; NON_SYSTEM */
    private String from;
    /* 节点唯一标识符: [name][SEPARATOR][level][SEPARATOR][from] */
    private String id;
    /* 依赖关系边，若无传出Node，则为size=0的list */
    private List<Edge> dependEdges;
    /* 从属边，若无从属的Node，则为null */
    private Edge belongEdge;
    /* 传入依赖个数 */
    private int afferentNum;

    public Node(String name, String level, String from) {
        this.name = name;
        this.level = level;
        this.from = from;
        this.id = name + NodeConstant.SEPARATOR + level + NodeConstant.SEPARATOR + from;
        this.dependEdges = new ArrayList<>();
    }

    /**
     * 添加dependEdge到dependEdges中
     * 若dependEdge已存在，则不添加
     * - 返回false，表示未添加
     * - 返回true，表示已添加
     * @param dependEdge
     */
    public boolean addDependEdge(Edge dependEdge) {
        for (Edge existedEdge : dependEdges) {
            if (existedEdge.equals(dependEdge)) {
                return false;
            }
        }
        return dependEdges.add(dependEdge);
    }

    /**
     * 删除dependEdges中的dependEdge
     * - 返回false，表示不存在该边，无法删除
     * - 返回true，表示存在该边，已删除
     * @param dependEdge
     */
    public boolean removeDependEdge(Edge dependEdge) {
        return dependEdges.remove(dependEdge);
    }

    /**
     * 获取节点的传出依赖数
     * @return
     */
    public int getEfferentNum() {
        return dependEdges.size();
    }

    /**
     * 是否为系统内的节点
     * @return
     */
    public boolean isSystem() {
        return from.equals(NodeConstant.FROM_SYSTEM);
    }

    /**
     * 是否为组件粒度的节点
     * @return
     */
    public boolean isComponent() {
        return level.equals(NodeConstant.LEVEL_COMPONENT);
    }


    public void increaseAfferent() {
        this.afferentNum++;
    }

    public void decayAfferent() {
        this.afferentNum--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return name.equals(node.name) && level.equals(node.level) && from.equals(node.from);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, level, from);
    }

    @Override
    public String toString() {
        return id;
    }
}
