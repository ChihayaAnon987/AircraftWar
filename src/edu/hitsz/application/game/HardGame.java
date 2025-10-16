package edu.hitsz.application.game;

import edu.hitsz.application.ImageManager;
import edu.hitsz.scores.LeaderboardManager;

public class HardGame extends Game {
    public HardGame() {
        targetRankPage = "HARD_RANK";
        level = 3;
        leaderboardManager = new LeaderboardManager(level);
        // 设置困难难度背景图片
        backgroundImage = ImageManager.BACKGROUND_IMAGE_HARD;
    }
}