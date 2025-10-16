package edu.hitsz.application.swing;

import edu.hitsz.application.Main;
import edu.hitsz.application.game.EasyGame;
import edu.hitsz.application.game.HardGame;
import edu.hitsz.application.game.MediumGame;
import edu.hitsz.application.music.MusicPlayer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class Menu{
    private JButton easyButton;
    private JButton mediumButton;
    private JButton hardButton;
    private JComboBox musicCombo;
    private JLabel musicLabel;
    private JPanel easyPanel;
    private JPanel mediumPanel;
    private JPanel hardPanel;
    private JPanel musicPanel;
    private JPanel mainPanel;

    public Menu() {
        // 初始化所有组件
        easyButton = new JButton("简单模式");
        mediumButton = new JButton("普通模式");
        hardButton = new JButton("困难模式");
        musicCombo = new JComboBox();
        musicLabel = new JLabel("音效设置：");
        easyPanel = new JPanel();
        mediumPanel = new JPanel();
        hardPanel = new JPanel();
        musicPanel = new JPanel();
        mainPanel = new JPanel();
        // 移除了 rankButton 的初始化

        // 布局设置
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        easyPanel.add(easyButton);
        mediumPanel.add(mediumButton);
        hardPanel.add(hardButton);
        musicPanel.add(musicLabel);
        musicPanel.add(musicCombo);


        mainPanel.add(easyPanel);
        mainPanel.add(mediumPanel);
        mainPanel.add(hardPanel);
        mainPanel.add(musicPanel);
        // 移除了 rankPanel 的添加

        /*
         * 点击按钮，选择游戏难度
         */
        easyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 切换到游戏界面
                EasyGame game = new EasyGame();
                Main.cardPanel.add(game, "EASY_GAME");
                Main.cardLayout.show(Main.cardPanel, "EASY_GAME");
                game.action();
            }
        });
        mediumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 切换到游戏界面
                MediumGame game = new MediumGame();
                Main.cardPanel.add(game, "MEDIUM_GAME");
                Main.cardLayout.show(Main.cardPanel, "MEDIUM_GAME");
                game.action();
            }
        });
        hardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 切换到游戏界面
                HardGame game = new HardGame();
                Main.cardPanel.add(game, "HARD_GAME");
                Main.cardLayout.show(Main.cardPanel, "HARD_GAME");
                game.action();
            }
        });

        /*
         * 设置音效开启 /关闭
         */
        musicCombo.addItem("开启");
        musicCombo.addItem("关闭");
        musicCombo.setSelectedIndex(0); // 默认开启音效
        musicCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String item = e.getItem().toString();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (item.equals("关闭")) {
                        MusicPlayer.getMusicPlayer().noMusic();
                    } else if (item.equals("开启")) {
                        MusicPlayer.getMusicPlayer().enableMusic();
                    }
                }
            }
        });
    }
    public JPanel getMainPanel() {
        return mainPanel;
    }

}