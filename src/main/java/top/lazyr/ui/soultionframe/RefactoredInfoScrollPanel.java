package top.lazyr.ui.soultionframe;

import top.lazyr.architecture.diagram.Graph;
import top.lazyr.architecture.diagram.Node;
import top.lazyr.constant.ConsolePrinter;
import top.lazyr.constant.NodeConstant;
import top.lazyr.constant.RefactorConstant;
import top.lazyr.smell.detector.cyclicdependency.CyclicDependencyDetector;
import top.lazyr.smell.detector.hublikedependency.HubLikeDependencyDetector;
import top.lazyr.smell.detector.unstabledependency.UnstableDependencyDetector;
import top.lazyr.util.ObjectiveUtil;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;

/**
 * @author lazyr
 * @created 2022/2/3
 */
public class RefactoredInfoScrollPanel extends JScrollPane {
    /* 异味重构前后变化信息 */
    private JTextArea refactoredSmellEffectArea;
    /* 异味重构前后目标函数变化信息 */
    private JTextArea refactorObjectiveArea;
    /* 重构操作列表 */
    private JTextArea refactorsArea;
    /* 异味重构信息 */
    private JTextArea smellRefactoredArea;

    public RefactoredInfoScrollPanel(Graph originGraph, Graph refactoredGraph, JTree tree, List<String> refactors) {
        initArea();
        initPanel();
        initData(originGraph, refactoredGraph, tree, refactors);
    }

    private void initData(Graph originGraph, Graph refactoredGraph, JTree tree, List<String> refactors) {
        initRefactoredSmellEffect(originGraph, refactoredGraph);
        initRefactorObjective(originGraph, refactoredGraph);
        initRefactorsArea(refactors);
        initRefactoredComponentArea(tree);
    }

    private void initRefactorObjective(Graph originGraph, Graph refactoredGraph) {
        DecimalFormat format = new DecimalFormat("#0.000");

        StringBuilder cohesion = new StringBuilder();
        double originCohesion= ObjectiveUtil.calCohesion(originGraph);
        double refactoredCohesion = ObjectiveUtil.calCohesion(refactoredGraph);
        cohesion.append("重构前内聚性: " + format.format(originCohesion) + ", 重构后内聚性: " + format.format(refactoredCohesion) + ", 改善率： " + format.format(1 + ((refactoredCohesion - originCohesion) / originCohesion)));

        StringBuilder coupling = new StringBuilder();
        double originCoupling = ObjectiveUtil.calCoupling(originGraph);
        double refactoredCoupling = ObjectiveUtil.calCoupling(refactoredGraph);
        coupling.append("重构前耦合度: " + format.format(originCoupling) + ", 重构后耦合度: " + format.format(refactoredCoupling) + ", 改善率： " + format.format(1 + ((originCoupling - refactoredCoupling) / originCoupling)));
        this.refactorObjectiveArea.setText(
                cohesion.toString() + "\n" + coupling
        );

    }

