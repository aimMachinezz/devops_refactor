package top.lazyr.smell.detector.cyclicdependency;


import top.lazyr.architecture.diagram.Node;
import top.lazyr.io.output.ExcelOutPut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/1/28
 */
public class CyclicDependencyExcelWriter {
    public static void write(List<Node> smellNodes, String workBookName) {
        List<List<String>> infos = new ArrayList<>();
        infos.add(Arrays.asList("环依赖节点"));
        for (Node smellNode : smellNodes) {
            List<String> info = new ArrayList<>();
            info.add(smellNode.getName());
            infos.add(info);
        }
        ExcelOutPut.write2Excel(workBookName, "info", infos);
    }
}
