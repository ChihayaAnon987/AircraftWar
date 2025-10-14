package edu.hitsz.scores;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 排行榜管理类
 */
public class LeaderboardManager {
    private static final int MAX_RECORDS = 20; // 只保留前20名记录
    private List<ScoreRecord> records;
    private ScoreDao scoreDao;

    public LeaderboardManager() {
        records = new ArrayList<>();
        scoreDao = new FileScoreDao(); // 使用文件数据访问对象
        loadScores();
    }

    /**
     * 添加新分数记录
     * @param playerName 玩家姓名
     * @param score 分数
     */
    public void addRecord(String playerName, int score) {
        records.add(new ScoreRecord(playerName, score));
        Collections.sort(records);
        
        // 只保留前MAX_RECORDS名记录
        if (records.size() > MAX_RECORDS) {
            records = records.subList(0, MAX_RECORDS);
        }
        
        saveScores();
    }

    /**
     * 显示排行榜
     */
    public void displayLeaderboard() {
        System.out.println("\n****************************************");
        System.out.println("                得分排行榜");
        System.out.println("****************************************");
        
        if (records.isEmpty()) {
            System.out.println("第1名: testUserName,0,01-20 17:15");
        } else {
            for (int i = 0; i < records.size(); i++) {
                ScoreRecord record = records.get(i);
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                System.out.printf("第%d名: %s,%d,%s%n", 
                    i + 1, 
                    record.getPlayerName(), 
                    record.getScore(), 
                    sdf.format(record.getDate()));
            }
        }
        System.out.println("Game Over.");
    }
    
    /**
     * 获取下一个匿名玩家名称
     * @return 匿名玩家名称，如"匿名玩家1"、"匿名玩家2"等
     */
    public String getNextAnonymousName() {
        int anonymousCount = 1;
        Set<String> existingNames = new HashSet<>();
        
        // 收集所有已存在的玩家名称
        for (ScoreRecord record : records) {
            existingNames.add(record.getPlayerName());
        }
        
        // 查找第一个未使用的匿名玩家名称
        String anonymousName;
        do {
            anonymousName = "匿名玩家" + anonymousCount;
            anonymousCount++;
        } while (existingNames.contains(anonymousName));
        
        return anonymousName;
    }

    /**
     * 从数据源加载分数记录
     */
    private void loadScores() {
        records = scoreDao.loadScores();
    }

    /**
     * 将分数记录保存到数据源
     */
    private void saveScores() {
        scoreDao.saveScores(records);
    }
}