package top.lazyr.refactor.actuator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lazyr.architecture.diagram.Edge;
import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.architecture.manager.GraphManager;
import top.lazyr.constant.NodeConstant;
import top.lazyr.constant.RefactorConstant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lazyr
 * @created 2022/1/28
 */
public class RefactorActuator {
    private static Logger logger = LoggerFactory.getLogger(RefactorActuator.class);


    /**
     * moveClasses,classA;classB;classC,packageName
     * extractPackage,classA;classB;classC,from_PackageName
     * 根据refactor语句对graph进行重构
     * - 返回refactor语句中对要重构文件的行重构操作成功的个数和操作失败的个数，[succeedNum, failedNum]
     * @param graph
     * @param refactor
     */
    public static int[] refactor(Graph graph, String refactor) {
        int[] succeedFailed = new int[2];
//        if (!refactorValidator(refactor)) {
//            logger.error("refactor string({}) is wrong.", refactor);
//            return new int[]{0, 0};
//        }

        String[] actions = refactor.split(RefactorConstant.ACTION_SEPARATOR);
        switch (actions[0]) {
            case RefactorConstant.MOVE_FILE:
                succeedFailed = moveFile(graph, refactor);
                break;
            case RefactorConstant.EXTRACT_COMPONENT:
                succeedFailed = extractComponent(graph, refactor);
                break;
            case RefactorConstant.MOVE_FILES:
                succeedFailed = moveFiles(graph, refactor);
                break;
        }
        return succeedFailed;
    }

    /**
     * 根据一组refactors语句按顺序对graph进行重构
     * - 返回一组refactors语句中对要重构类的行重构操作成功的个数和操作失败的个数之和，[succeedNum, failedNum]
     * 返回对重构文件的执行重构操作成功的个数和操作失败的个数
     * [succeedNum, failedNum]
     * @param graph
     * @param refactors
     */
    public static int[] refactor(Graph graph, List<String> refactors) {
        int succeedNum = 0;
        int failedNum = 0;
        if (graph == null || refactors == null) {
            logger.warn("the params is empty.");
            return new int[]{0, 0};
        }
        for (String refactor : refactors) {
            int[] tempSucceedFailed = refactor(graph, refactor);
            succeedNum += tempSucceedFailed[0];
            failedNum += tempSucceedFailed[1];
        }

        return new int[]{succeedNum, failedNum};
    }

    /**
     * 验证重构操作字符串是否符合格式，合法重构字符串字符串
     * - moveFile,sourceComponentNodeId,fileNodeId,targetComponentNodeId
     * - extractComponent,sourceComponentNodeId,fileNodeId1;fileNodeId2;fileNodeId3,from_sourceComponentNodeId
     * @param refactor
     * @return
     */
    private static boolean refactorValidator(String refactor) {
        String[] actions = refactor.split(RefactorConstant.ACTION_SEPARATOR);
        return  actions.length == 4 || actions.length == 3;
    }


    /**
     * moveFile原子操作
     * 根据moveFile字符串(moveFile,sourceComponentNodeId,fileNodeId,targetComponentNodeId)进行操作
     * 返回成功移动文件的个数和失败移动文件的个数
     * [succeedNum, failedNum]
     * @param graph
     * @param moveFile
     * @return
     */
    public static int[] moveFile(Graph graph, String moveFile) {
        // 记录执行成功和失败的个数
        String[] actions = moveFile.split(RefactorConstant.ACTION_SEPARATOR);
        if (!actions[0].equals(RefactorConstant.MOVE_FILE)) {
            return new int[]{0, 0};
        }
        String sourceComponentNodeId = actions[1];
        String fileNodeId = actions[2];
        String targetComponentNodeId = actions[3];

        Node sourceComponentNode = graph.findNodeById(sourceComponentNodeId);
        Node fileNode = graph.findNodeById(fileNodeId);
        Node targetComponentNode = graph.findNodeById(targetComponentNodeId);
        if (!actionValidator(fileNode, sourceComponentNode, targetComponentNode)) { // 若sourceComponentNode中无fileNode，则认为操作失败
            return new int[]{0, 1};
        }

        moveFile(graph, sourceComponentNode, fileNode, targetComponentNode);
        return new int[]{1, 0};
    }

