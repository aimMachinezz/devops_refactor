package top.lazyr.parser;

import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.architecture.writer.ExcelModelWriter;
import top.lazyr.architecture.writer.TestConsoleModelWriter;
import top.lazyr.io.input.ExcelInput;

import java.util.List;
import java.util.Map;

/**
 * @author lazyr
 * @created 2022/1/25
 */
public class ExcelParser implements Parser {
    @Override
    public Graph parse(String path) {
        Map<String, List<List<String>>> sheets = ExcelInput.readAllFromExcel(path);
        Graph graph = new Graph();
        buildNodes(graph, sheets.get("Nodes"));
        buildDepends(graph, sheets.get("ComponentDepends"));
        buildDepends(graph, sheets.get("FileDepends"));
        buildBelong(graph, sheets.get("Belong"));
        return graph;
    }

    private void buildBelong(Graph graph, List<List<String>> belongs) {
        for (int i = 1; i < belongs.size(); i++) {
            List<String> belongInfo = belongs.get(i);
            String componentNodeId = belongInfo.get(0);
            Node componentNode = graph.findNodeById(componentNodeId);
            String fileNodeIds = belongInfo.get(1);
            String[] fileNodeIdArr = fileNodeIds.split("\n");
            if (fileNodeIdArr.length == 0) {
                continue;
            }
            for (String fileNodeId : fileNodeIdArr) {
                Node fileNode = graph.findNodeById(fileNodeId);
                graph.updateBelong(fileNode, componentNode);
            }
        }
    }

    private void buildDepends(Graph graph, List<List<String>> componentDepends) {
        for (int i = 1; i < componentDepends.size(); i++) {
            List<String> dependInfo = componentDepends.get(i);
            graph.addDepend(graph.findNodeById(dependInfo.get(0)), graph.findNodeById(dependInfo.get(1)));
        }
    }

    private void buildNodes(Graph graph, List<List<String>> infos) {
        for (int i = 1; i < infos.size(); i++) {
            List<String> nodeInfo = infos.get(i);
            Node node = new Node(nodeInfo.get(0), nodeInfo.get(1), nodeInfo.get(2));
            if (node.isComponent()) {
                graph.addComponentNode(node);
            } else {
                graph.addFileNode(node);
            }
        }
    }

    public static void main(String[] args) {
        String path = "/Users/lazyr/Work/projects/devops/test/data/refactor_demo/target/classes";
        Graph graph = new SourceCodeParser().parse(path);

        ExcelModelWriter.write(graph);
        TestConsoleModelWriter.write(graph, "a.p1 ~ COMPONENT ~ SYSTEM");

        Graph readGraph = new ExcelParser().parse("exp/源代码模型图.xlsx");
//        ConsoleModelWriter.write(readGraph);
        TestConsoleModelWriter.write(readGraph, "a.p1 ~ COMPONENT ~ SYSTEM");
    }
}
