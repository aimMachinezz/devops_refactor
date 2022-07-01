package top.lazyr.ui.soultionframe;

import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.architecture.manager.GraphManager;
import top.lazyr.constant.NodeConstant;
import top.lazyr.smell.detector.cyclicdependency.CyclicDependencyDetector;
import top.lazyr.smell.detector.hublikedependency.HubLikeDependencyDetector;
import top.lazyr.smell.detector.unstabledependency.UnstableDependencyDetector;
import top.lazyr.util.ArrayUtil;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * @author lazyr
 * @created 2022/2/1
 */
public class SolutionPanel extends JPanel {
    /* 文件列表 */
    private JScrollPane listScrollPane;
    private Map<String, FileNode> catalogNodeMap;
    private String rootNodeName = "系统内目录";
    private JTree tree;

    /* 信息展示面板 */
    private JPanel infoShowPanel;
    /* 信息展示面板的布局 */
    private CardLayout cardLayout;
    /* 组件信息面板 */
    private String componentInfo = "component_info";
    private ComponentInfoScrollPanel componentInfoScrollPanel;
    /* 文件信息面板 */
    private String fileInfo = "file_info";
    private FileInfoScrollPanel fileInfoScrollPanel;
    /* 重构信息面板 */
    private String refactoredInfo = "refactored_info";
    private JScrollPane refactoredInfoScrollPane;


    public SolutionPanel(Graph originGraph, Graph refactoredGraph, List<String> refactors) {
        initListScrollPanel(originGraph, refactoredGraph);
        initInfoShowPanel(originGraph, refactoredGraph, refactors);
        initLayout();
    }

    private void initListScrollPanel(Graph originGraph, Graph refactoredGraph) {
        JPanel listPanel = new JPanel(new BorderLayout());
        initJTree(originGraph, refactoredGraph);
        listPanel.add(tree, BorderLayout.CENTER);
        listScrollPane = new JScrollPane();
        listScrollPane.setViewportView(listPanel);
        listScrollPane.getVerticalScrollBar().setUnitIncrement(8);
    }

    private void initInfoShowPanel(Graph originGraph, Graph refactoredGraph, List<String> refactors) {
        refactoredInfoScrollPane = new RefactoredInfoScrollPanel(originGraph, refactoredGraph, tree, refactors);
        componentInfoScrollPanel = new ComponentInfoScrollPanel();
        fileInfoScrollPanel = new FileInfoScrollPanel();
        cardLayout = new CardLayout();
        infoShowPanel = new JPanel();
        infoShowPanel.setLayout(cardLayout);

        infoShowPanel.add(refactoredInfoScrollPane, refactoredInfo);
        infoShowPanel.add(componentInfoScrollPanel, componentInfo);
        infoShowPanel.add(fileInfoScrollPanel, fileInfo);
        cardLayout.show(infoShowPanel, refactoredInfo);
    }

    private void initLayout() {
        this.setLayout(new GridLayout(1, 2));
        this.add(listScrollPane);
        this.add(infoShowPanel);

    }

