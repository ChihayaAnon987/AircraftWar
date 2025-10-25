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
    public void run() {
        action();
    }
}