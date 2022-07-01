package top.lazyr.architecture.writer;

import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;

import java.util.List;

/**
 * @author lazyr
 * @created 2022/1/27
 */
public class NumConsoleModelWriter{

    public static void write(Graph graph) {
        String projectInfo = getProjectInfo(graph);
        System.out.println(projectInfo);
    }

    private static String getProjectInfo(Graph graph) {
        List<Node> systemFileNodes = graph.filterSystemFileNodes();
        List<Node> nonSystemFileNodes = graph.filterNonSystemFileNodes();
        List<Node> systemComponentNodes = graph.filterSystemComponentNodes();
        List<Node> nonSystemComponentNodes = graph.filterNonSystemComponentNodes();
        StringBuilder projectInfo = new StringBuilder();
        projectInfo.append("项目内的文件个数 : " + (systemFileNodes == null ? 0 : systemFileNodes.size()) + "\n");
        projectInfo.append("项目外的文件个数 : " + (nonSystemFileNodes == null ? 0 : nonSystemFileNodes.size()) + "\n");
        projectInfo.append("所有文件个数     : " + (graph.getFileNodes() == null ? 0 : graph.getFileNodes().size()) + "\n");
        projectInfo.append("项目内的组件个数 : " + (systemComponentNodes == null ? 0 : systemComponentNodes.size()) + "\n");
        projectInfo.append("项目外的组件个数 : " + (nonSystemComponentNodes == null ? 0 : nonSystemComponentNodes.size()) + "\n");
        projectInfo.append("所有组件个数    : " + (graph.getComponentNodes() == null ? 0 : graph.getComponentNodes().size()));
        return projectInfo.toString();
    }
}
