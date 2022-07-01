package top.lazyr.ui.soultionframe;

import top.lazyr.constant.ConsolePrinter;
import top.lazyr.constant.NodeConstant;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/2/2
 */
public class FileInfoScrollPanel extends JScrollPane {
    private JTextArea currentCatalogArea;
    private JTextArea moveInfoArea;
    private JTextArea afferentArea;
    private JTextArea efferentArea;

    public FileInfoScrollPanel() {
        initInfoPanel();
        init();
    }

    private void init() {
        Box verticalBox = Box.createVerticalBox();

        JPanel catalogPanel = buildPanel("所在组件", currentCatalogArea);
        JPanel moveInfoPanel = buildPanel("移动信息", moveInfoArea);
        JPanel afferentPanel = buildPanel("传入节点", afferentArea);
        JPanel efferentPanel = buildPanel("传出节点", efferentArea);

        verticalBox.add(catalogPanel);
        verticalBox.add(moveInfoPanel);
        verticalBox.add(afferentPanel);
        verticalBox.add(efferentPanel);
        this.setViewportView(verticalBox);
        this.getVerticalScrollBar().setUnitIncrement(8);
    }

    private void initInfoPanel() {
        currentCatalogArea = new JTextArea();
        currentCatalogArea.setEditable(false);
        moveInfoArea = new JTextArea();
        moveInfoArea.setEditable(false);
        afferentArea = new JTextArea();
        afferentArea.setEditable(false);
        efferentArea = new JTextArea();
        efferentArea.setEditable(false);
    }

    private JPanel buildPanel(String title, JTextArea area) {
        JPanel refactoredPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Dialog",Font.BOLD, 15));
        refactoredPanel.add(titleLabel, BorderLayout.NORTH);
        refactoredPanel.add(area, BorderLayout.CENTER);
        return refactoredPanel;
    }

    public void updateInfo(FileNode fileNode) {
        currentCatalogArea.setText(fileNode.getRefactoredCatalog());
        boolean moved = fileNode.isMoved();
        boolean created = fileNode.isCreated();
        String moveInfo = moved ?
                                        created ?
                                                "" :
                                                "从当前组件移动到 " + fileNode.getRefactoredCatalog() + " 组件中" :
                                        created ?
                                                "从 " + fileNode.getOriginCatalog() + " 组件移动当前组件中" :
                                                "未被移动";
        moveInfoArea.setText(moveInfo);

        List<List<String>> classifiedAfferentIds = classifiedByNodeIds(fileNode.getOriginAfferentIds());
        List<List<String>> classifiedEfferentIds = classifiedByNodeIds(fileNode.getOriginEfferentIds());
        afferentArea.setText(
                ConsolePrinter.SHORT_SEPARATOR + "系统内文件" + ConsolePrinter.SHORT_SEPARATOR + "\n" +
                formatList(classifiedAfferentIds.get(0)) + "\n" +
                        ConsolePrinter.SHORT_SEPARATOR + "系统外文件" + ConsolePrinter.SHORT_SEPARATOR + "\n" +
                formatList(classifiedAfferentIds.get(1))
        );
        efferentArea.setText(
                ConsolePrinter.SHORT_SEPARATOR + "系统内文件" + ConsolePrinter.SHORT_SEPARATOR + "\n" +
                formatList(classifiedEfferentIds.get(0)) + "\n" +
                        ConsolePrinter.SHORT_SEPARATOR + "系统外文件" + ConsolePrinter.SHORT_SEPARATOR + "\n" +
                formatList(classifiedEfferentIds.get(1))
        );
    }

    /**
     * 将 [a.b ~ COMPONENT ~ SYSTEM, a.c ~ COMPONENT ~ NON_SYSTEM] 转换为 - a.b\n- a.c
     * 将 [] 转换为 ""
     * @param nodeIds
     * @return
     */
    private String formatList(List<String> nodeIds) {
        if (nodeIds == null || nodeIds.size() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < nodeIds.size(); i++) {
            builder.append("- " + nodeIds.get(i).split(NodeConstant.SEPARATOR)[0]);
            if (i != nodeIds.size() - 1) {
                builder.append("\n");
            }
        }
        return builder.toString();
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

}
