package top.lazyr.refactor.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.architecture.manager.GraphManager;
import top.lazyr.util.ArrayUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author lazyr
 * @created 2022/1/28
 */
public class RefactorListGenerator11 extends RefactorListGenerator {
    private static Logger logger = LoggerFactory.getLogger(RefactorListGenerator11.class);
    private Graph graph;
    private List<Node> smellComponentNodes;
    /**
     * key表示可移动的文件Node
     * value表示可移动的组件(不包含文件Node本身所在的节点)
     */
    private Map<Node, List<Node>> moveFile;
    /**
     * 保存待重构的文件
     * 可每次随机生成下标来选取待重构的文件
     */
    private List<Node> refactoredFileNodes;

    private int smellFileNum;

    public RefactorListGenerator11(Graph graph, List<Node> smellComponentNodes) {
        this.graph = graph;
        this.smellComponentNodes = smellComponentNodes;
        this.smellFileNum = findSubFiles(graph, smellComponentNodes);
        initMoveFile();
        System.out.println("可重构的文件个数: " + refactoredFileNodes.size());
    }

    @Override
    public String generateOne(int r) {
        // 要重构文件的个数（个数在异味文件/重构操作）
        int refactorFileNum = ThreadLocalRandom.current().nextInt(smellFileNum >  r ? smellFileNum / r : 1);
        refactorFileNum = refactorFileNum == 0 ? 1 : refactorFileNum;
        Map<String, String> fileSourceIds = new HashMap<>();
        Map<String, String> fileTargetIds = new HashMap<>();
        while (fileTargetIds.size() < refactorFileNum) {
            // 随机获取待重构文件
            int size = refactoredFileNodes.size();
            int refactoredIndex = ThreadLocalRandom.current().nextInt(size);
            Node refactoredNode = this.refactoredFileNodes.get(refactoredIndex);
            if (fileSourceIds.containsKey(refactoredNode.getId())) {
                continue;
            }
            // 获取重构文件所在的组件
            Node sourceComponentNode = GraphManager.findBelongComponent(graph, refactoredNode);
            // 随机获取要移动的目标组件
            List<Node> movedComponentNodes = moveFile.get(refactoredNode);
            int movedIndex = ThreadLocalRandom.current().nextInt(movedComponentNodes.size());
            Node targetComponentNode = movedComponentNodes.get(movedIndex);
            fileSourceIds.put(refactoredNode.getId(), sourceComponentNode.getId());
            fileTargetIds.put(refactoredNode.getId(), targetComponentNode.getId());
        }

        return moveFiles(fileSourceIds, fileTargetIds);
    }

    @Override
    public String generateOne(List<String> refactors, int r) {
        return generateOne(r);
    }

    @Override
    public List<String> generateList(int r) {
        List<String> refactors = new ArrayList<>();
        for (int i = 0; i < r; i++) {
            String refactor = generateOne(r);
            while (!refactorValidate(refactors, refactor)) { // 若无效，则继续随机生成，直到有效为止
                refactor = generateOne(r);
            }
            refactors.add(refactor);
        }

        return refactors;
    }

    @Override
    public String getIntroduction() {
        return "static moveFiles(生成完全不冲突的重构次操作)";
    }

