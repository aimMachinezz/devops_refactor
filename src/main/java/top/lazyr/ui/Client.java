/*
 * Created by JFormDesigner on Tue Mar 22 13:38:43 CST 2022
 */

package top.lazyr.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author unknown
 */
public class Client extends JFrame{
    static TrayIcon trayIcon = null; // 托盘图标
    static SystemTray tray = null; // 本操作系统托盘的实例

    Client() {
        if (SystemTray.isSupported()) // 如果操作系统支持托盘
        {
            this.tray();
        }
        this.setSize(300, 200);
        this.setVisible(true);
        // 窗口关闭时触发事件
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

            public void windowIconified(WindowEvent e) {
                try {
                    tray.add(trayIcon); // 将托盘图标添加到系统的托盘实例中
                    // setVisible(false); // 使窗口不可视
                    dispose();
                } catch (AWTException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    void tray() {
        tray = SystemTray.getSystemTray(); // 获得本操作系统托盘的实例
        ImageIcon icon = new ImageIcon("wifi.png"); // 将要显示到托盘中的图标
        PopupMenu pop = new PopupMenu(); // 构造一个右键弹出式菜单
        MenuItem show = new MenuItem("打开程序(s)");
        MenuItem exit = new MenuItem("退出程序(x)");
        pop.add(show);
        pop.add(exit);
        trayIcon = new TrayIcon(icon.getImage(), "开心农场收菜工+工厂清理工 V1.5", pop);
        // 添加鼠标监听器，当鼠标在托盘图标上双击时，默认显示窗口
        trayIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) // 鼠标双击
                {
                    // tray.remove(trayIcon); // 从系统的托盘实例中移除托盘图标
                    setExtendedState(JFrame.NORMAL);
                    setVisible(true); // 显示窗口
                    toFront();
                }
            }
        });
        show.addActionListener(new ActionListener() // 点击“显示窗口”菜单后将窗口显示出来
        {
            public void actionPerformed(ActionEvent e) {
                // tray.remove(trayIcon); // 从系统的托盘实例中移除托盘图标
                setExtendedState(JFrame.NORMAL);
                setVisible(true); // 显示窗口
                toFront();
            }
        });
        exit.addActionListener(new ActionListener() // 点击“退出演示”菜单后退出程序
        {
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // 退出程序
            }
        });
    }

    public static void main(String[] args) throws Exception {
        new Client();
    }


}
