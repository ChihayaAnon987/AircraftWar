package edu.hitsz.scores;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 排行榜管理类
 */
public class LeaderboardManager {
    private static final String SCORE_FILE = "scores.txt"; // 改为txt文件
    private static final int MAX_RECORDS = 20; // 只保留前20名记录
    private List<ScoreRecord> records;

    public LeaderboardManager() {
        records = new ArrayList<>();
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
     * 从文件加载分数记录
     */
    private void loadScores() {
        try (BufferedReader br = new BufferedReader(new FileReader(SCORE_FILE))) {
            String line;
            records = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                // 解析每行数据：玩家名,分数,日期
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    try {
                        String playerName = parts[0];
                        int score = Integer.parseInt(parts[1]);
                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                        Date date = sdf.parse(parts[2]);
                        records.add(new ScoreRecord(playerName, score, date));
                    } catch (Exception e) {
                        System.err.println("记录解析错误: " + e.getMessage());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // 文件不存在，创建新文件
            saveScores(); // 创建空文件
        } catch (IOException e) {
            System.err.println("加载分数记录时出错: " + e.getMessage());
            records = new ArrayList<>();
        }
    }

    /**
     * 将分数记录保存到文件
     */
    private void saveScores() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(SCORE_FILE))) {
            for (ScoreRecord record : records) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                pw.printf("%s,%d,%s\n", 
                    record.getPlayerName(), 
                    record.getScore(), 
                    sdf.format(record.getDate()));
            }
        } catch (IOException e) {
            System.err.println("保存分数记录时出错: " + e.getMessage());
        }
    }
}