    private void initMoveFile() {
        this.moveFile = new HashMap<>();
        this.refactoredFileNodes = new ArrayList<>();

        if (this.smellComponentNodes == null) {
            return;
        }

        // 初始化异味文件Node可移动的组件
        for (Node smellComponentNode : smellComponentNodes) {
            // 获取异味组件节点下的文件节点
            List<Node> subFileNodes = GraphManager.findSubFileNodes(graph, smellComponentNode);
            if (subFileNodes == null) {
                continue;
            }
            // 获取每个异味文件节点的系统内传入传出依赖组件
            for (Node subFileNode : subFileNodes) {
                Set<Node> movedComponentNodes = new HashSet<>();
                // 获取依赖subFileNode的所有组件（一定是系统内组件）
                Set<Node> afferentComponentNodes = GraphManager.findAfferentComponentNodes(graph, subFileNode);
                if (afferentComponentNodes != null) {
                    movedComponentNodes.addAll(afferentComponentNodes); // 可能会包含异味组件自身
                }
                // 获取subFileNode依赖的所有系统内组件
                Set<Node> systemEfferentComponentNodes = GraphManager.findSystemEfferentComponentNodes(graph, subFileNode);
                if (systemEfferentComponentNodes != null) {
                    movedComponentNodes.addAll(systemEfferentComponentNodes); // 可能会包含异味组件自身
                }
                // 不可以自己移动自己
                movedComponentNodes.remove(smellComponentNode);
                if (movedComponentNodes.size() > 0) {
                    moveFile.put(subFileNode, new ArrayList<>(movedComponentNodes));
                }
            }

        }

        // 初始化传入组件中文件可移动到的异味组件
        Set<Node> afferentComponentNodes = new HashSet<>();
        for (Node smellComponentNode : smellComponentNodes) {
            List<Node> tempAfferentComponentNodes = GraphManager.findAfferentNodes(graph, smellComponentNode);
            if (tempAfferentComponentNodes == null) {
                continue;
            }
            afferentComponentNodes.addAll(tempAfferentComponentNodes);
        }
        for (Node afferentComponentNode : afferentComponentNodes) {
            if (smellComponentNodes.indexOf(afferentComponentNode) != -1) { // 上面已处理过异味组件，这里不再处理
                continue;
            }
            List<Node> subFileNodes = GraphManager.findSubFileNodes(graph, afferentComponentNode);
            if (subFileNodes == null) {
                continue;
            }
            for (Node subFileNode : subFileNodes) {
//                Set<Node> efferentComponentNodes = GraphManager.findEfferentComponentNodes(graph, subFileNode);
                Set<Node> systemEfferentComponentNodes = GraphManager.findSystemEfferentComponentNodes(graph, subFileNode);
                if (systemEfferentComponentNodes == null) {
                    continue;
                }
                Set<Node> movedComponentNodes = new HashSet<>();

                // 求项目内传入节点和异味节点的交集
                movedComponentNodes.addAll(systemEfferentComponentNodes);
                movedComponentNodes.retainAll(smellComponentNodes);

                // 若afferentComponentNode也为异味组件，则可能会自己移动到自己中（获取传出组件是包括自己的）
                movedComponentNodes.remove(afferentComponentNode);
                if (movedComponentNodes.size() > 0) {
                    moveFile.put(subFileNode, new ArrayList<>(movedComponentNodes));
                }
            }
        }

        // 初始化系统内传出组件中文件可移动到的异味组件
        Set<Node> systemEfferentComponentNodes = new HashSet<>();
        for (Node smellComponentNode : smellComponentNodes) {
            List<Node> tempSystemEfferentComponentNodes = GraphManager.findSystemEfferentNodes(graph, smellComponentNode);
            if (tempSystemEfferentComponentNodes == null) {
                continue;
            }
            systemEfferentComponentNodes.addAll(tempSystemEfferentComponentNodes);
        }
        for (Node systemEfferentComponentNode : systemEfferentComponentNodes) {
            if (smellComponentNodes.indexOf(systemEfferentComponentNode) != -1) { // 上面已处理过异味组件，这里不再处理
                continue;
            }
            List<Node> subFileNodes = GraphManager.findSubFileNodes(graph, systemEfferentComponentNode);
            if (subFileNodes == null) {
                continue;
            }
            for (Node subFileNode : subFileNodes) {
                Set<Node> systemAfferentComponentNodes = GraphManager.findAfferentComponentNodes(graph, subFileNode);
                if (systemAfferentComponentNodes == null) {
                    continue;
                }
                Set<Node> movedComponentNodes = null;
                if (moveFile.containsKey(subFileNode)) { // 若已作为传入组件处理，则在之前的数据基础上继续拓展
                    movedComponentNodes = new HashSet<>(moveFile.get(subFileNode));
                } else {
                    movedComponentNodes = new HashSet<>();
                }

                for (Node smellComponentNode : smellComponentNodes) {
                    if (systemAfferentComponentNodes.contains(smellComponentNode)) {
                        movedComponentNodes.add(smellComponentNode);
                    }
                }
                // 若systemEfferentComponentNode也为异味组件，则可能会自己移动到自己中（获取传入组件是包括自己的）
                movedComponentNodes.remove(systemEfferentComponentNode);
                if (movedComponentNodes.size() > 0) {
                    moveFile.put(subFileNode, ArrayUtil.set2List(movedComponentNodes));
                }
            }

        }

        // 初始化可重构的文件Node
        for (Node refactorNode : moveFile.keySet()) {
            this.refactoredFileNodes.add(refactorNode);
        }

    }

    /**
     * 获取所有componentNodes中的文件Node个数
     * @param componentNodes
     * @return
     */
    private int findSubFiles(Graph graph, List<Node> componentNodes) {
        int subFileNum  = 0;
        for (Node componentNode : componentNodes) {
            List<Node> subFileNodes = GraphManager.findSubFileNodes(graph, componentNode);
            subFileNum += subFileNodes == null ? 0 : subFileNodes.size();
        }
        return subFileNum;
    }
}