    private void initRefactoredSmellEffect(Graph originGraph, Graph refactoredGraph) {
        Set<Node> originHLNodes = HubLikeDependencyDetector.detect(originGraph).keySet();
        Set<Node> originUDNodes = UnstableDependencyDetector.detect(originGraph).keySet();
        Set<Node> originCDNodes = new HashSet<>(CyclicDependencyDetector.detect(originGraph));

        Set<Node> originSmellNodes = new HashSet<>();
        originSmellNodes.addAll(originHLNodes);
        originSmellNodes.addAll(originUDNodes);
        originSmellNodes.addAll(originCDNodes);

        Set<Node> refactoredHLNodes = HubLikeDependencyDetector.detect(refactoredGraph).keySet();
        Set<Node> refactoredUDNodes = UnstableDependencyDetector.detect(refactoredGraph).keySet();
        Set<Node> refactoredCDNodes = new HashSet<>(CyclicDependencyDetector.detect(refactoredGraph));

        Set<Node> refactoredSmellNodes = new HashSet<>();
        refactoredSmellNodes.addAll(refactoredHLNodes);
        refactoredSmellNodes.addAll(refactoredUDNodes);
        refactoredSmellNodes.addAll(refactoredCDNodes);




        StringBuilder smellInfo = new StringBuilder();
        smellInfo.append(
                        "重构前异味组件数: " + (originSmellNodes.size()) +
                        ", 重构后异味组件数: " + (refactoredSmellNodes.size()) + "\n" +
                        "重构前总异味数: " + (originHLNodes.size() + originUDNodes.size() + originCDNodes.size()) +
                        ", 重构后总异味数: " + (refactoredHLNodes.size() + refactoredUDNodes.size() + refactoredCDNodes.size()) + "\n" +
                                ConsolePrinter.SHORT_SEPARATOR + "枢纽型异味" + ConsolePrinter.SHORT_SEPARATOR + "\n" +
                        buildSmellEffect(originHLNodes, refactoredHLNodes, "枢纽型异味") + "\n" +
                                ConsolePrinter.SHORT_SEPARATOR + "不稳定异味" + ConsolePrinter.SHORT_SEPARATOR + "\n" +
                        buildSmellEffect(originUDNodes, refactoredUDNodes, "不稳定异味") + "\n" +
                                ConsolePrinter.SHORT_SEPARATOR + "环依赖异味" + ConsolePrinter.SHORT_SEPARATOR + "\n" +
                        buildSmellEffect(originCDNodes, refactoredCDNodes, "环依赖异味")
                         );

        refactoredSmellEffectArea.setText(
                smellInfo.toString()
        );
    }

    /**
     * 返回
     * 重构前smellName数: num, 重构后smellName数: num
     * smellName未消除的组件: componentName1, componentName2, ...
     * smellName已消除的组件: componentName1, componentName2, ...
     * 新引入smellName的组件: componentName1, componentName2, ...
     * @param originSmellNodes
     * @param refactoredSmellNodes
     * @param smellName
     * @return
     */
    private String buildSmellEffect(Set<Node> originSmellNodes, Set<Node> refactoredSmellNodes, String smellName) {
        Set<String> originSmellNodeNames = nodes2Names(originSmellNodes);
        Set<String> refactoredSmellNodeNames = nodes2Names(refactoredSmellNodes);


        StringBuilder smellRefactorInfo = new StringBuilder();
        smellRefactorInfo.append("重构前" + smellName + "数: " + originSmellNodes.size() + ", 重构后" + smellName + "数: " + refactoredSmellNodes.size() + "\n");
        Set<String> remainComponents = new HashSet<>();
        Set<String> deleteComponents = new HashSet<>();
        Set<String> newComponents = new HashSet<>();


        remainComponents.addAll(originSmellNodeNames);
        remainComponents.retainAll(refactoredSmellNodeNames);

        deleteComponents.addAll(originSmellNodeNames);
        deleteComponents.removeAll(remainComponents);

        newComponents.addAll(refactoredSmellNodeNames);
        newComponents.removeAll(remainComponents);


        smellRefactorInfo.append(smellName + "已消除的组件: \n" + toListString(deleteComponents) + "\n");
        smellRefactorInfo.append(smellName + "未消除的组件: \n" + toListString(remainComponents)+ "\n");
        smellRefactorInfo.append("新引入" + smellName + "的组件: \n" + toListString(newComponents));




        return smellRefactorInfo.toString();
    }

    /**
     * 将 [a, b, c] 转换为 - a\n- b\n -c
     * 若remainComponents为null或size=0，则返回"无"
     * @param remainComponents
     * @return
     */
    private String toListString(Set<String> remainComponents) {
        if (remainComponents == null || remainComponents.size() == 0) {
            return "无";
        }
        StringBuilder listString = new StringBuilder();
        int i = 0;
        for (String remainComponent : remainComponents) {
            listString.append("- " + remainComponent);
            if (i != remainComponents.size() - 1) {
                listString.append("\n");
                i++;
            }
        }
        return listString.toString();

    }

