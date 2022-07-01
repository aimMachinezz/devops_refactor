package top.lazyr;

import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.architecture.manager.GraphManager;
import top.lazyr.constant.ConsolePrinter;
import top.lazyr.parser.Parser;
import top.lazyr.parser.SourceCodeParser;
import top.lazyr.smell.detector.cyclicdependency.CyclicDependencyDetector;
import top.lazyr.smell.detector.hublikedependency.HubLikeDependencyDetector;
import top.lazyr.smell.detector.unstabledependency.UnstableDependencyDetector;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lazyr
 * @created 2022/2/12
 */
public class Info {
    public static void main(String[] args) {
        // 重构项目绝对路径
        String[] paths = new String[]{
//                "/Users/lazyr/Work/projects/devops/test/data/refactor_demo/target/classes",
                "/Users/lazyr/Work/graduation/实验数据/expProjects/pmd/net",
                "/Users/lazyr/Work/graduation/实验数据/expProjects/findbugs/edu",
                "/Users/lazyr/Work/graduation/实验数据/expProjects/jpox/org",
                "/Users/lazyr/Work/graduation/实验数据/expProjects/jfreechart/org",
                "/Users/lazyr/Work/graduation/实验数据/expProjects/itextpdf/com",
                "/Users/lazyr/Work/graduation/实验数据/expProjects/mockito/org/",
        };
        for (String path : paths) {
            showInfo(path, extractName(path));
        }
    }

    private static String extractName(String path) {
        String[] split = path.split("/");
        return split[split.length - 2];
    }

    /**
     * 显示path内项目的详细信息
     * @param path
     */
    public static void showInfo(String path, String projectName) {

        // 1、构建源代码模型图并输出到 "resources/exp/源代码模型图.xlsx"
        Parser parser = new SourceCodeParser();
        Graph originGraph = parser.parse(path);
        // 输出源代码模型图信息到 "exp2/源代码模型图.xlsx"


        // 2、异味检测并输出信息

        int smellNum = 0; // 记录异味个数
        int smellFileNum = 0;
        Set<Node> smellComponentNodes = new HashSet<>(); // 记录异味组件Node
        // 输出不稳定依赖异味信息到 "exp2/不稳定依赖异味.xlsx"
        Map<Node, List<Node>> udSmellInfo = UnstableDependencyDetector.detect(originGraph);

        smellNum += udSmellInfo.size();
        smellComponentNodes.addAll(udSmellInfo.keySet());


        // 输出枢纽型异味信息到 "exp2/枢纽型依赖异味.xlsx"
        Map<Node, List<Integer>> hlSmellInfo = HubLikeDependencyDetector.detect(originGraph);

        smellNum += hlSmellInfo.size();
        smellComponentNodes.addAll(hlSmellInfo.keySet());

        // 输出环依赖异味信息到 "exp2/环依赖异味.xlsx"
        List<Node> cdSmellInfo = CyclicDependencyDetector.detect(originGraph);

        smellNum += cdSmellInfo.size();
        smellComponentNodes.addAll(cdSmellInfo);
        smellFileNum = findSubFiles(originGraph, smellComponentNodes);

//        if (!(udSmellInfo.size() >= 2 &&
//            hlSmellInfo.size() >= 2 &&
//                cdSmellInfo.size() >= 2 &&
//                smellNum > 15 &&
//                originGraph.filterSystemFileNodes().size() < 1100)
//        ) {
//            ConsoleConstant.printTitle(projectName + "不符合要求");
//            return;
//        }

        ConsolePrinter.printTitle(projectName + "基础信息");
//        NumConsoleModelWriter.write(originGraph);
        System.out.println("组件个数, 异味组件个数, 文件个数, 异味文件个数, 异味个数");

        System.out.print(originGraph.filterSystemComponentNodes().size() + ",");
        System.out.print(smellComponentNodes.size() + ",");
        System.out.print(originGraph.filterSystemFileNodes().size() + ",");
        System.out.print(smellFileNum + ",");
        System.out.println(smellNum);


//        ConsoleModelWriter.write(originGraph);
    }
    /**
     * 获取所有componentNodes中的文件Node个数
     * @param componentNodes
     * @return
     */
    private static int findSubFiles(Graph graph, Set<Node> componentNodes) {
        int subFileNum  = 0;
        for (Node componentNode : componentNodes) {
            List<Node> subFileNodes = GraphManager.findSubFileNodes(graph, componentNode);
            subFileNum += subFileNodes == null ? 0 : subFileNodes.size();
        }
        return subFileNum;
    }
}
