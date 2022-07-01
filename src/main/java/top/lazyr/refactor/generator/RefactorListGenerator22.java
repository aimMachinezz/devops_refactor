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
 * @created 2022/2/5
 */
public class RefactorListGenerator22 extends RefactorListGenerator {
    private static Logger logger = LoggerFactory.getLogger(RefactorListGenerator22.class);
    private Graph graph;
    private List<Node> smellComponentNodes;
    /**
     * key表示可移动的文件Node
     * value表示可移动的组件(不包含文件Node本身所在的节点)
     */
    private Map<Node, List<Node>> moveFile;
    /**
     * 保存可用于moveFile的文件Node
     * 可通过每次随机生成下标来选取待重构的文件
     */
    private List<Node> moveFileNodes;
    /**
     * key表示异味包中的文件Node
     * value表示key的传入/传出依赖集合
     */
    private Map<Node, List<Node>> extractComponent;
    /**
     * 保存可用于extractComponent的文件Node
     * 可通过每次随机生成下标来选取待重构的文件
     */
    private List<Node> extractComponentNodes;

    private int smellFileNum;

    public RefactorListGenerator22(Graph graph, List<Node> smellComponentNodes) {
        this.graph = graph;
        this.smellComponentNodes = smellComponentNodes;
        this.smellFileNum = findSubFiles(graph, smellComponentNodes);
        initMoveFile();
        initExtractComponent();
        System.out.println("可重构的文件个数: " + moveFileNodes.size());
    }

    private void initExtractComponent() {
        this.extractComponent = new HashMap<>();
        this.extractComponentNodes = new ArrayList<>();
        if (this.smellComponentNodes == null) {
            return;
        }

        this.extractComponent = new HashMap<>();
        this.extractComponentNodes = new ArrayList<>();
        if (this.smellComponentNodes == null) {
            return;
        }

        for (Node smellComponentNode : this.smellComponentNodes) {
            List<Node> subFileNodes = GraphManager.findSubFileNodes(graph, smellComponentNode);
            if (subFileNodes == null) {
                continue;
            }
            for (Node subFileNode : subFileNodes) {

                List<Node> afferentFileNodes = GraphManager.findAfferentNodes(graph, subFileNode);
                List<Node> systemEfferentFileNodes = GraphManager.findSystemEfferentNodes(graph, subFileNode);
                Set<Node> afferentEfferentFileNodes = new HashSet<>();
                if (afferentFileNodes != null) {
                    afferentEfferentFileNodes.addAll(afferentFileNodes);
                }
                if (systemEfferentFileNodes != null) {
                    afferentEfferentFileNodes.addAll(systemEfferentFileNodes);
                }
                // 若该异味文件即无传入节点，也无传出节点，则不对该异味文件进行重构
                if (afferentEfferentFileNodes.size() == 0) {
                    continue;
                }
                extractComponentNodes.add(subFileNode);
                extractComponent.put(subFileNode, new ArrayList<>(afferentEfferentFileNodes));
            }
        }
    }

