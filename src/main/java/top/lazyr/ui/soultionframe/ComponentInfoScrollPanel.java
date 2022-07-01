package top.lazyr.ui.soultionframe;

import top.lazyr.constant.ConsolePrinter;
import top.lazyr.constant.NodeConstant;
import top.lazyr.util.ArrayUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/2/2
 */
public class ComponentInfoScrollPanel extends JScrollPane {
    // 是否重构
    private JTextArea updatedArea;
    // 异味信息
    private JTextArea smellArea;
    // 传入节点信息
    private JTextArea afferentArea;
    // 传出节点信息
    private JTextArea efferentArea;
    // 组件内文件列表信息
    private JTextArea fileArea;


    public ComponentInfoScrollPanel() {
        initArea();
        init();
    }

    private void initArea() {
        updatedArea = new JTextArea();
        updatedArea.setEditable(false);

        fileArea = new JTextArea();
        fileArea.setEditable(false);

        smellArea = new JTextArea();
        smellArea.setEditable(false);

        afferentArea = new JTextArea();
        afferentArea.setEditable(false);

        efferentArea = new JTextArea();
        efferentArea.setEditable(false);

    }

    private void init() {
        Box verticalBox = Box.createVerticalBox();

        JPanel updatedPanel = buildPanel("是否被重构", updatedArea);
        verticalBox.add(updatedPanel);

        JPanel smellPanel = buildPanel("异味重构情况", smellArea);
        verticalBox.add(smellPanel);

        Box afferentEfferentPanel = initAfferentEfferentPanel();
        verticalBox.add(afferentEfferentPanel);

        JPanel filePanel = buildPanel("文件信息", fileArea);
        verticalBox.add(filePanel);

        this.setViewportView(verticalBox);
        this.getVerticalScrollBar().setUnitIncrement(8);
    }


    private Box initAfferentEfferentPanel() {
        Box verticalBox = Box.createVerticalBox();

        JPanel afferentPanel = buildPanel("传入节点", afferentArea);
        JPanel efferentPanel = buildPanel("传出节点", efferentArea);
        verticalBox.add(afferentPanel);
        verticalBox.add(efferentPanel);

        return verticalBox;
    }

    private JPanel buildPanel(String title, JTextArea area) {
        JPanel refactoredPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Dialog",Font.BOLD, 15));
        refactoredPanel.add(titleLabel, BorderLayout.NORTH);
        refactoredPanel.add(area, BorderLayout.CENTER);
        return refactoredPanel;
    }

