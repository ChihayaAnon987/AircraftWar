package edu.hitsz.application.game;

import edu.hitsz.application.ImageManager;
import edu.hitsz.scores.LeaderboardManager;

public class EasyGame extends Game {
    public EasyGame() {
        targetRankPage = "EASY_RANK";
        level = 1;
        leaderboardManager = new LeaderboardManager(level);
        // 设置简单难度背景图片
        backgroundImage = ImageManager.BACKGROUND_IMAGE_EASY;
    }

    @Override
    public void configureDifficulty() {
        // 简单模式配置
        enemyMaxNumber = 5;                 // 敌机最大数量
        cycleDuration = 600;                // 敌机产生周期
        bossScoreThreshold = Integer.MAX_VALUE;  // Boss敌机产生的分数阈值（永不产生）
        isBossEnabled = false;              // 不启用Boss敌机
        eliteEnemyProbability = 0.2;        // 精英敌机的产生概率
        superEliteEnemyProbability = 0.05;  // 超级精英敌机的产生概率
        isDifficultyIncreaseWithTime = false; // 难度不随时间增加
    }
    
    @Override
    public void adjustDifficultyWithTime(int time) {
        // 简单模式难度不随时间增加变化，不打印任何信息
    }

    @Override
    public void run() {
        action();
    }
}