    @Override
    public String generateOne(int r) {
        int action = ThreadLocalRandom.current().nextInt(2);
        String refactor = "";
        switch (action) {
            case 0:
                refactor = generateExtractComponent(r);
                break;
            case 1:
                refactor = generateMoveFiles(r);
                break;
        }

        return refactor;
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
            while (!refactorValidate2(refactors, refactor)) { // 若无效，则继续随机生成，直到有效为止
                refactor = generateOne(r);

            }
            refactors.add(refactor);
        }
        return refactors;
    }

    @Override
    public String getIntroduction() {
        return "static moveFiles(生成完全不一样的重构次操作) && extractComponent";
    }

    private String generateMoveFiles(int r) {
        // 要重构文件的个数（个数在异味文件/重构操作）
        int refactorFileNum = ThreadLocalRandom.current().nextInt(smellFileNum >  r ? smellFileNum / r : 1);
        refactorFileNum = refactorFileNum == 0 ? 1 : refactorFileNum;
        Map<String, String> fileSourceIds = new HashMap<>();
        Map<String, String> fileTargetIds = new HashMap<>();
        while (fileTargetIds.size() < refactorFileNum) {
            // 随机获取待重构文件
            int size = moveFileNodes.size();
            int refactoredIndex = ThreadLocalRandom.current().nextInt(size);
            Node refactoredNode = this.moveFileNodes.get(refactoredIndex);
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

    private String generateExtractComponent(int r) {
        // 随机获取待重构的异味文件
        int size = extractComponentNodes.size();
        int refactoredIndex = ThreadLocalRandom.current().nextInt(size);
        Node refactoredNode = this.extractComponentNodes.get(refactoredIndex);
        // 获取待重构的异味文件的传入和传出异味文件节点
        List<Node> afferentEfferentNodes = extractComponent.get(refactoredNode);
        // 随机获取要重构的传入和传出异味文件节点的个数(因为要重构r次，避免出现最坏情况，所以每次只能重构总数的1/r个文件)
//        System.out.println("afferentEfferentNodes.size() => " + afferentEfferentNodes.size());
        int num = ThreadLocalRandom.current().nextInt(afferentEfferentNodes.size() < r ? 1 : afferentEfferentNodes.size() / r);
//        int num = ThreadLocalRandom.current().nextInt(afferentEfferentNodes.size());
        // 因为算上异味文件节点，抽取组件至少要两个文件
        num = num == 0 ? 1 : num;

        // 随机获取要重构的传入和传出异味文件节点
        Set<Node> refactoredAfferentEfferentNodes = new HashSet<>();
        while (refactoredAfferentEfferentNodes.size() < num) {
            int index = ThreadLocalRandom.current().nextInt(afferentEfferentNodes.size());
            refactoredAfferentEfferentNodes.add(afferentEfferentNodes.get(index));
        }

        Map<String, String> fileComponentIds = new HashMap<>();
        fileComponentIds.put(refactoredNode.getId(), GraphManager.findBelongComponent(graph, refactoredNode).getId());
        for (Node refactoredAfferentEfferentNode : refactoredAfferentEfferentNodes) {
            fileComponentIds.put(refactoredAfferentEfferentNode.getId(), GraphManager.findBelongComponent(graph, refactoredAfferentEfferentNode).getId());
        }

        return extractComponent(GraphManager.findBelongComponent(graph, refactoredNode).getId(), fileComponentIds);
    }

    private void initMoveFile() {
        this.moveFile = new HashMap<>();
        this.moveFileNodes = new ArrayList<>();

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
                Set<Node> afferentComponentNode = GraphManager.findAfferentComponentNodes(graph, subFileNode);
                if (afferentComponentNode != null) {
                    movedComponentNodes.addAll(afferentComponentNode);
                }
                // 获取subFileNode依赖的所有系统内组件
                Set<Node> systemEfferentComponentNodes = GraphManager.findSystemEfferentComponentNodes(graph, subFileNode);
                if (systemEfferentComponentNodes != null) {
                    movedComponentNodes.addAll(systemEfferentComponentNodes);
                }
                // 不用自己依赖自己
                movedComponentNodes.remove(smellComponentNode);
                if (movedComponentNodes.size() > 0) {
                    moveFile.put(subFileNode, ArrayUtil.set2List(movedComponentNodes));
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
                Set<Node> efferentComponentNodes = GraphManager.findEfferentComponentNodes(graph, subFileNode);
                if (efferentComponentNodes == null) {
                    continue;
                }
                Set<Node> movedComponentNodes = new HashSet<>();
                for (Node smellComponentNode : smellComponentNodes) {
                    if (efferentComponentNodes.contains(smellComponentNode)) {
                        movedComponentNodes.add(smellComponentNode);
                    }
                }
                // 若afferentComponentNode也为异味组件，则可能会自己移动到自己中（获取传出组件是包括自己的）
                movedComponentNodes.remove(afferentComponentNode);
                if (movedComponentNodes.size() > 0) {
                    moveFile.put(subFileNode, ArrayUtil.set2List(movedComponentNodes));
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
            this.moveFileNodes.add(refactorNode);
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
