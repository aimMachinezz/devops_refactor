package top.lazyr.architecture.diagram;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lazyr.constant.NodeConstant;

import java.util.Objects;

/**
 * @author lazyr
 * @created 2022/1/27
 */
@Data
public class Edge {
    private static Logger logger = LoggerFactory.getLogger(Edge.class);
    /**
     * 入节点Id
     * 格式: [name][SEPARATOR][level][SEPARATOR][from]
     */
    private String inNodeId;
    /**
     * 出节点Id
     * 格式: [name][SEPARATOR][level][SEPARATOR][from]
     */
    private String outNodeId;
    /* 入节点Name */
    private String inNodeName;
    /* 出节点Name */
    private String outNodeName;
    /* 边的类型: DEPEND; BELONG; */
    private String type;

    public Edge(Node inNode, Node outNode, String type) {
        this.inNodeId = inNode.getId();
        this.outNodeId = outNode.getId();
        this.inNodeName = inNode.getName();
        this.outNodeName = outNode.getName();
        this.type = type;
    }

    public Edge(String inNodeId, String outNodeId, String type) {
        this.inNodeId = inNodeId;
        this.outNodeId = outNodeId;
        this.inNodeName = inNodeId.split(NodeConstant.SEPARATOR)[0];
        this.outNodeName = outNodeId.split(NodeConstant.SEPARATOR)[0];
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return inNodeId.equals(edge.inNodeId) && outNodeId.equals(edge.outNodeId) && type.equals(edge.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inNodeId, outNodeId, type);
    }

    @Override
    public String toString() {
        return inNodeId + " ==" + type + "==>" + outNodeId;
    }

}
