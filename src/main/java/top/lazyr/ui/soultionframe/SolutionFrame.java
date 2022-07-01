package top.lazyr.ui.soultionframe;


import top.lazyr.architecture.diagram.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyr
 * @created 2022/2/7
 */
public class SolutionFrame {
    private List<JPanel> solutionPanels;
    private CardLayout cardLayout;
    private JPanel solutionShowPanel;
    private JPanel controlPanel;
    private JLabel titleLabel;
    private JPanel titlePanel;
    private JFrame frame;


    public SolutionFrame(Graph originGraph, List<Graph> refactoredGraphs, List<List<String>> refactorsList) {
        initSolutionPanels(originGraph, refactoredGraphs, refactorsList);
        initSolutionShowPanel();
        initControlPanel();
        initTitlePanel();
        initLayout(refactoredGraphs.size());
    }

    private void initTitlePanel() {
        this.titleLabel = new JLabel("solution0", JLabel.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 15));
        this.titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.CENTER);
    }

    private void initLayout(int solutionNum) {
        frame = new JFrame(solutionNum + "个pareto最优解");
        frame.setLayout(new BorderLayout());
        frame.add(titlePanel, BorderLayout.NORTH);
        frame.add(solutionShowPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);
        frame.setSize(1200 , 800);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(100, 0);
    }

    private void initControlPanel() {
        this.controlPanel = new JPanel(new GridLayout(1, 2));
        JButton pre = new JButton("上一个");
        JButton next = new JButton("下一个");
        this.controlPanel.add(pre);
        this.controlPanel.add(next);

        pre.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.previous(solutionShowPanel);
                updateTitle();
            }
        });

        next.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.next(solutionShowPanel);
                updateTitle();
            }
        });
    }

    private void initSolutionShowPanel() {
        this.cardLayout = new CardLayout();
        this.solutionShowPanel = new JPanel(cardLayout);
        for (int i = 0; i < this.solutionPanels.size(); i++) {
            this.solutionShowPanel.add(solutionPanels.get(i), "solution" + i);
        }
        cardLayout.show(this.solutionShowPanel, "solution0");
    }

    private void initSolutionPanels(Graph originGraph, List<Graph> refactoredGraphs, List<List<String>> refactorsList) {
        this.solutionPanels = new ArrayList<>();
        for (int i = 0; i < refactoredGraphs.size(); i++) {
            solutionPanels.add(new SolutionPanel(originGraph, refactoredGraphs.get(i), refactorsList.get(i)));
        }
    }


    private void updateTitle() {
        for(int i = 0; i < solutionShowPanel.getComponentCount(); i++){
            Component c = solutionShowPanel.getComponent(i);
            if (c.isVisible()) {
                titleLabel.setText("solution" + i);
                break;
            }
        }
    }
}