    /**
     * moveFiles原则操作
     * 根据moveFile字符串(moveFiles,fileNodeId1>sourceComponentNodeId1>targetComponentNodeId1;fileNodeId2>sourceComponentNodeId2>targetComponentNodeId2)
     * 返回成功移动文件的个数和失败移动文件的个数
     * @param graph
     * @param moveFiles
     * @return
     */
    public static int[] moveFiles(Graph graph, String moveFiles) {
        // 记录执行成功和失败的个数
        String[] actions = moveFiles.split(RefactorConstant.ACTION_SEPARATOR);
        int succeed = 0;
        int failed = 0;
        if (!actions[0].equals(RefactorConstant.MOVE_FILES)) {
            return new int[]{0, 0};
        }
        String contents = actions[1];
        String[] moveFileInfos = contents.split(RefactorConstant.CONTENT_SEPARATOR);
        for (String moveFileInfo : moveFileInfos) {
            String[] fileSourceTarget = moveFileInfo.split(RefactorConstant.BELONG_SEPARATOR);
            String fileNodeId = fileSourceTarget[0];
            String sourceComponentNodeId = fileSourceTarget[1];
            String targetComponentNodeId = fileSourceTarget[2];
            Node sourceComponentNode = graph.findNodeById(sourceComponentNodeId);
            Node fileNode = graph.findNodeById(fileNodeId);
            Node targetComponentNode = graph.findNodeById(targetComponentNodeId);
            if (!actionValidator(fileNode, sourceComponentNode, targetComponentNode)) { // 若sourceComponentNode中无fileNode，则认为操作失败
                failed++;
                continue;
            }
            moveFile(graph, sourceComponentNode, fileNode, targetComponentNode);
            succeed++;
        }
        return new int[]{succeed, failed};
    }


    /**
     * extractComponent原子操作
     * 根据extractComponent字符串(extractComponent,sourceComponentNodeId1>fileNodeId1;sourceComponentNodeId2>fileNodeId2,from_sourceComponentNodeId)进行操作
     * 返回成功移动文件的个数和失败移动文件的个数
     * [succeedNum, failedNum]
     * @param graph
     * @param extractComponent
     * @return
     */
    public static int[] extractComponent(Graph graph, String extractComponent) {
        int succeed = 0;
        int failed = 0;

        String[] actions = extractComponent.split(RefactorConstant.ACTION_SEPARATOR);
        if (!actions[0].equals(RefactorConstant.EXTRACT_COMPONENT)) { // 若重构操作不是抽取组件，则不进行后续操作
            return new int[]{0, 0};
        }

        String[] fileComponentIds = actions[1].split(RefactorConstant.CONTENT_SEPARATOR);
        String targetComponentNodeId = actions[2];

        Map<Node, Node> fileComponentNodes = new HashMap<>();
        for (String fileComponentId : fileComponentIds) {
            String[] componentFile = fileComponentId.split(RefactorConstant.BELONG_SEPARATOR);
            String fileId = componentFile[0];
            String componentId = componentFile[1];
            Node componentNode = graph.findNodeById(componentId);
            Node fileNode = graph.findNodeById(fileId);
            if (componentNode == null || fileNode == null) { // 若组件或文件有一个不存在，则认为失败次数加一
                failed++;
                System.out.println("");
                continue;
            }
            fileComponentNodes.put(fileNode, componentNode);
        }


        Node targetComponentNode = graph.findNodeById(targetComponentNodeId);
        if (targetComponentNode == null) { // targetComponentNode未创建, 创建并添加到graph中
            targetComponentNode = GraphManager.buildSystemComponentNode(targetComponentNodeId.split(NodeConstant.SEPARATOR)[0]);
            graph.addComponentNode(targetComponentNode);
        }

        for (Node fileNode : fileComponentNodes.keySet()) {
            Node sourceComponentNode = fileComponentNodes.get(fileNode);

            if (!actionValidator(fileNode, sourceComponentNode, targetComponentNode)) {
                failed++;
                continue;
            }
            moveFile(graph, sourceComponentNode, fileNode, targetComponentNode);
            succeed++;
        }

        return new int[]{succeed, failed};
    }