    /**
     * 通过对比 originGraph 和 refactoredGraph中系统内文件位置，创建文件节点
     * 返回一个根目录FileNode
     * @param originGraph
     * @param refactoredGraph
     * @return
     */
    private void initJTree(Graph  originGraph, Graph refactoredGraph) {
        this.catalogNodeMap = new HashMap<>();
        List<Node> systemComponentNodes = refactoredGraph.filterSystemComponentNodes();
        FileNode rootNode = initCatalogNodes(systemComponentNodes);
        initFileNodes(originGraph, refactoredGraph);
        this.tree = new JTree(rootNode);
        tree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
                        .getLastSelectedPathComponent();

                if (node == null)
                    return;
                FileNode fileNode = (FileNode) node;

                if (fileNode.isDirectory() && fileNode.isComponent()) { // 是组件节点
                    componentInfoScrollPanel.updateInfo(fileNode);
                    cardLayout.show(infoShowPanel, componentInfo);
                } else if (!fileNode.isDirectory()) { // 是文件节点
                    fileInfoScrollPanel.updateInfo(fileNode);
                    cardLayout.show(infoShowPanel, fileInfo);
                } else if(fileNode.getName().equals(rootNodeName)) {
                    cardLayout.show(infoShowPanel, refactoredInfo);
                }

            }
        });
    }

    /**
     * 构建所有的文件
     * @param originGraph
     * @param refactoredGraph
     */
    private void initFileNodes(Graph originGraph, Graph refactoredGraph) {
        Set<Node> originHLNodes = HubLikeDependencyDetector.detect(originGraph).keySet();
        Set<Node> originUDNodes = UnstableDependencyDetector.detect(originGraph).keySet();
        Set<Node> originCDNodes = new HashSet<>(CyclicDependencyDetector.detect(originGraph));

        Set<Node> refactoredHLNodes = HubLikeDependencyDetector.detect(refactoredGraph).keySet();
        Set<Node> refactoredUDNodes = UnstableDependencyDetector.detect(refactoredGraph).keySet();
        Set<Node> refactoredCDNodes = new HashSet<>(CyclicDependencyDetector.detect(refactoredGraph));

        for (String catalog : catalogNodeMap.keySet()) {
            String componentNodeId = catalog +
                    NodeConstant.SEPARATOR +
                    NodeConstant.LEVEL_COMPONENT +
                    NodeConstant.SEPARATOR +
                    NodeConstant.FROM_SYSTEM;
            Node originSystemComponentNode = originGraph.findNodeById(componentNodeId);
            Node refactoredSystemComponentNode = refactoredGraph.findNodeById(componentNodeId);

            if (originSystemComponentNode == null && refactoredSystemComponentNode == null) { // 若当前catalog不是组件
                continue;
            }

            FileNode catalogNode = catalogNodeMap.get(catalog);
            fillCatalogNode(catalogNode,
                            originGraph,
                            refactoredGraph,
                            // 重构前后是否还是枢纽型异味
                            originHLNodes.contains(originSystemComponentNode),
                            refactoredHLNodes.contains(refactoredSystemComponentNode),
                            // 重构前后是否还是不稳定型异味
                            originUDNodes.contains(originSystemComponentNode),
                            refactoredUDNodes.contains(refactoredSystemComponentNode),
                            // 重构前后是否还是环依赖异味
                            originCDNodes.contains(originSystemComponentNode),
                            refactoredCDNodes.contains(refactoredSystemComponentNode));

        }
    }

    /**
     * 完善所有组件FileNode信息
     * @param catalogNode
     * @param originGraph
     * @param refactoredGraph
     * @param originHL
     * @param refactoredHL
     * @param originUD
     * @param refactoredUD
     * @param originCD
     * @param refactoredCD
     */
    private void fillCatalogNode(FileNode catalogNode, Graph originGraph, Graph refactoredGraph, boolean originHL, boolean refactoredHL, boolean originUD, boolean refactoredUD, boolean originCD, boolean refactoredCD) {
        String componentNodeId = catalogNode.getCompleteName() +
                NodeConstant.SEPARATOR +
                NodeConstant.LEVEL_COMPONENT +
                NodeConstant.SEPARATOR +
                NodeConstant.FROM_SYSTEM;

        Node originSystemComponentNode = originGraph.findNodeById(componentNodeId);
        Node refactoredSystemComponentNode = refactoredGraph.findNodeById(componentNodeId);

        if (originSystemComponentNode == null) { // 表示该目录为新创建的
            // 更新组件基本信息
            catalogNode.setComponent(true);
            catalogNode.setUpdated(true);

            // 更新组件重构前后的传入/传出节点ID
            catalogNode.setOriginAfferentIds(null);
            catalogNode.setOriginEfferentIds(null);
            catalogNode.setRefactoredAfferentIds(nodes2Ids(GraphManager.findAfferentNodes(refactoredGraph, refactoredSystemComponentNode)));
            catalogNode.setRefactoredEfferentIds(nodes2Ids(GraphManager.findEfferentNodes(refactoredGraph, refactoredSystemComponentNode)));

            // 更新组件重构前后的异味信息
            catalogNode.setOriginHubLike(false);
            catalogNode.setOriginUnstable(false);
            catalogNode.setOriginCyclic(false);
            catalogNode.setRefactoredHubLike(refactoredHL);
            catalogNode.setRefactoredUnstable(refactoredUD);
            catalogNode.setRefactoredCyclic(refactoredCD);

            List<Node> refactoredFileNodes = GraphManager.findSubFileNodes(refactoredGraph, refactoredSystemComponentNode);
            if (refactoredFileNodes == null) {
                return;
            }
            for (Node refactoredFileNode : refactoredFileNodes) {
                FileNode fileNode = buildFileNode(refactoredFileNode.getId(), refactoredGraph, false, true);
                catalogNode.add(fileNode);
            }
        } else {

            // 获取当前目录重构前后的文件
            List<Node> originFileNodes = GraphManager.findSubFileNodes(originGraph, originSystemComponentNode);
            List<Node> refactoredFileNodes = GraphManager.findSubFileNodes(refactoredGraph, refactoredSystemComponentNode);
            // 对比文件的区别
            List<List<String>> diff = ArrayUtil.diff(nodes2Ids(originFileNodes), nodes2Ids(refactoredFileNodes));
            List<String> movedFileNodeIds = diff.get(0);
            List<String> createdFileNodeIds = diff.get(1);
            List<String> noRefactoredFileNodeIds = diff.get(2);

            // 更新组件基本信息
            catalogNode.setComponent(true);
            catalogNode.setUpdated(movedFileNodeIds.size() > 0 || createdFileNodeIds.size() > 0);

            // 更新组件重构前后的传入/传出节点ID
            catalogNode.setOriginAfferentIds(nodes2Ids(GraphManager.findAfferentNodes(originGraph, originSystemComponentNode)));
            catalogNode.setOriginEfferentIds(nodes2Ids(GraphManager.findEfferentNodes(originGraph, originSystemComponentNode)));
            catalogNode.setRefactoredAfferentIds(nodes2Ids(GraphManager.findAfferentNodes(refactoredGraph, refactoredSystemComponentNode)));
            catalogNode.setRefactoredEfferentIds(nodes2Ids(GraphManager.findEfferentNodes(refactoredGraph, refactoredSystemComponentNode)));


            // 更新组件重构前后的异味信息
            catalogNode.setOriginHubLike(originHL);
            catalogNode.setOriginUnstable(originUD);
            catalogNode.setOriginCyclic(originCD);
            catalogNode.setRefactoredHubLike(refactoredHL);
            catalogNode.setRefactoredUnstable(refactoredUD);
            catalogNode.setRefactoredCyclic(refactoredCD);


            for (String movedFileNodeId : movedFileNodeIds) {
                FileNode fileNode = buildFileNode(movedFileNodeId, refactoredGraph, true, false);
                catalogNode.add(fileNode);
            }
            for (String createdFileNodeId : createdFileNodeIds) {
                FileNode fileNode = buildFileNode(createdFileNodeId, refactoredGraph, false, true);
                catalogNode.add(fileNode);
            }
            for (String noRefactoredFileNodeId : noRefactoredFileNodeIds) {
                FileNode fileNode = buildFileNode(noRefactoredFileNodeId, refactoredGraph, false, false);
                catalogNode.add(fileNode);
            }

        }

    }

    /**
     * 构建文件FileNode
     * @param refactoredFileNodeId
     * @param refactoredGraph
     * @param moved
     * @param created
     * @return
     */
    private FileNode buildFileNode(String refactoredFileNodeId, Graph refactoredGraph, boolean moved, boolean created) {
        Node refactoredFileNode = refactoredGraph.findNodeById(refactoredFileNodeId);
        FileNode fileNode = FileNode.builder()
                .name(extractCurrentPath(refactoredFileNode.getName()))
                .completeName(refactoredFileNode.getName())
                .directory(false)
                .moved(moved)
                .created(created)
                .originCatalog(extractParentPath(refactoredFileNode.getName()))
                .refactoredCatalog(GraphManager.findBelongComponent(refactoredGraph, refactoredFileNode).getName())
                .originAfferentIds(nodes2Ids(GraphManager.findAfferentNodes(refactoredGraph, refactoredFileNode)))
                .originEfferentIds(nodes2Ids(GraphManager.findEfferentNodes(refactoredGraph, refactoredFileNode)))
                .build();
        return fileNode;
    }

    /**
     * 构建系统内目录节点，返回根节点
     * @param systemComponentNodes
     * @return
     */
    private FileNode initCatalogNodes(List<Node> systemComponentNodes) {
        FileNode rootNode = FileNode.builder()
                                        .name(rootNodeName)
                                        .completeName(rootNodeName)
                                        .directory(true)
                                        .system(true)
                                        .build();

        for (Node systemComponentNode : systemComponentNodes) {
            FileNode preNode = null;
            FileNode currentNode = rootNode;

            List<String> allPaths = extractAllPaths(systemComponentNode.getName());
            for (String completePath : allPaths) {
                preNode = currentNode;
                currentNode = catalogNodeMap.get(completePath);
                if (currentNode == null) {
                    currentNode = FileNode.builder()
                                            .name(extractCurrentPath(completePath))
                                            .completeName(completePath)
                                            .directory(true)
                                            .system(true)
                                            .build();

                }
                catalogNodeMap.put(completePath, currentNode);
                preNode.add(currentNode);
            }

        }
        return rootNode;
    }

    /**
     * 按从浅到深依次抽取completePath上所有路径，如
     * 输入: a.b.c.d
     * 返回: ["a", "a.b", "a.b.c", "a.b.c.d"]
     * @param completePath
     * @return
     */
    private List<String> extractAllPaths(String completePath) {
        List<String> allCompletePaths = new ArrayList<>();
        // 按从深到浅创建
        while (!completePath.equals("")) {
            allCompletePaths.add(completePath);
            completePath = extractParentPath(completePath);
        }
        // 反转，转化为从浅到深
        Collections.reverse(allCompletePaths);
        return allCompletePaths;
    }

    /**
     * 抽取完整路径completePath的父级路径，如
     * 输入: a.b, 返回: a
     * 输入: a, 返回: ""
     * @param completePath
     * @return
     */
    private String extractParentPath(String completePath) {
        if (!completePath.contains(".")) {
            return "";
        }
        return completePath.substring(0, completePath.lastIndexOf("."));
    }

    /**
     * 抽取完整路径completePath的当前路径，如
     * 输入: a.b.c, 返回: c
     * 输入: a, 返回: a
     * @param completePath
     * @return
     */
    private String extractCurrentPath(String completePath) {
        if (!completePath.contains(".")) {
            return completePath;
        }
        return completePath.substring(completePath.lastIndexOf(".") + 1);
    }

    /**
     * 将一组nodes转换为一组nodeIds返回
     * - 若nodes为null，则返回null
     * @param nodes
     * @return
     */
    private List<String> nodes2Ids(List<Node> nodes) {
        if (nodes == null) {
            return null;
        }
        List<String> nodeIds = new ArrayList<>();
        for (Node efferentNode : nodes) {
            nodeIds.add(efferentNode.getId());
        }
        return nodeIds;
    }


}
