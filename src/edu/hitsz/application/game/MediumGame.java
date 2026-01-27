package edu.hitsz.application.game;

import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.application.ImageManager;
import edu.hitsz.scores.LeaderboardManager;

public class MediumGame extends Game {
    private int initialCycleDuration = 600;     // 初始敌机产生周期
    private int minCycleDuration = 300;         // 最小敌机产生周期
    private double initialEliteProb = 0.3;      // 初始精英敌机概率
    private double maxEliteProb = 0.5;          // 最大精英敌机概率
    
    public MediumGame() {
        targetRankPage = "MEDIUM_RANK";
        level = 2;
        leaderboardManager = new LeaderboardManager(level);
        // 设置中等难度背景图片
        backgroundImage = ImageManager.BACKGROUND_IMAGE_MEDIUM;
    }

    @Override
    public void configureDifficulty() {
        // 中等模式配置
        enemyMaxNumber = 7;                     // 敌机最大数量
        cycleDuration = 600;                    // 初始敌机产生周期
        bossScoreThreshold = 300;               // Boss敌机产生的分数阈值
        isBossEnabled = true;                   // 启用Boss敌机
        bossHp = 500;                           // Boss敌机的血量
        eliteEnemyProbability = 0.3;            // 初始精英敌机的产生概率
        superEliteEnemyProbability = 0.1;       // 超级精英敌机的产生概率
        isDifficultyIncreaseWithTime = true;    // 难度随时间增加
        enemyAttributeMultiplier = 1.0;         // 初始敌机属性倍率
    }
    
    @Override
    public void adjustDifficultyWithTime(int time) {
        // 随着时间增加，逐渐提高难度
        // 提升精英敌机的概率
        if (eliteEnemyProbability < maxEliteProb) {
            eliteEnemyProbability += 0.0001 * time / 1000.0;
            if (eliteEnemyProbability > maxEliteProb) {
                eliteEnemyProbability = maxEliteProb;
            }
        }
        
        // 缩短敌机产生周期（有下限）
        if (cycleDuration > minCycleDuration) {
            cycleDuration -= time / 10000;
            if (cycleDuration < minCycleDuration) {
                cycleDuration = minCycleDuration;
            }
        }
        
        // 提升敌机属性倍率（有上限）
        if (enemyAttributeMultiplier < 1.5) {  // 最多提升到1.5倍
            enemyAttributeMultiplier = 1.0 + (time / 5000.0) * 0.02;
            if (enemyAttributeMultiplier > 1.5) {
                enemyAttributeMultiplier = 1.5;
            }
            // 打印难度提升信息
            System.out.printf("提高难度！精英机概率：%.2f，敌机周期：%.2f，敌机属性提升倍率：%.2f。%n",
                    eliteEnemyProbability, cycleDuration / 100.0, enemyAttributeMultiplier);
        } else
        {
            System.out.printf("难度已提升到最大！敌机属性提升倍率为1.5%n");
        }
        

    }
    
    // 重写Boss生成逻辑，使Boss血量固定
    @Override
    protected BossEnemy createBossEnemy() {
        System.out.printf("Boss敌机血量：%d。%n", bossHp);
        return (BossEnemy) bossEnemyFactory.createAircraft(
                (int) (Math.random() * (edu.hitsz.application.Main.WINDOW_WIDTH - ImageManager.BOSS_IMAGE.getWidth())),
                (int) (Math.random() * edu.hitsz.application.Main.WINDOW_HEIGHT * 0.05 + 50),
                2,
                0,
                (int)(bossHp)
        );
    }

    @Override
    public void run() {
        action();
    }
}