    /**
     * 验证重构操作是否合法
     * - 若sourceComponentNode中已不存在fileNode，则认为该重构操作无效
     * - 若sourceComponentNode和targetComponentNode相等，则认为该重构操作无效
     * @param fileNode
     * @param sourceComponentNode
     * @return
     */
    private static boolean actionValidator(Node fileNode, Node sourceComponentNode, Node targetComponentNode) {
        if (fileNode == null || sourceComponentNode == null) {
            return false;
        }
        if (fileNode.isComponent() || !sourceComponentNode.isComponent() || !targetComponentNode.isComponent()) {
            return false;
        }

        if (sourceComponentNode.getId().equals(targetComponentNode.getId())) {
            return false;
        }


        Edge belongEdge = fileNode.getBelongEdge();
        if (belongEdge == null) {
            logger.error("file node({}) does not contain belong edge.", fileNode);
            return false;
        }


        return belongEdge.getOutNodeId().equals(sourceComponentNode.getId());
    }

    /**
     * 将graph中 fileNode 从 sourceComponentNode 移动到 targetComponentNode
     * - 获取所有依赖fileNode的文件Node对应的组件，
     *      - 若这些组件只依赖sourceComponentNode中fileNode，则删除依赖sourceComponentNode
     *      - 将这些组件添加依赖targetComponentNode
     * - 在targetComponentNode中添加fileNode依赖的边
     * - 修改fileNode的belong边指向targetComponentNode
     * - 删除 sourceComponentNode 中只有 fileNode 唯一依赖的组件的边
     * - TODO: 若删除完fileNode，componentNode无子类，是否需要删除？
     * @param graph
     * @param targetComponentNode
     * @param fileNode
     */
    private static void moveFile(Graph graph, Node sourceComponentNode, Node fileNode, Node targetComponentNode) {
        if (sourceComponentNode == null || graph == null || fileNode == null || targetComponentNode == null) {
            return;
        }

        // 1、获取所有依赖fileNode的文件Node对应的组件
        Map<Node, Set<Node>> groups = GraphManager.groupAfferentNodesByComponentNode(graph, fileNode);
        if (groups != null) {
            for (Node afferentComponentNode : groups.keySet()) {
                // 1.1 若这些组件只依赖sourceComponentNode中fileNode，则删除依赖sourceComponentNode
                Set<Node> fileNodes = groups.get(afferentComponentNode);
                if (fileNodes.size() == 1) {
                    graph.removeDepend(afferentComponentNode, sourceComponentNode);
                }
                // 1.2 将这些组件添加依赖targetComponentNode
                graph.addDepend(afferentComponentNode, targetComponentNode);
            }
        }


        // 2、在targetComponentNode中添加fileNode依赖的组件
        Set<Node> efferentComponentNodes = GraphManager.findEfferentComponentNodes(graph, fileNode);
        if (efferentComponentNodes != null) {
            for (Node efferentComponentNode : efferentComponentNodes) {
                graph.addDepend(targetComponentNode, efferentComponentNode);
            }
        }

        // 3、修改 fileNode 中belong边指向 targetComponentNode
        graph.updateBelong(fileNode, targetComponentNode);


        // 4、删除 sourceComponentNode 中只有 fileNode 唯一依赖的组件的边
        // 获取fileNode依赖的组件Node(并删除sourceComponentNode)
//        new TestConsoleModelWriter(fileNode.getId()).write(graph);
        Set<Node> dependComponentNodes = GraphManager.findEfferentComponentNodes(graph, fileNode);
        if (dependComponentNodes == null) { // 若无依赖的组件，则无需做后续处理
            return;
        }
        dependComponentNodes.remove(sourceComponentNode);


        // 获取componentNode中只有fileNode依赖的组件Node
        List<Node> subFileNodes = GraphManager.findSubFileNodes(graph, sourceComponentNode);
        if (subFileNodes != null) {
            for (Node subFileNode : subFileNodes) {
                Set<Node> subDependComponentNodes = GraphManager.findEfferentComponentNodes(graph, subFileNode);
                if (subDependComponentNodes == null) {
                    continue;
                }
                for (Node subDependComponentNode : subDependComponentNodes) {
                    dependComponentNodes.remove(subDependComponentNode);
                }
            }
        }

        // 删除sourceComponentNode中只有fileNode依赖的组件Node
        for (Node dependComponentNode : dependComponentNodes) {
            graph.removeDepend(sourceComponentNode, dependComponentNode);
        }

    }
}
