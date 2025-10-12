package edu.hitsz.application;

import edu.hitsz.aircraft.*;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.prop.BaseProp;
import edu.hitsz.scores.LeaderboardManager;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 游戏主面板，游戏主要组件之一，用于绘制并控制游戏界面
 *
 * @author hitsz
 */
public class Game extends JPanel {

    private int backGroundTop = 0;

    /**
     * Scheduled 线程池，用于任务调度
     */
    private final ScheduledExecutorService executorService;

    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private int timeInterval = 40;

    private final HeroAircraft heroAircraft;
    private final List<AbstractAircraft> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<BaseProp> props; // 道具列表

    /**
     * 屏幕中出现的敌机最大数量
     */
    private int enemyMaxNumber = 5;

    /**
     * 当前得分
     */
    private int score = 0;
    /**
     * 当前时刻
     */
    private int time = 0;

    /**
     * 周期（ms)
     * 指示子弹的发射、敌机的产生频率
     */
    private int cycleDuration = 600;
    private int cycleTime = 0;

    /**
     * 游戏结束标志
     */
    private boolean gameOverFlag = false;

    /**
     * Boss 控制：生成冷却与上次生成时间（ms）
     */
    private final int bossCooldown = 30000; // 30s 冷却，可调整
    private int lastBossSpawnTime = -bossCooldown; // 初始允许生成
    // 使用工厂
    private final AircraftFactory mobEnemyFactory = new MobEnemyFactory();
    private final AircraftFactory eliteEnemyFactory = new EliteEnemyFactory();
    private final AircraftFactory superEliteEnemyFactory = new SuperEliteEnemyFactory();
    private final AircraftFactory bossEnemyFactory = new BossEnemyFactory();
    
    /**
     * 排行榜管理器
     */
    private final LeaderboardManager leaderboardManager = new LeaderboardManager();

    public Game() {
        heroAircraft = HeroAircraft.getInstance();
        heroAircraft.setLocation(
                Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight());

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>(); // 初始化道具列表

        /**
         * Scheduled 线程池，用于定时任务调度
         * 关于alibaba code guide：可命名的 ThreadFactory 一般需要第三方包
         * apache 第三方库： org.apache.commons.lang3.concurrent.BasicThreadFactory
         */
        this.executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("game-action-%d").daemon(true).build());

