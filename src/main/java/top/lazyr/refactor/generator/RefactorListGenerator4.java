package top.lazyr.refactor.generator;


import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.architecture.manager.GraphManager;
import top.lazyr.refactor.actuator.RefactorActuator;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author lazyr
 * @created 2022/2/13
 */
public class RefactorListGenerator4 extends RefactorListGenerator{
    private Graph refactoringGraph;
    private Graph originGraph;
    private Set<String> smellComponentNodeIds;
    /**
     * 保存可用于moveFile的文件id
     * 可每次随机生成下标来选取待重构的文件
     */
    private List<String> refactoredFileNodeIds;
    /**
     * key为系统内文件id
     * value为key的所有传入文件id
     */
    private Map<String, List<String>> afferentFileNodeIds;
    /**
     * key为系统内文件id
     * value为key的所有项目内传出文件id
     */
    private Map<String, List<String>> systemEfferentFileNodeIds;

    public RefactorListGenerator4(Graph graph, List<Node> smellComponentNodes) {
        this.originGraph = graph;
        this.smellComponentNodeIds = new HashSet<>(GraphManager.nodes2Ids(smellComponentNodes));
        refresh();
        initRefactoredFileNodeIds();
        System.out.println("可重构的文件个数: " + refactoredFileNodeIds.size());
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
                refactor = generateMoveFile(r);
                break;
        }
        return refactor;
    }

    private String generateExtractComponent(int r) {
        // 随机获取带重构的异味文件
        Node refactorFileNode = null;
        Node belongComponent = null;
        String refactor = null;

        while (refactor == null) {
            while (refactorFileNode == null) {
                int refactorFileIndex = ThreadLocalRandom.current().nextInt(refactoredFileNodeIds.size());
                String refactorFileNodeId = refactoredFileNodeIds.get(refactorFileIndex);
                refactorFileNode = refactoringGraph.findNodeById(refactorFileNodeId);
                belongComponent = GraphManager.findBelongComponent(refactoringGraph, refactorFileNode);
                if (!smellComponentNodeIds.contains(belongComponent.getId())) { // 若refactorFileNode已不属于异味组件，则不在对其进行抽取异味组件操作
                    refactorFileNode = null;
                    belongComponent = null;
                }
            }
            List<String> afferentFileNodeIds = this.afferentFileNodeIds.get(refactorFileNode.getId());
            List<String> efferentFileNodeIds = this.systemEfferentFileNodeIds.get(refactorFileNode.getId());
            List<String> afferentEfferentNodeIds = new ArrayList<>();
            if (afferentFileNodeIds != null) {
                afferentEfferentNodeIds.addAll(afferentFileNodeIds);
            }
            if (efferentFileNodeIds != null) {
                afferentEfferentNodeIds.addAll(efferentFileNodeIds);
            }
            if (afferentEfferentNodeIds.size() > 0) {

                // 获取要抽取组件的传入传出文件的个数
                int num = ThreadLocalRandom.current().nextInt(afferentEfferentNodeIds.size() < r ? 1 : afferentEfferentNodeIds.size() / r);

//                int num = ThreadLocalRandom.current().nextInt(afferentEfferentNodeIds.size()) + 1;
                num = num == 0 ? 1 : num;

                // 随机获取要重构的传入和传出异味文件节点
                Set<String> refactoredAfferentEfferentNodeIds = new HashSet<>();
                while (refactoredAfferentEfferentNodeIds.size() < num) {
                    int index = ThreadLocalRandom.current().nextInt(afferentEfferentNodeIds.size());
                    refactoredAfferentEfferentNodeIds.add(afferentEfferentNodeIds.get(index));
                }
                Map<String, String> fileComponentIds = new HashMap<>();
                fileComponentIds.put(refactorFileNode.getId(), belongComponent.getId());
                for (String refactoredAfferentEfferentNodeId : refactoredAfferentEfferentNodeIds) {
                    fileComponentIds.put(refactoredAfferentEfferentNodeId, GraphManager.findBelongComponent(refactoringGraph, refactoringGraph.findNodeById(refactoredAfferentEfferentNodeId)).getId());
                }
                refactor = extractComponent(belongComponent.getId(), fileComponentIds);
            } else {
                refactorFileNode = null;
                belongComponent = null;
            }
        }

        return refactor;
    }

    private String generateMoveFile(int r) {
        int size = refactoredFileNodeIds.size();
        Node sourceComponentNode = null;
        Node refactoredFileNode = null;
        Node targetComponentNode = null;
        while (targetComponentNode == null) { // 若待移动的目标组件不存在，则继续随机获取
            // 随机获取待重构文件
            int refactoredIndex = ThreadLocalRandom.current().nextInt(size);
            // 获取待重构的文件Node
            String refactoredFileNodeId = this.refactoredFileNodeIds.get(refactoredIndex);
            refactoredFileNode = refactoringGraph.findNodeById(refactoredFileNodeId);
            // 获取待重构的文件Node此刻所属的组件Node
            sourceComponentNode = GraphManager.findBelongComponent(refactoringGraph, refactoredFileNode);
            // 随机获取要移动的目标组件
            targetComponentNode = generateTargetComponentNode(refactoredFileNode, sourceComponentNode);
        }
        String refactor = moveFile(sourceComponentNode.getId(), refactoredFileNode.getId(), targetComponentNode.getId());
        return refactor;
    }

    @Override
    public String generateOne(List<String> refactors, int r) {
        this.refresh();
        RefactorActuator.refactor(refactoringGraph, refactors);
        String refactor = generateOne(r);
        return refactor;
    }

    @Override
    public List<String> generateList(int r) {
        this.refresh();
        List<String> refactors = new ArrayList<>();
        for (int i = 0; i < r; i++) {
            String refactor = generateOne(r);
            while (!refactorValidate(refactors, refactor)) { // 若无效，则继续随机生成，直到有效为止
                refactor = generateOne(r);
            }
            RefactorActuator.refactor(refactoringGraph, refactor);
            refactors.add(refactor);
        }
        return refactors;
    }

    /**
     * 若refactoredFileNode为异味组件下的文件，则其传入/传出项目内组件都可进行移动
     * 若refactoredFileNode为非异味组件中的文件，则只可以移动到其传入/传出组件为异味的组件
     * 若无可移动的的组件，则返回null
     * @param refactoredFileNode
     * @param sourceComponentNode
     * @return
     */
    private Node generateTargetComponentNode(Node refactoredFileNode, Node sourceComponentNode) {
        Set<Node> afferentComponentNodes = findAfferentComponentNodes(refactoredFileNode);
        Set<Node> efferentComponentNodes = findSystemEfferentComponentNodes(refactoredFileNode);
        Set<Node> movedSmellComponentNodeSet = new HashSet<>();
        if (smellComponentNodeIds.contains(sourceComponentNode.getId())) {
            // 若refactoredFileNode为异味组件下的文件，则其项目内传入传出组件都可进行移动
            if (afferentComponentNodes != null) {
                movedSmellComponentNodeSet.addAll(afferentComponentNodes);
            }
            if (efferentComponentNodes != null) {
                movedSmellComponentNodeSet.addAll(efferentComponentNodes);
            }
            movedSmellComponentNodeSet.remove(sourceComponentNode);
        } else {
            // 若refactoredFileNode为非异味组件中的文件，则只可以移动到其传入/传出组件为异味的组件
            Set<Node> afferentSmellComponentNodes = new HashSet<>();
            if (afferentComponentNodes != null) {
                afferentSmellComponentNodes.addAll(GraphManager.ids2Nodes(refactoringGraph, smellComponentNodeIds));
                afferentSmellComponentNodes.retainAll(afferentComponentNodes);
            }

            Set<Node> efferentSmellComponentNodes = new HashSet<>();
            if (efferentComponentNodes != null) {
                efferentSmellComponentNodes.addAll(GraphManager.ids2Nodes(refactoringGraph, smellComponentNodeIds));
                efferentSmellComponentNodes.retainAll(efferentComponentNodes);
            }

            movedSmellComponentNodeSet.addAll(afferentSmellComponentNodes);
            movedSmellComponentNodeSet.addAll(efferentSmellComponentNodes);
            movedSmellComponentNodeSet.remove(sourceComponentNode);
        }
        return selectMovedSmellComponentNode(movedSmellComponentNodeSet);
    }

    private Node selectMovedSmellComponentNode(Set<Node> movedSmellComponentNodeSet) {
        if (movedSmellComponentNodeSet == null || movedSmellComponentNodeSet.size() == 0) {
            return null;
        }
        List<Node> movedSmellComponentNodes = new ArrayList<>(movedSmellComponentNodeSet);
        int movedIndex = ThreadLocalRandom.current().nextInt(movedSmellComponentNodes.size());
        return movedSmellComponentNodes.get(movedIndex);
    }

    @Override
    public String getIntroduction() {
        return "dynamic moveFile";
    }

    public void refresh() {
        this.refactoringGraph = GraphManager.cloneGraph(originGraph);
    }

    private void initRefactoredFileNodeIds()  {
        this.refactoredFileNodeIds = new ArrayList<>();
        initAfferentEfferent();
        if (this.smellComponentNodeIds == null) {
            return;
        }

        Set<String> refactoredFileNodeIdSet = new HashSet<>();
        for (String smellComponentNodeId : this.smellComponentNodeIds) {
            Node smellComponentNode = originGraph.findNodeById(smellComponentNodeId);
            List<Node> subFileNodes = GraphManager.findSubFileNodes(originGraph, smellComponentNode);
            if (subFileNodes == null) {
                continue;
            }
            refactoredFileNodeIdSet.addAll(GraphManager.nodes2Ids(subFileNodes));
            for (Node subFileNode : subFileNodes) {
                List<String> afferentFileNodeIds = this.afferentFileNodeIds.get(subFileNode.getId());
                if (afferentFileNodeIds != null) {
                    refactoredFileNodeIdSet.addAll(afferentFileNodeIds);
                }
                List<String> efferentFileNodes = this.systemEfferentFileNodeIds.get(subFileNode);
                if (efferentFileNodes != null) {
                    refactoredFileNodeIdSet.addAll(efferentFileNodes);
                }
            }
        }
        refactoredFileNodeIds = new ArrayList<>(refactoredFileNodeIdSet);
    }

    private void initAfferentEfferent() {
        if (this.afferentFileNodeIds != null && this.systemEfferentFileNodeIds != null) {
            return;
        }
        this.afferentFileNodeIds = new HashMap<>();
        this.systemEfferentFileNodeIds = new HashMap<>();

        List<Node> systemFileNodes = originGraph.filterSystemFileNodes();
        if (systemFileNodes == null) {
            return;
        }
        for (Node systemFileNode : systemFileNodes) {
            List<Node> afferentNodes = GraphManager.findAfferentNodes(originGraph, systemFileNode);
            afferentFileNodeIds.put(systemFileNode.getId(), GraphManager.nodes2Ids(afferentNodes));
            List<Node> systemEfferentNodes = GraphManager.findSystemEfferentNodes(originGraph, systemFileNode);
            systemEfferentFileNodeIds.put(systemFileNode.getId(), GraphManager.nodes2Ids(systemEfferentNodes));
        }
    }

    /**
     * 获取fileNode的系统内传出文件Node所对应的组件Node
     * - 若fileNode为null或graph为null，则返回null
     * - 若fileNode不是文件粒度，则返回null
     * - 若无传出文件Node，则返回null
     * - 返回结果包含fileNode所在的组件Node
     * @param fileNode
     * @return
     */
    private Set<Node> findSystemEfferentComponentNodes(Node fileNode) {
        if (refactoringGraph == null || fileNode == null) {
            return null;
        }
        if (fileNode.isComponent()) {
            return null;
        }
        List<String> efferentNodeIds = systemEfferentFileNodeIds.get(fileNode.getId());
        if (efferentNodeIds == null) {
            return null;
        }
        Set<Node> efferentComponentNodes = new HashSet<>();
        for (String efferentFileNodeId : efferentNodeIds) {
            Node efferentFileNode = refactoringGraph.findNodeById(efferentFileNodeId);
            Node belongComponent = GraphManager.findBelongComponent(refactoringGraph, efferentFileNode);
            if (belongComponent == null) {
                continue;
            }
            if (!belongComponent.isSystem()) {
                continue;
            }
            efferentComponentNodes.add(belongComponent);
        }
        return efferentComponentNodes;
    }

    /**
     * 获取fileNode的传入文件Node所对应的组件Node
     * - 若fileNode为null或graph为null，则返回null
     * - 若fileNode不是文件粒度，则返回null
     * - 若无入出文件Node，则返回null
     * - 返回结果包含fileNode所在的组件Node
     * @param fileNode
     * @return
     */
    public Set<Node> findAfferentComponentNodes(Node fileNode) {
        if (refactoringGraph == null || fileNode == null) {
            return null;
        }
        if (fileNode.isComponent()) {
            return null;
        }
        // 获取所有传入节点
        List<String> afferentNodeIds = afferentFileNodeIds.get(fileNode.getId());
        if (afferentNodeIds == null) {
            return null;
        }
        Set<Node> afferentComponentNodes = new HashSet<>();
        for (String afferentFileNodeIds : afferentNodeIds) {
            Node afferentFileNode = refactoringGraph.findNodeById(afferentFileNodeIds);
            Node belongComponent = GraphManager.findBelongComponent(refactoringGraph, afferentFileNode);
            afferentComponentNodes.add(belongComponent);
        }
        return afferentComponentNodes;
    }
}