    private void initRefactoredComponentArea(JTree tree) {
        FileNode rootNode = (FileNode)tree.getModel().getRoot();
        StringBuilder updatedComponent = new StringBuilder();
        StringBuilder noUpdatedOriComponent = new StringBuilder();
        StringBuilder noUpdatedRefComponent = new StringBuilder();
        bfs(rootNode, updatedComponent, noUpdatedOriComponent, noUpdatedRefComponent);

        smellRefactoredArea.setText(
                (updatedComponent.toString().equals("") ? "" : ConsolePrinter.SHORT_SEPARATOR + "被重构的组件" + ConsolePrinter.SHORT_SEPARATOR + "\n" + updatedComponent.toString())
                +
                (noUpdatedOriComponent.toString().equals("") ? "" : ConsolePrinter.SHORT_SEPARATOR + "有异味但未被重构的组件" + ConsolePrinter.SHORT_SEPARATOR + "\n" + noUpdatedOriComponent.toString())
                +
                (noUpdatedRefComponent.toString().equals("") ? "" : ConsolePrinter.SHORT_SEPARATOR + "组件重构前无异味且未被重构，但重构后引入了异味的组件（可能是其他组件重构导致）" + ConsolePrinter.SHORT_SEPARATOR + "\n" + noUpdatedRefComponent.toString())

        );
    }