        //启动英雄机鼠标监听
        new HeroController(this, heroAircraft);

    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {

        // 定时任务：绘制、对象产生、碰撞判定、击毁及结束判定
        Runnable task = () -> {

            time += timeInterval;


            // 周期性执行（控制频率）
            if (timeCountAndNewCycleJudge()) {
                System.out.println(time);
                // 新敌机产生

                if (enemyAircrafts.size() < enemyMaxNumber) {
                    // 生成策略：优先检查Boss生成条件（分数阈值），否则按概率生成超级精英/精英/普通
                    // Boss触发分数阈值（可多次出现）
                    int bossSpawnScoreThreshold = 500; // 可调整：达到该分数后Boss有机会出现
                    // 检查当前场上是否已有 Boss
                    boolean bossExists = false;
                    for (AbstractAircraft a : enemyAircrafts) {
                        if (a instanceof BossEnemy && !a.notValid()) {
                            bossExists = true;
                            break;
                        }
                    }
                    // 只有在没有 Boss、且冷却时间到、且分数达到阈值时才有概率生成 Boss
                    if (!bossExists && (time - lastBossSpawnTime >= bossCooldown) && this.score >= bossSpawnScoreThreshold && Math.random() < 0.05) {
                        enemyAircrafts.add(bossEnemyFactory.createAircraft(
                            (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.BOSS_IMAGE.getWidth())),
                            (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05 + 50),
                            2,
                            0,
                            1000
                        ));
                        // 记录生成时间，开始冷却
                        lastBossSpawnTime = time;
                    } else if (Math.random() < 0.1) {
                        // 生成超级精英
                        int superSpeedX = (int) (Math.random() * 10) - 5;
                        if (superSpeedX == 0) superSpeedX = 1;
                        enemyAircrafts.add(superEliteEnemyFactory.createAircraft(
                                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.ELITE_PLUS_IMAGE.getWidth())),
                                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                                superSpeedX,
                                6,
                                80
                        ));
                    } else if (Math.random() < 0.3) {
                        // 生成精英敌机
                        int eliteSpeedX = (int) (Math.random() * 10) - 5;
                        // 确保精英敌机的speedX不为0
                        if (eliteSpeedX == 0) {
                            eliteSpeedX = 1;
                        }
                        enemyAircrafts.add(eliteEnemyFactory.createAircraft(
                                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.ELITE_ENEMY_IMAGE.getWidth())),
                                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                                eliteSpeedX,
                                5,
                                50
                        ));
                    } else {
                        // 其余概率生成普通敌机
                        enemyAircrafts.add(mobEnemyFactory.createAircraft(
                                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                                0,
                                10,
                                30
                        ));
                    }
                }
                // 飞机射出子弹
                shootAction();
            }

            // 子弹移动
            bulletsMoveAction();

            // 道具移动
            propsMoveAction();

            // 飞机移动
            aircraftsMoveAction();

            // 撞击检测
            crashCheckAction();

            // 后处理
            postProcessAction();

            //每个时刻重绘界面
            repaint();

            // 游戏结束检查英雄机是否存活
            if (heroAircraft.getHp() <= 0) {
                // 游戏结束
                executorService.shutdown();
                gameOverFlag = true;
                
                // 获取玩家姓名并记录分数
                String playerName = JOptionPane.showInputDialog(
                    null, 
                    "游戏结束！您的得分：" + score + "\n请输入您的姓名：", 
                    "游戏结束", 
                    JOptionPane.QUESTION_MESSAGE
                );
                
                // 如果玩家点击了取消或关闭对话框，使用默认名称
                if (playerName == null || playerName.trim().isEmpty()) {
                    playerName = leaderboardManager.getNextAnonymousName();
                }
                
                // 添加记录到排行榜
                leaderboardManager.addRecord(playerName, score);
                
                // 显示排行榜
                leaderboardManager.displayLeaderboard();
            }

        };

        /**
         * 以固定延迟时间进行执行
         * 本次任务执行完成后，需要延迟设定的延迟时间，才会执行新的任务
         */
        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);

    }

    //***********************
    //      Action 各部分
    //***********************

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private void shootAction() {
        // 敌机射击
        for (AbstractAircraft enemyAircraft : enemyAircrafts){
            enemyBullets.addAll(enemyAircraft.shoot());
        }


        // 英雄射击
        heroBullets.addAll(heroAircraft.shoot());
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void propsMoveAction() {
        for (BaseProp prop : props) {
            prop.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }


    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() {
        // 敌机子弹攻击英雄
        for (BaseBullet bullet : enemyBullets) {
            if(bullet.notValid()){
                continue;
            }
            if(heroAircraft.crash(bullet)){
                //战机被射中
                //损失生命值
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    if (enemyAircraft.notValid()) {
                        // 获得分数，产生道具补给
                        // 区分 Boss、超级精英、精英与普通的得分与掉落
                        if (enemyAircraft instanceof BossEnemy) {
                            // 击毁 Boss 获得高分
                            score += 100;
                            List<BaseProp> dropList = ((BossEnemy) enemyAircraft).dropProps();
                            if (dropList != null && !dropList.isEmpty()) {
                                props.addAll(dropList);
                            }
                        } else if (enemyAircraft instanceof SuperEliteEnemy) {
                            // 超级精英获得中等分
                            score += 30;
                            BaseProp prop = ((SuperEliteEnemy) enemyAircraft).dropProp();
                            if (prop != null) {
                                props.add(prop);
                            }
                        } else if (enemyAircraft instanceof EliteEnemy) {
                            score += 20;
                            // 精英敌机有几率掉落道具
                            BaseProp prop = ((EliteEnemy) enemyAircraft).dropProp();
                            if (prop != null) {
                                props.add(prop);
                            }
                        } else {
                            // 普通敌机
                            score += 10;
                        }
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // 我方获得道具，道具生效
        for (BaseProp prop : props) {
            if (prop.notValid()) {
                continue;
            }
            if (heroAircraft.crash(prop)) {
                // 道具与英雄机碰撞
                prop.effect(heroAircraft, enemyAircrafts, enemyBullets);
                prop.vanish();
            }
        }

    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 删除无效的道具
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid); // 移除无效道具
    }


    //***********************
    //      Paint 各部分
    //***********************

    /**
     * 重写paint方法
     * 通过重复调用paint方法，实现游戏动画
     *
     * @param  g
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // 绘制背景,图片滚动
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop, null);
        this.backGroundTop += 1;
        if (this.backGroundTop == Main.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        // 先绘制子弹，后绘制飞机
        // 这样子弹显示在飞机的下层
        paintImageWithPositionRevised(g, enemyBullets);
        paintImageWithPositionRevised(g, heroBullets);
        paintImageWithPositionRevised(g, props); // 绘制道具

        paintImageWithPositionRevised(g, enemyAircrafts);

        g.drawImage(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

        //绘制得分和生命值
        paintScoreAndLife(g);

    }

    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
        }
    }

    private void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(new Color(16711680));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE:" + this.score, x, y);
        y = y + 20;
        g.drawString("LIFE:" + this.heroAircraft.getHp(), x, y);
    }


}