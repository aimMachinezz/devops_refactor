package top.lazyr.architecture.writer;

import top.lazyr.architecture.diagram.Edge;
import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.architecture.manager.GraphManager;
import top.lazyr.io.output.ExcelOutPut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/1/27
 */
public class ExcelModelWriter{

    public static void write(Graph graph) {
        write(graph, "exp/源代码模型图.xlsx");
    }


    public static void write(Graph graph, String fileName) {
        writeNodeInfo(graph, fileName);
        writeComponentDepend(graph, fileName);
        writeFileDepend(graph, fileName);
        writeBelong(graph, fileName);
    }

    private static void writeNodeInfo(Graph graph, String fileName) {
        List<List<String>> infos = new ArrayList<>();
        infos.add(Arrays.asList("name", "level", "from"));
        List<Node> fileNodes = graph.getFileNodes();
        for (Node fileNode : fileNodes) {
            List<String> info = new ArrayList<>();
            info.add(fileNode.getName());
            info.add(fileNode.getLevel());
            info.add(fileNode.getFrom());
            infos.add(info);
        }
        List<Node> componentNodes = graph.getComponentNodes();
        for (Node componentNode : componentNodes) {
            List<String> info = new ArrayList<>();
            info.add(componentNode.getName());
            info.add(componentNode.getLevel());
            info.add(componentNode.getFrom());
            infos.add(info);
        }
        ExcelOutPut.write2Excel(fileName, "Nodes", infos);
    }

    private static void writeBelong(Graph graph, String fileName) {
        List<List<String>> infos = new ArrayList<>();
        infos.add(Arrays.asList("component", "files"));
        List<Node> componentNodes = graph.getComponentNodes();
        for (Node componentNode : componentNodes) {
            List<Node> subNodes = GraphManager.findSubFileNodes(graph, componentNode);
            List<String> info = new ArrayList<>();
            info.add(componentNode.getId());
            StringBuilder subNodeBuilder = new StringBuilder();
            if (subNodes != null) {
                for (int i = 0; i < subNodes.size(); i++) {
                    subNodeBuilder.append(subNodes.get(i).getId());
                    if (i != subNodes.size() - 1) {
                        subNodeBuilder.append("\n");
                    }
                }
            }
            info.add(subNodeBuilder.toString());
            infos.add(info);
        }
        ExcelOutPut.append2Excel(fileName, "Belong", infos);
    }

    private static void writeFileDepend(Graph graph, String fileName) {
        List<List<String>> infos = new ArrayList<>();
        infos.add(Arrays.asList("sourceFile", "dependFile", "value"));
        for (Node fileNode : graph.getFileNodes()) {
            String sourceFileId = fileNode.getId();
            for (Edge dependEdge : fileNode.getDependEdges()) {
                List<String> info = new ArrayList<>();
                info.add(sourceFileId);
                info.add(dependEdge.getOutNodeId());
                info.add("1");
                infos.add(info);
            }
        }
        ExcelOutPut.append2Excel(fileName, "FileDepends", infos);
    }

    private static void writeComponentDepend(Graph graph, String fileName) {
        List<List<String>> infos = new ArrayList<>();
        infos.add(Arrays.asList("sourceComponent", "dependComponent", "value"));
        for (Node componentNode : graph.getComponentNodes()) {
            String sourceComponentId = componentNode.getId();
            for (Edge dependEdge : componentNode.getDependEdges()) {
                List<String> info = new ArrayList<>();
                info.add(sourceComponentId);
                info.add(dependEdge.getOutNodeId());
                info.add("1");
                infos.add(info);
            }
        }
        ExcelOutPut.append2Excel(fileName, "ComponentDepends", infos);
    }
}