    private void bfs(FileNode rootNode, StringBuilder updatedComponent, StringBuilder noUpdatedOriComponent, StringBuilder noUpdatedRefComponent) {
        if (!rootNode.isDirectory()) {
            return;
        }
        if (rootNode.isComponent()) {
            boolean updated = rootNode.isUpdated();
            boolean originHubLike = rootNode.isOriginHubLike();
            boolean originUnstable = rootNode.isOriginUnstable();
            boolean originCyclic = rootNode.isOriginCyclic();

            boolean refactoredHubLike = rootNode.isRefactoredHubLike();
            boolean refactoredUnstable = rootNode.isRefactoredUnstable();
            boolean refactoredCyclic = rootNode.isRefactoredCyclic();
            // 若 组件被重构
            if (updated) {
                updatedComponent.append(rootNode.getCompleteName() + " 组件" + "\n");
                updatedComponent.append(originHubLike ?
                                        refactoredHubLike?
                                                "- 枢纽型异味未解决\n" :
                                                "- 枢纽型异味已解决\n" :
                                        refactoredHubLike ?
                                                "- 引入了枢纽型异味\n" :
                                                "");
                updatedComponent.append(originUnstable ?
                                    refactoredUnstable ?
                                            "- 不稳定异味未解决\n" :
                                            "- 不稳定异味已解决\n" :
                                    refactoredUnstable ?
                                            "- 引入了不稳定异味\n" :
                                            "");
                updatedComponent.append(originCyclic ?
                                    refactoredCyclic ?
                                            "- 环依赖异味未解决\n" :
                                            "- 环依赖异味已解决\n" :
                                    refactoredCyclic ?
                                            "- 引入了环依赖异味\n" :
                                            "");

                updatedComponent.append(!(originHubLike || originUnstable || originCyclic) && !(refactoredHubLike || refactoredUnstable || refactoredCyclic) ?
                                    "重构前后都无异味\n" :
                                    "");
                updatedComponent.append("\n");
            }
            // 若 组件未被重构 && 重构前有异味
            else if (originHubLike || originUnstable || originCyclic) {
                noUpdatedOriComponent.append(rootNode.getCompleteName() + " 组件" + "\n");
                noUpdatedOriComponent.append(originHubLike ?
                                    refactoredHubLike?
                                            "枢纽型异味未解决\n" :
                                            "枢纽型异味已解决\n" :
                                    refactoredHubLike ?
                                            "引入了枢纽型异味\n" :
                                            "");
                noUpdatedOriComponent.append(originUnstable ?
                                    refactoredUnstable ?
                                            "不稳定异味未解决\n" :
                                            "不稳定异味已解决\n" :
                                    refactoredUnstable ?
                                            "引入了不稳定异味\n" :
                                            "");
                noUpdatedOriComponent.append(originCyclic ?
                                    refactoredCyclic ?
                                            "环依赖异味未解决\n" :
                                            "环依赖异味已解决\n" :
                                    refactoredCyclic ?
                                            "引入了环依赖异味\n" :
                                            "");
                noUpdatedOriComponent.append("\n");
            }
            // 若 组件未被重构 && 重构前无异味 && 重构后有异味
            else if (refactoredHubLike || refactoredUnstable || refactoredCyclic) {
                noUpdatedRefComponent.append(rootNode.getCompleteName() + " 组件" + "\n");
                noUpdatedRefComponent.append(originHubLike ?
                                    refactoredHubLike?
                                            "枢纽型异味未解决\n" :
                                            "枢纽型异味已解决\n" :
                                    refactoredHubLike ?
                                            "引入了枢纽型异味\n" :
                                            "");
                noUpdatedRefComponent.append(originUnstable ?
                                    refactoredUnstable ?
                                            "不稳定异味未解决\n" :
                                            "不稳定异味已解决\n" :
                                    refactoredUnstable ?
                                            "引入了不稳定异味\n" :
                                            "");
                noUpdatedRefComponent.append(originCyclic ?
                                    refactoredCyclic ?
                                            "环依赖异味未解决\n" :
                                            "环依赖异味已解决\n" :
                                    refactoredCyclic ?
                                            "引入了环依赖异味\n" :
                                            "");
                noUpdatedRefComponent.append("\n");
            }
        }
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            FileNode fileNode = (FileNode)rootNode.getChildAt(i);
            bfs(fileNode, updatedComponent, noUpdatedOriComponent, noUpdatedRefComponent);
        }
    }


    private void initRefactorsArea(List<String> refactors) {
        if (refactors == null || refactors.size() == 0) {
            return;
        }

        Map<String, String> moveToComponent = new HashMap<>();
        Map<String, String> moveFromComponent = new HashMap<>();
        Map<String, String> extractToComponent = new HashMap<>();
        Map<String, String> extractFromComponent = new HashMap<>();

        for (String refactor : refactors) {
            String[] actions = refactor.split(RefactorConstant.ACTION_SEPARATOR);
            switch (actions[0]) {
                case RefactorConstant.MOVE_FILE:
                    if (moveToComponent.containsKey(actions[2])) {
                        continue;
                    }
                    moveToComponent.put(extractCompleteNameFromId(actions[2]), extractCompleteNameFromId(actions[3]));
                    moveFromComponent.put(extractCompleteNameFromId(actions[2]), extractCompleteNameFromId(actions[1]));
                    break;
                case RefactorConstant.EXTRACT_COMPONENT:
                    String[] fileComponentIds = actions[1].split(RefactorConstant.CONTENT_SEPARATOR);
                    for (String fileComponentId : fileComponentIds) {
                        String[] fileComponent = fileComponentId.split(RefactorConstant.BELONG_SEPARATOR);
                        if (extractToComponent.containsKey(fileComponent[0])) {
                            continue;
                        }
                        extractToComponent.put(extractCompleteNameFromId(fileComponent[0]), extractCompleteNameFromId(actions[2]));
                        extractFromComponent.put(extractCompleteNameFromId(fileComponent[0]), extractCompleteNameFromId(fileComponent[1]));
                    }
                    break;
            }
        }

        Map<String, List<String>> refactoredComponents = new HashMap<>();
        for (String refactoredCompleteFileName : moveFromComponent.keySet()) {
            String refactoredComponentName = moveFromComponent.get(refactoredCompleteFileName);
            List<String> refactoredFileNames = refactoredComponents.getOrDefault(refactoredComponentName, new ArrayList<>());
            refactoredFileNames.add(refactoredCompleteFileName);
            refactoredComponents.put(refactoredComponentName, refactoredFileNames);
        }
        for (String refactoredCompleteFileName : extractFromComponent.keySet()) {
            String refactoredComponentName = extractFromComponent.get(refactoredCompleteFileName);
            List<String> refactoredFileNames = refactoredComponents.getOrDefault(refactoredComponentName, new ArrayList<>());
            refactoredFileNames.add(refactoredCompleteFileName);
            refactoredComponents.put(refactoredComponentName, refactoredFileNames);
        }


        StringBuilder refactorsInfo = new StringBuilder();
        refactorsInfo.append("重构文件总个数: " + moveToComponent.size() + "\n");
        for (String refactoredComponentName : refactoredComponents.keySet()) {
            refactorsInfo.append("对 " + refactoredComponentName + " 组件中以下文件进行重构" + ConsolePrinter.SHORT_SEPARATOR + "\n");
            List<String> refactoredFileNames = refactoredComponents.get(refactoredComponentName);
            for (int i = 0; i < refactoredFileNames.size(); i++) {
                if (moveToComponent.containsKey(refactoredFileNames.get(i))) {
                    refactorsInfo.append("- 移动 " + extractCurrentPath(refactoredFileNames.get(i)) + ".java 到 " + moveToComponent.get(refactoredFileNames.get(i)) + " 组件中\n");
                } else {
                    refactorsInfo.append("- 抽取 " + extractCurrentPath(refactoredFileNames.get(i)) + ".java 到 " + extractToComponent.get(refactoredFileNames.get(i)) + " 组件中\n");
                }
            }
            refactorsInfo.append("\n");
        }

        refactorsArea.setText(refactorsInfo.toString().substring(0, refactorsInfo.length() - 2));
    }

    /**
     * a.b.C => a.b
     * @param completeFileName
     * @return
     */
    private String extractComponentName(String completeFileName) {
        if (!completeFileName.contains(".")) {
            return completeFileName;
        }
        return completeFileName.substring(0, completeFileName.lastIndexOf("."));
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
     * 将 a.b.C ~ FILE ~ SYSTEM 转换为 C
     * @param nodeId
     * @return
     */
    private String extractNameFromId(String nodeId) {
        return extractCurrentPath(nodeId.split(NodeConstant.SEPARATOR)[0]);
    }

    /**
     * 将 a.b.c ~ FILE ~ SYSTEM 转换为 a.b.c
     * @param nodeId
     * @return
     */
    private String extractCompleteNameFromId(String nodeId) {
        return nodeId.split(NodeConstant.SEPARATOR)[0];
    }


    /**
     * 将一组Node转换为一组nodeIds返回
     * - 若nodes为null，则返回null
     * @param nodes
     * @return
     */
    private List<String> nodes2Ids(Set<Node> nodes) {
        if (nodes == null) {
            return null;
        }
        List<String> nodeIds = new ArrayList<>();
        for (Node node : nodes) {
            nodeIds.add(node.getId());
        }
        return nodeIds;
    }

    /**
     * 将一组Node转换为一组nodeNames返回
     * - 若nodes为null，则返回null
     * @param nodes
     * @return
     */
    private Set<String> nodes2Names(Set<Node> nodes) {
        if (nodes == null) {
            return null;
        }
        Set<String> nodeNames = new HashSet<>();
        for (Node node : nodes) {
            nodeNames.add(node.getName());
        }
        return nodeNames;
    }

    private void initPanel() {
        Box verticalBox = Box.createVerticalBox();
        JPanel refactorObjectivePanel = buildPanel("重构前后耦合内聚信息", this.refactorObjectiveArea);
        JPanel refactoredSmellEffectPanel = buildPanel("重构前后异味信息", this.refactoredSmellEffectArea);
        JPanel refactorsPanel = buildPanel("重构操作建议", this.refactorsArea);
        JPanel smellRefactoredPanel = buildPanel("异味重构信息", this.smellRefactoredArea);
        verticalBox.add(refactorObjectivePanel);
        verticalBox.add(refactoredSmellEffectPanel);
        verticalBox.add(refactorsPanel);
        verticalBox.add(smellRefactoredPanel);
        this.setViewportView(verticalBox);
        this.getVerticalScrollBar().setUnitIncrement(8);
    }

    private JPanel buildPanel(String title, JTextArea area) {
        JPanel refactoredPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Dialog",Font.BOLD, 15));
        refactoredPanel.add(titleLabel, BorderLayout.NORTH);
        refactoredPanel.add(area, BorderLayout.CENTER);
        return refactoredPanel;
    }

    private void initArea() {
        this.refactorsArea = new JTextArea();
        refactorsArea.setEditable(false);

        this.smellRefactoredArea = new JTextArea();
        smellRefactoredArea.setEditable(false);

        this.refactoredSmellEffectArea = new JTextArea();
        refactoredSmellEffectArea.setEditable(false);

        this.refactorObjectiveArea = new JTextArea();
        refactorObjectiveArea.setEditable(false);
    }
}
