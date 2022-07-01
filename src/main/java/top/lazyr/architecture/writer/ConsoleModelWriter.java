package top.lazyr.architecture.writer;

import top.lazyr.constant.ConsolePrinter;
import top.lazyr.architecture.diagram.Edge;
import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;

import java.util.List;


/**
 * @author lazyr
 * @created 2022/1/27
 */
public class ConsoleModelWriter{

    public static void write(Graph graph) {
        List<Node> fileNodes = graph.getFileNodes();
        ConsolePrinter.printTitle("FILES");
        for (Node fileNode : fileNodes) {
            System.out.println(fileNode.getId());
            List<Edge> dependEdges = fileNode.getDependEdges();
            for (Edge dependEdge : dependEdges) {
                System.out.println("\t\t" + dependEdge.getInNodeName() + "(" + graph.findNodeById(dependEdge.getInNodeId()).getFrom()  + ") ==" + dependEdge.getType() + "==>" + dependEdge.getOutNodeName()  + "(" + graph.findNodeById(dependEdge.getOutNodeId()).getFrom()  + ")");
            }
            Edge belongEdge = fileNode.getBelongEdge();
            System.out.println("\t\t" + belongEdge.getInNodeName() + "(" + graph.findNodeById(belongEdge.getInNodeId()).getFrom()  + ") ==" + belongEdge.getType() + "==> " + belongEdge.getOutNodeName()  + "(" + graph.findNodeById(belongEdge.getOutNodeId()).getFrom()  + ")");
        }

        ConsolePrinter.printTitle("COMPONENTS");
        List<Node> componentNodes = graph.getComponentNodes();
        for (Node componentNode : componentNodes) {
            System.out.println(componentNode.getId());
            List<Edge> dependEdges = componentNode.getDependEdges();
            for (Edge dependEdge : dependEdges) {
                System.out.println("\t\t" + dependEdge.getInNodeName() + "(" + graph.findNodeById(dependEdge.getInNodeId()).getFrom()  + ") ==" + dependEdge.getType() + "==> " + dependEdge.getOutNodeName()  + "(" + graph.findNodeById(dependEdge.getOutNodeId()).getFrom()  + ")");
            }
        }
    }
}
