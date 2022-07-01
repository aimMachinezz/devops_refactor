package top.lazyr.smell.detector.cyclicdependency;

import top.lazyr.architecture.diagram.Node;
import top.lazyr.constant.ConsolePrinter;

import java.util.List;

/**
 * @author lazyr
 * @created 2022/1/28
 */
public class CyclicDependencyConsoleWriter {

    public static void write(List<Node> smellNodes) {
        ConsolePrinter.printTitle("环依赖异味数: " + smellNodes.size());
        for (Node smellNode : smellNodes) {
            System.out.println(smellNode);
        }
    }
}
