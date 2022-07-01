package top.lazyr.ui.soultionframe;


import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.manager.GraphManager;
import top.lazyr.architecture.writer.TestConsoleModelWriter;
import top.lazyr.constant.ConsolePrinter;
import top.lazyr.parser.Parser;
import top.lazyr.parser.SourceCodeParser;
import top.lazyr.refactor.actuator.RefactorActuator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/2/2
 */
public class JTreeTest {
    public static void main(String[] args) {
        String path = "/Users/lazyr/Work/projects/devops/test/data/refactor_demo/target/classes";
//        String path = "/Users/lazyr//Work/graduation/arcan/data/jedit/org";
        Parser parser = new SourceCodeParser();
        Graph graph = parser.parse(path);

        List<String> refactors = new ArrayList<>();

        refactors.add("moveFile,a.p3 ~ COMPONENT ~ SYSTEM,a.p3.C32 ~ FILE ~ SYSTEM,a.p2 ~ COMPONENT ~ SYSTEM");
        refactors.add("moveFile,a.p3 ~ COMPONENT ~ SYSTEM,a.p3.C33 ~ FILE ~ SYSTEM,a.p1 ~ COMPONENT ~ SYSTEM");
        refactors.add("moveFile,a.p2 ~ COMPONENT ~ SYSTEM,a.p2.C22 ~ FILE ~ SYSTEM,a.p3 ~ COMPONENT ~ SYSTEM");
        refactors.add("moveFile,a.p3 ~ COMPONENT ~ SYSTEM,a.p3.C33 ~ FILE ~ SYSTEM,a.p1 ~ COMPONENT ~ SYSTEM");
        refactors.add("extractComponent,a.p2 ~ COMPONENT ~ SYSTEM>a.p2.C21 ~ FILE ~ SYSTEM;a.p2 ~ COMPONENT ~ SYSTEM>a.p2.C23 ~ FILE ~ SYSTEM,a.from_p2 ~ COMPONENT ~ SYSTEM");
        Graph refactoredGraph = GraphManager.cloneGraph(graph);
        int[] refactor = new RefactorActuator().refactor(refactoredGraph, refactors);

        ConsolePrinter.printTitle("成功: " + refactor[0]);
        ConsolePrinter.printTitle("失败: " + refactor[1]);

        TestConsoleModelWriter.write(refactoredGraph, "a.from_p2 ~ COMPONENT ~ SYSTEM");
//        ConsoleModelWriter.write(refactoredGraph);
        new SolutionPanel(graph, refactoredGraph, refactors);
    }
}
