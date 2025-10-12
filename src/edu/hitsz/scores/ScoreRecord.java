package edu.hitsz.scores;

import java.io.*;
import java.util.*;

/**
 * 分数记录类，用于存储单条分数记录
 */
public class ScoreRecord implements Serializable, Comparable<ScoreRecord> {
    private static final long serialVersionUID = 1L;
    
    private String playerName;
    private int score;
    private Date date;

    public ScoreRecord(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
        this.date = new Date();
    }

    public ScoreRecord(String playerName, int score, Date date) {
        this.playerName = playerName;
        this.score = score;
        this.date = (date != null) ? new Date(date.getTime()) : new Date();
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public Date getDate() {
        return new Date(date.getTime());
    }

    @Override
    public int compareTo(ScoreRecord other) {
        // 按分数降序排列
        return Integer.compare(other.score, this.score);
    }

    @Override
    public String toString() {
        return String.format("%-10s %-10d %s", playerName, score, date);
    }
}