    public void updateInfo(FileNode catalogNode) {

        // 更新是否被重构信息
        boolean updated = catalogNode.isUpdated();
        updatedArea.setText(updated ? "是" : "否");

        // 更新异味重构信息
        boolean originHubLike = catalogNode.isOriginHubLike();
        boolean refactoredHubLike = catalogNode.isRefactoredHubLike();
        boolean originUnstable = catalogNode.isOriginUnstable();
        boolean refactoredUnstable = catalogNode.isRefactoredUnstable();
        boolean originCyclic = catalogNode.isOriginCyclic();
        boolean refactoredCyclic = catalogNode.isRefactoredCyclic();
        smellArea.setText(
                            "枢纽型异味: " + (originHubLike ? "有" : "无") + " ==重构后==> " + (refactoredHubLike ? "有" : "无") + "\n" +
                            "不稳定异味: " + (originUnstable ? "有" : "无")  + " ==重构后==> " + (refactoredUnstable ? "有" : "无") + "\n" +
                            "环依赖异味: " + (originCyclic ? "有" : "无")  + " ==重构后==> " + (refactoredCyclic ? "有" : "无")
                            );

        // 更新文件列表信息
        StringBuilder movedFiles = new StringBuilder();
        StringBuilder createdFiles = new StringBuilder();
        StringBuilder noRefactoredFiles = new StringBuilder();

        for (int i = 0; i < catalogNode.getChildCount(); i++) {
            FileNode fileNode = (FileNode)catalogNode.getChildAt(i);
            if (fileNode.isCreated()) {
                createdFiles.append("- " + fileNode.getName() + ".java (来自 " + fileNode.getOriginCatalog() + " 组件)\n");
            } else if (fileNode.isMoved()) {
                movedFiles.append("- " + fileNode.getName() + ".java (已移动到 " + fileNode.getRefactoredCatalog() + " 组件中)\n");
            } else {
                noRefactoredFiles.append("- " + fileNode.getName() + ".java\n");
            }
        }
        if (movedFiles.lastIndexOf("\n") != -1) {
            movedFiles.deleteCharAt(movedFiles.lastIndexOf("\n"));
        }
        if (createdFiles.lastIndexOf("\n") != -1) {
            createdFiles.deleteCharAt(createdFiles.lastIndexOf("\n"));
        }
        if (noRefactoredFiles.lastIndexOf("\n") != -1) {
            noRefactoredFiles.deleteCharAt(noRefactoredFiles.lastIndexOf("\n"));
        }

        fileArea.setText((movedFiles.toString().equals("") ? "" : movedFiles.toString() + "\n") +
                        (createdFiles.toString().equals("") ? "" : createdFiles.toString() + "\n") +
                        (noRefactoredFiles.toString().equals("") ? "" : noRefactoredFiles.toString()));

        // 更新传入节点信息
        List<String> originAfferentIds = catalogNode.getOriginAfferentIds();
        List<String> refactoredAfferentIds = catalogNode.getRefactoredAfferentIds();
        List<List<String>> afferentDiff = ArrayUtil.diff(originAfferentIds, refactoredAfferentIds);
        List<String> vanishedAfferentIds = afferentDiff.get(0);
        List<String> newAfferentIds = afferentDiff.get(1);
        List<String> noRefactoredAfferentIds = afferentDiff.get(2);

        List<List<String>> classifiedVanishedAfferentIds = classifiedByNodeIds(vanishedAfferentIds);
        List<List<String>> classifiedNewAfferentStr  = classifiedByNodeIds(newAfferentIds);
        List<List<String>> classifiedNoRefactoredAfferentStr = classifiedByNodeIds(noRefactoredAfferentIds);

        String systemVanishedAfferentStr = formatList(classifiedVanishedAfferentIds.get(0), "(删除)");
        String systemNewAfferentStr = formatList(classifiedNewAfferentStr.get(0), "(添加)");
        String systemNoRefactoredAfferentStr = formatList(classifiedNoRefactoredAfferentStr.get(0), "");
        String nonSystemVanishedAfferentStr = formatList(classifiedVanishedAfferentIds.get(1), "(删除)");
        String nonSystemNewAfferentStr = formatList(classifiedNewAfferentStr.get(1), "(添加)");
        String nonSystemNoRefactoredAfferentStr = formatList(classifiedNoRefactoredAfferentStr.get(1), "");

        String systemAfferentStr = (systemVanishedAfferentStr.equals("") ? "" : "\n" + systemVanishedAfferentStr) +
                                    (systemNewAfferentStr.equals("") ? "" : "\n" + systemNewAfferentStr) +
                                    (systemNoRefactoredAfferentStr.equals("") ? "" : "\n" + systemNoRefactoredAfferentStr);
        String nonSystemAfferentStr = (nonSystemVanishedAfferentStr.equals("") ? "" : "\n" + nonSystemVanishedAfferentStr) +
                                        (nonSystemNewAfferentStr.equals("") ? "" : "\n" + nonSystemNewAfferentStr) +
                                        (nonSystemNoRefactoredAfferentStr.equals("") ? "" : "\n" + nonSystemNoRefactoredAfferentStr);



        afferentArea.setText(
                ConsolePrinter.SHORT_SEPARATOR + "系统内组件" + ConsolePrinter.SHORT_SEPARATOR +
                (systemAfferentStr.equals("") ? "\n无" : systemAfferentStr) +
                "\n" + ConsolePrinter.SHORT_SEPARATOR + "系统外组件" + ConsolePrinter.SHORT_SEPARATOR +
                (nonSystemAfferentStr.equals("") ? "\n无" : nonSystemAfferentStr));

        // 更新传出节点信息
        List<String> originEfferentIds = catalogNode.getOriginEfferentIds();
        List<String> refactoredEfferentIds = catalogNode.getRefactoredEfferentIds();
        List<List<String>> efferentDiff = ArrayUtil.diff(originEfferentIds, refactoredEfferentIds);
        List<String> vanishedEfferentIds = efferentDiff.get(0);
        List<String> newEfferentIds = efferentDiff.get(1);
        List<String> noRefactoredEfferentIds = efferentDiff.get(2);

        List<List<String>> classifiedVanishedEfferentIds = classifiedByNodeIds(vanishedEfferentIds);
        List<List<String>> classifiedNewEfferentStr  = classifiedByNodeIds(newEfferentIds);
        List<List<String>> classifiedNoRefactoredEfferentStr = classifiedByNodeIds(noRefactoredEfferentIds);


        String systemVanishedEfferentStr = formatList(classifiedVanishedEfferentIds.get(0), "(删除)");
        String systemNewEfferentStr = formatList(classifiedNewEfferentStr.get(0), "(添加)");
        String systemNoRefactoredEfferentStr = formatList(classifiedNoRefactoredEfferentStr.get(0), "");
        String nonSystemVanishedEfferentStr = formatList(classifiedVanishedEfferentIds.get(1), "(删除)");
        String nonSystemNewEfferentStr = formatList(classifiedNewEfferentStr.get(1), "(添加)");
        String nonSystemNoRefactoredEfferentStr = formatList(classifiedNoRefactoredEfferentStr.get(1), "");

        String systemEfferentStr = (systemVanishedEfferentStr.equals("") ? "" : "\n" + systemVanishedEfferentStr) +
                (systemNewEfferentStr.equals("") ? "" : "\n" + systemNewEfferentStr) +
                (systemNoRefactoredEfferentStr.equals("") ? "" : "\n" + systemNoRefactoredEfferentStr);
        String nonSystemEfferentStr = (nonSystemVanishedEfferentStr.equals("") ? "" : "\n" + nonSystemVanishedEfferentStr) +
                (nonSystemNewEfferentStr.equals("") ? "" : "\n" + nonSystemNewEfferentStr) +
                (nonSystemNoRefactoredEfferentStr.equals("") ? "" : "\n" + nonSystemNoRefactoredEfferentStr);



        efferentArea.setText(
                ConsolePrinter.SHORT_SEPARATOR + "系统内组件" + ConsolePrinter.SHORT_SEPARATOR +
                        (systemEfferentStr.equals("") ? "\n无" : systemEfferentStr) +
                        "\n" + ConsolePrinter.SHORT_SEPARATOR + "系统外组件" + ConsolePrinter.SHORT_SEPARATOR +
                        (nonSystemEfferentStr.equals("") ? "\n无" : nonSystemEfferentStr));

    }

