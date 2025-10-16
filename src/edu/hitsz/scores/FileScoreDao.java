package edu.hitsz.scores;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 基于文件的分数记录数据访问对象实现
 */
public class FileScoreDao implements ScoreDao {
    private static final String EASY_SCORE_FILE = "easy_scores.txt";
    private static final String MEDIUM_SCORE_FILE = "medium_scores.txt";
    private static final String HARD_SCORE_FILE = "hard_scores.txt";

    private String getScoreFileByLevel(int level) {
        switch (level) {
            case 1: return EASY_SCORE_FILE;
            case 2: return MEDIUM_SCORE_FILE;
            case 3: return HARD_SCORE_FILE;
            default: return EASY_SCORE_FILE;
        }
    }

    @Override
    public void saveScores(List<ScoreRecord> records, int level) {
        String scoreFile = getScoreFileByLevel(level);
        try (PrintWriter pw = new PrintWriter(new FileWriter(scoreFile))) {
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

    @Override
    public List<ScoreRecord> loadScores(int level) {
        String scoreFile = getScoreFileByLevel(level);
        List<ScoreRecord> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(scoreFile))) {
            String line;
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
            // 文件不存在，返回空列表
            return records;
        } catch (IOException e) {
            System.err.println("加载分数记录时出错: " + e.getMessage());
        }
        return records;
    }
}