package edu.hitsz.application.game;

import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.application.ImageManager;
import edu.hitsz.scores.LeaderboardManager;

public class HardGame extends Game {
    private int initialBossHp = 500;            // 初始Boss血量
    private int bossHpIncrement = 100;          // 每次Boss血量增加
    private int maxBossHp = 1500;               // Boss血量上限
    private int initialCycleDuration = 600;     // 初始敌机产生周期
    private int minCycleDuration = 350;         // 最小敌机产生周期
    private double initialEliteProb = 0.4;      // 初始精英敌机概率
    private double maxEliteProb = 0.6;          // 最大精英敌机概率
    private int bossSpawnCount = 0;             // Boss生成次数
    
    public HardGame() {
        targetRankPage = "HARD_RANK";
        level = 3;
        leaderboardManager = new LeaderboardManager(level);
        // 设置困难难度背景图片
        backgroundImage = ImageManager.BACKGROUND_IMAGE_HARD;
    }

    @Override
    public void configureDifficulty() {
        // 困难模式配置
        enemyMaxNumber = 8;                    // 敌机最大数量
        cycleDuration = 600;                    // 初始敌机产生周期
        bossScoreThreshold = 300;               // Boss敌机产生的分数阈值
        isBossEnabled = true;                   // 启用Boss敌机
        bossHp = 500;                           // 初始Boss敌机的血量
        eliteEnemyProbability = 0.4;            // 初始精英敌机的产生概率
        superEliteEnemyProbability = 0.15;      // 超级精英敌机的产生概率
        isDifficultyIncreaseWithTime = true;    // 难度随时间增加
        enemyAttributeMultiplier = 1.0;         // 初始敌机属性倍率
    }
    
    @Override
    public void adjustDifficultyWithTime(int time) {
        // 随着时间增加，逐渐提高难度
        // 提升精英敌机的概率
        if (eliteEnemyProbability < maxEliteProb) {
            eliteEnemyProbability += 0.0002 * time / 1000.0;
            if (eliteEnemyProbability > maxEliteProb) {
                eliteEnemyProbability = maxEliteProb;
            }
        }
        
        // 缩短敌机产生周期（有下限）
        if (cycleDuration > minCycleDuration) {
            cycleDuration -= time / 8000;
            if (cycleDuration < minCycleDuration) {
                cycleDuration = minCycleDuration;
            }
        }
        
        // 提升敌机属性倍率（有上限）
        if (enemyAttributeMultiplier < 2.0) {  // 最多提升到2倍
            enemyAttributeMultiplier = 1.0 + (time / 5000.0) * 0.02;
            if (enemyAttributeMultiplier > 2.0) {
                enemyAttributeMultiplier = 2.0;
            }
            // 打印难度提升信息
            System.out.printf("提高难度！精英机概率：%.2f，敌机周期：%.2f，敌机属性提升倍率：%.2f。%n",
                    eliteEnemyProbability, cycleDuration / 100.0, enemyAttributeMultiplier);
        } else
        {
            System.out.printf("难度已提升到最大！敌机属性提升倍率为2.0");
        }
        

    }
    
    // 重写Boss生成逻辑，使每次Boss血量递增，但有上限
    @Override
    protected BossEnemy createBossEnemy() {
        bossSpawnCount++;
        int currentBossHp = initialBossHp + (bossSpawnCount - 1) * bossHpIncrement;
        // 设置Boss血量上限
        if (currentBossHp > maxBossHp) {
            currentBossHp = maxBossHp;
        }
        int finalBossHp = (int)(currentBossHp * enemyAttributeMultiplier);
        System.out.printf("Boss敌机血量：%d。%n", finalBossHp);
        return (BossEnemy) bossEnemyFactory.createAircraft(
                (int) (Math.random() * (edu.hitsz.application.Main.WINDOW_WIDTH - ImageManager.BOSS_IMAGE.getWidth())),
                (int) (Math.random() * edu.hitsz.application.Main.WINDOW_HEIGHT * 0.05 + 50),
                2,
                0,
                finalBossHp
        );
    }

    @Override
    public void run() {
        action();
    }
}