    /**
     * 将nodeIds分成项目内和项目外两组节点, 若nodeIds为null则返回两个size=0的List
     * 返回
     *  [
     *      // 项目内节点id
     *      [],
     *      // 项目外节点id
     *      []
     *  ]
     * @param nodeIds
     * @return
     */
    private List<List<String>> classifiedByNodeIds(List<String> nodeIds) {
        List<List<String>> classifiedNodeIds = new ArrayList<>();
        List<String> systemNodeIds = new ArrayList<>();
        List<String> nonSystemNodeIds = new ArrayList<>();
        classifiedNodeIds.add(systemNodeIds);
        classifiedNodeIds.add(nonSystemNodeIds);

        if (nodeIds == null) {
            return classifiedNodeIds;
        }

        for (String nodeId : nodeIds) {
            if (nodeId.contains(NodeConstant.FROM_NON_SYSTEM)) {
                nonSystemNodeIds.add(nodeId);
            } else {
                systemNodeIds.add(nodeId);
            }
        }
        return classifiedNodeIds;
    }

    /**
     * 将 [a.b ~ COMPONENT ~ SYSTEM, a.c ~ COMPONENT ~ NON_SYSTEM] 转换为 - a.bsuffix\n- a.c+suffix
     * 将 [] 转换为 ""
     * @param nodeIds
     * @return
     */
    private String formatList(List<String> nodeIds, String suffix) {
        if (nodeIds == null || nodeIds.size() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < nodeIds.size(); i++) {
            builder.append("- " + nodeIds.get(i).split(NodeConstant.SEPARATOR)[0] + suffix);
            if (i != nodeIds.size() - 1) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }
}
