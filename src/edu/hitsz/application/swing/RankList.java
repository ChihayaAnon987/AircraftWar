package edu.hitsz.application.swing;

import edu.hitsz.application.Main;
import edu.hitsz.scores.FileScoreDao;
import edu.hitsz.scores.ScoreDao;
import edu.hitsz.scores.ScoreRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

public class RankList {
    private JPanel mainPanel;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JTable rankTable;
    private JButton deleteButton;
    private JLabel levelLabel;
    private JLabel rankLabel;
    private JScrollPane rankScroll;
    private List<ScoreRecord> records ;
    String[] columnNames = {"名次", "玩家名", "得分", "记录时间"};
    String[][] tableData = null;

    private ScoreDao scoreDao;
    DefaultTableModel model ;
    
    private int level; // 难度等级

    public RankList(int level) {
        this.level = level;
        
        // 初始化界面组件
        mainPanel = new JPanel();
        topPanel = new JPanel();
        bottomPanel = new JPanel();
        rankTable = new JTable();
        deleteButton = new JButton("删除");
        levelLabel = new JLabel();
        rankLabel = new JLabel("排行榜");
        rankScroll = new JScrollPane();
        
        // 布局设置
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        topPanel.add(rankLabel);
        topPanel.add(levelLabel);
        bottomPanel.add(deleteButton);
        
        mainPanel.add(topPanel);
        mainPanel.add(rankScroll);
        mainPanel.add(bottomPanel);
        
        if(level==1){
            levelLabel.setText("难度：EASY");
        }else if (level==2){
            levelLabel.setText("难度：MEDIUM");
        }else if (level==3){
            levelLabel.setText("难度：HARD");
        }
        scoreDao = new FileScoreDao();
        try {
            records = scoreDao.loadScores(level);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        showRankList(level);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = rankTable.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(null, "请选择要删除的行", "warning", JOptionPane.WARNING_MESSAGE);
                } else {
                    int result = JOptionPane.showConfirmDialog(null, "是否确认删除选中的玩家？", "选择一个选项", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        try {
                            records.remove(row);
                            scoreDao.saveScores(records, level);
                            showRankList(level);
                        } catch (Exception ioException) {
                            JOptionPane.showMessageDialog(null, "删除记录失败: " + ioException.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        
        // 添加返回按钮
        JButton backButton = new JButton("返回主菜单");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.cardLayout.show(Main.cardPanel, "MENU");
            }
        });
        bottomPanel.add(backButton);
    }

    private void getScoreTable(){
        if(records != null && !records.isEmpty()){
            tableData = new String[records.size()][4];
            for (int i = 0; i < records.size(); i++) {
                tableData[i][0] = String.valueOf(i + 1);
                tableData[i][1] = records.get(i).getPlayerName();
                tableData[i][2] = String.valueOf(records.get(i).getScore());
                tableData[i][3] = new SimpleDateFormat("MM-dd HH:mm").format(records.get(i).getDate());
            }
        }
        else {
            tableData = new String[0][4];
        }
    }
    
    public void refresh(int level) {
        try {
            records = scoreDao.loadScores(level);
            showRankList(level);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据游戏难度显示排行榜
     */
    public void showRankList(int level){
        // 每次显示前都重新加载数据
        try {
            records = scoreDao.loadScores(level);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "加载记录失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
        getScoreTable();
        model = new DefaultTableModel(tableData, columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        //从表格模型那里获取数据
        rankTable.setModel(model);
        rankScroll.setViewportView(rankTable);
    }
    
    /**
     * 刷新排行榜显示
     */
    public void refreshRankList() {
        showRankList(this.level);
    }
    
    public JPanel getMainPanel() {
        return mainPanel;
    }

    public int getLevel() {
        return level;
    }
}