package edu.hitsz.application;

import edu.hitsz.application.swing.Menu;
import edu.hitsz.application.swing.RankList;

import javax.swing.*;
import java.awt.*;

/**
 * 程序入口
 * @author hitsz
 */
public class Main {

    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;
    public static CardLayout cardLayout;
    public static JPanel cardPanel;
    
    // 保存排行榜实例的引用，以便在需要时刷新
    public static RankList easyRank;
    public static RankList mediumRank;
    public static RankList hardRank;

    public static void main(String[] args) {

        System.out.println("Hello Aircraft War");

        // 获得屏幕的分辨率，初始化 Frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new JFrame("Aircraft War");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setResizable(false);
        //设置窗口的大小和位置,居中放置
        frame.setBounds(((int) screenSize.getWidth() - WINDOW_WIDTH) / 2, 0,
                WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建卡片布局
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // 创建菜单面板
        Menu menu = new Menu();
        cardPanel.add(menu.getMainPanel(), "MENU");

        // 创建排行榜面板 (这里创建三个不同难度的排行榜)
        easyRank = new RankList(1);
        cardPanel.add(easyRank.getMainPanel(), "EASY_RANK");
        
        mediumRank = new RankList(2);
        cardPanel.add(mediumRank.getMainPanel(), "MEDIUM_RANK");
        
        hardRank = new RankList(3);
        cardPanel.add(hardRank.getMainPanel(), "HARD_RANK");

        frame.add(cardPanel);
        frame.setVisible(true);
        
        // 默认显示菜单
        cardLayout.show(cardPanel, "MENU");
    }
}