package edu.hitsz.application.game;

import edu.hitsz.application.ImageManager;
import edu.hitsz.scores.LeaderboardManager;

public class MediumGame extends Game {
    public MediumGame() {
        targetRankPage = "MEDIUM_RANK";
        level = 2;
        leaderboardManager = new LeaderboardManager(level);
        // 设置中等难度背景图片
        backgroundImage = ImageManager.BACKGROUND_IMAGE_MEDIUM;
    }

    @Override
    public void run() {
        action();
    }
}