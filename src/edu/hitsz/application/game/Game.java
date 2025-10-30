package edu.hitsz.application.game;

import edu.hitsz.aircraft.*;
import edu.hitsz.application.HeroController;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.application.music.MusicPlayer;
import edu.hitsz.application.music.MusicThread;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.prop.BaseProp;
import edu.hitsz.prop.BombObserver;
import edu.hitsz.prop.FireSupply;
import edu.hitsz.prop.SuperFireSupply;
import edu.hitsz.scores.LeaderboardManager;
import edu.hitsz.strategy.RingShootStrategy;
import edu.hitsz.strategy.ScatterShootStrategy;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 基础游戏框架，包含游戏界面、游戏逻辑、音乐播放等核心功能
 *
 * @author hitsz
 */
public abstract class Game extends JPanel implements Runnable {

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
     * 当前激活的道具类型
     */
    private PropType activePropType = PropType.NONE;
    
    /**
     * 道具效果开始时间
     */
    private long propEffectStartTime = 0;
    
    /**
     * 道具效果持续时间
     */
    private long propEffectDuration = 0;

    /**
     * 道具类型枚举
     */
    private enum PropType {
        NONE,      // 无道具效果
        FIRE,      // 火力道具效果
        SUPER_FIRE // 超级火力道具效果
    }

    /**
     * 火力道具效果跟踪
     */
    // private long fireSupplyStartTime = 0;
    // private long fireSupplyDuration = 0;
    // private boolean fireSupplyActive = false;

    /**
     * 超级火力道具效果跟踪
     */
    // private long superFireSupplyStartTime = 0;
    // private long superFireSupplyDuration = 0;
    // private boolean superFireSupplyActive = false;

    /**
     * 屏幕中出现的敌机最大数量
     */
    protected int enemyMaxNumber = 5;

    /**
     * 当前得分
     */
    private int score = 0;
    /**
     * 当前时刻
     */
    private int time = 0;
    
    /**
     * 上次难度提升时间
     */
    private int lastDifficultyIncreaseTime = 0;
    
    /**
     * 难度提升间隔（毫秒）
     */
    private final int difficultyIncreaseInterval = 5000; // 每5秒提升一次难度

    /**
     * 周期（ms)
     * 指示子弹的发射、敌机的产生频率
     */
    protected int cycleDuration = 600;
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
    protected final AircraftFactory bossEnemyFactory = new BossEnemyFactory();
    
    /**
     * 排行榜管理器
     */
    protected LeaderboardManager leaderboardManager;
    
    private JButton returnButton;
    
    // 游戏结束后跳转的目标界面
    protected String targetRankPage = "EASY_RANK";
    
    // 难度等级
    protected int level = 1;
    
    // 不同难度的背景图片
    protected BufferedImage backgroundImage;

    // 音乐播放器和音乐线程
    private MusicPlayer musicPlayer = MusicPlayer.getMusicPlayer();
    private MusicThread bgmThread = null;
    private MusicThread bossBgmThread = null;
    
    // 游戏难度相关配置参数
    protected int bossScoreThreshold = 300;  // Boss敌机产生的分数阈值
    protected int bossHp = 500;              // Boss敌机的血量
    protected double eliteEnemyProbability = 0.3;  // 精英敌机的产生概率
    protected double superEliteEnemyProbability = 0.1;  // 超级精英敌机的产生概率
    protected boolean isBossEnabled = true;  // 是否启用Boss敌机
    protected boolean isDifficultyIncreaseWithTime = true;  // 难度是否随时间增加
    
    // 敌机属性倍率
    protected double enemyAttributeMultiplier = 1.0;

    public Game() {
        // 添加返回按钮
        returnButton = new JButton("返回主菜单");
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.cardLayout.show(Main.cardPanel, "MENU");
            }
        });
        this.add(returnButton);
        // 游戏开始时隐藏返回按钮
        returnButton.setVisible(false);

        heroAircraft = HeroAircraft.getInstance();
        // 重置英雄机状态
        heroAircraft.setHp(1000);
        heroAircraft.setLocation(
                Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight());
        // 重置射击模式
        heroAircraft.resetShootMode();
        
        // 设置道具效果结束回调
        heroAircraft.setPropEffectEndCallback(new Runnable() {
            @Override
            public void run() {
                // 道具效果结束时重置状态
                activePropType = PropType.NONE;
            }
        });

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

        // 初始化排行榜管理器
        leaderboardManager = new LeaderboardManager(level);

        // 重置游戏状态
        gameOverFlag = false;
        
        // 配置游戏难度
        configureDifficulty();

    }
    
    /**
     * 抽象方法：配置不同难度的基本参数
     */
    public abstract void configureDifficulty();
    
    /**
     * 抽象方法：随着时间推移调整游戏难度
     * @param time 当前游戏时间
     */
    public abstract void adjustDifficultyWithTime(int time);
    
    /**
     * 创建Boss敌机的工厂方法，可被子类重写以实现不同的Boss属性
     * @return Boss敌机实例
     */
    protected BossEnemy createBossEnemy() {
        return (BossEnemy) bossEnemyFactory.createAircraft(
                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.BOSS_IMAGE.getWidth())),
                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05 + 50),
                2,
                0,
                bossHp
        );
    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {
        
        // 开始播放背景音乐
        bgmThread = musicPlayer.playMusic("src/videos/bgm.wav");

        // 定时任务：绘制、对象产生、碰撞判定、击毁及结束判定
        Runnable task = () -> {

            time += timeInterval;

            // 调整难度
            if (isDifficultyIncreaseWithTime && time - lastDifficultyIncreaseTime >= difficultyIncreaseInterval) {
                lastDifficultyIncreaseTime = time;
                adjustDifficultyWithTime(time);
            }

            // 周期性执行（控制频率）
            if (timeCountAndNewCycleJudge()) {
                // System.out.println(time);
                // 新敌机产生

                if (enemyAircrafts.size() < enemyMaxNumber) {
                    // 生成策略：优先检查Boss生成条件（分数阈值），否则按概率生成超级精英/精英/普通
                    // Boss触发分数阈值（可多次出现）
                    // 检查当前场上是否已有 Boss
                    boolean bossExists = false;
                    for (AbstractAircraft a : enemyAircrafts) {
                        if (a instanceof BossEnemy && !a.notValid()) {
                            bossExists = true;
                            break;
                        }
                    }
                    // 只有在没有 Boss、且冷却时间到、且分数达到阈值时才有概率生成 Boss
                    if (isBossEnabled && !bossExists && (time - lastBossSpawnTime >= bossCooldown) && this.score >= bossScoreThreshold && Math.random() < 0.15) {
                        BossEnemy boss = createBossEnemy();
                        enemyAircrafts.add(boss);
                    
                        // 打印Boss生成信息
                        System.out.println("产生BOSS敌机");

                        // 停止普通背景音乐
                        if (bgmThread != null) {
                            musicPlayer.stopMusic(bgmThread);
                            bgmThread = null;
                        }
                    
                        // 播放Boss背景音乐
                        if (bossBgmThread != null) {
                            musicPlayer.stopMusic(bossBgmThread);
                        }
                        bossBgmThread = musicPlayer.playMusic("src/videos/bgm_boss.wav");
                    } else if (Math.random() < superEliteEnemyProbability) {
                        // 生成超级精英
                        int superSpeedX = (int) (Math.random() * 10) - 5;
                        if (superSpeedX == 0) superSpeedX = 1;
                        enemyAircrafts.add(superEliteEnemyFactory.createAircraft(
                                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.ELITE_PLUS_IMAGE.getWidth())),
                                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                                superSpeedX,
                                (int)(6 * enemyAttributeMultiplier),
                                (int)(80 * enemyAttributeMultiplier)
                        ));
                    } else if (Math.random() < eliteEnemyProbability) {
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
                                (int)(5 * enemyAttributeMultiplier),
                                (int)(50 * enemyAttributeMultiplier)
                        ));
                    } else {
                        // 其余概率生成普通敌机
                        enemyAircrafts.add(mobEnemyFactory.createAircraft(
                                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                                0,
                                (int)(10 * enemyAttributeMultiplier),
                                (int)(30 * enemyAttributeMultiplier)
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
            if (heroAircraft.getHp() <= 0 && !gameOverFlag) {
                // 游戏结束
                gameOverFlag = true;
                executorService.shutdown();
                
                // 显示返回按钮
                returnButton.setVisible(true);
                
                // 停止所有音乐播放
                if (bgmThread != null) {
                    musicPlayer.stopMusic(bgmThread);
                }
                if (bossBgmThread != null) {
                    musicPlayer.stopMusic(bossBgmThread);
                }
                
                // 播放游戏结束音效
                musicPlayer.playMusic("src/videos/game_over.wav");

                // 跳转到排行榜界面并刷新
                Main.cardLayout.show(Main.cardPanel, targetRankPage);

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

                // 刷新对应的排行榜以显示最新记录
                switch (targetRankPage) {
                    case "EASY_RANK":
                        Main.easyRank.refreshRankList();
                        break;
                    case "MEDIUM_RANK":
                        Main.mediumRank.refreshRankList();
                        break;
                    case "HARD_RANK":
                        Main.hardRank.refreshRankList();
                        break;
                }
            }

        };

        /*
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
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyBullets.addAll(enemyAircraft.shoot());
//                musicPlayer.playMusic("src/videos/bullet.wav");
        }

        // 英雄射击
        heroBullets.addAll(heroAircraft.shoot());
//            musicPlayer.playMusic("src/videos/bullet.wav");
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
                    
                    // 播放子弹击中音效
                    musicPlayer.playMusic("src/videos/bullet_hit.wav");
                    
                    if (enemyAircraft.notValid()) {
                        // 获得分数，产生道具补给
                        // 区分 Boss、超级精英、精英与普通的得分与掉落
                        if (enemyAircraft instanceof BossEnemy) {
                            // 击毁 Boss 获得高分
                            score += 100;
                            // 记录Boss死亡时间，开始冷却
                            lastBossSpawnTime = time;
                            List<BaseProp> dropList = ((BossEnemy) enemyAircraft).dropProps();
                            if (dropList != null && !dropList.isEmpty()) {
                                props.addAll(dropList);
                            }
                            
                            // 停止播放Boss背景音乐，恢复普通背景音乐
                            if (bossBgmThread != null) {
                                musicPlayer.stopMusic(bossBgmThread);
                                bossBgmThread = null;
                            }
                            // 如果游戏还没有结束且普通背景音乐线程为空，则重新播放普通背景音乐
                            if (!gameOverFlag && bgmThread == null) {
                                bgmThread = musicPlayer.playMusic("src/videos/bgm.wav");
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
                    
                    // 播放子弹击中音效
                    musicPlayer.playMusic("src/videos/bullet_hit.wav");
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
                System.out.println(prop.getClass().getSimpleName() + " active!");
                
                // 根据道具类型设置相应的状态和时间参数
                if (prop instanceof FireSupply) {
                    // 只有当当前不是环射模式时才应用火力道具
                    if (heroAircraft.getCurrentShootMode() != HeroAircraft.ShootMode.RING) {
                        activePropType = PropType.FIRE;
                        propEffectStartTime = System.currentTimeMillis();
                        propEffectDuration = ScatterShootStrategy.SCATTER_SHOOT_DURATION;
                        prop.effect(heroAircraft, enemyAircrafts, enemyBullets);
                    }
                } else if (prop instanceof SuperFireSupply) {
                    // 超级火力道具可以覆盖任何现有道具
                    activePropType = PropType.SUPER_FIRE;
                    propEffectStartTime = System.currentTimeMillis();
                    propEffectDuration = RingShootStrategy.RING_SHOOT_DURATION;
                    prop.effect(heroAircraft, enemyAircrafts, enemyBullets);
                } else {
                    // 其他道具（如HpSupply, BombSupply等）
                    prop.effect(heroAircraft, enemyAircrafts, enemyBullets);
                }
                
                prop.vanish();
                
                // 播放道具生效音效
                musicPlayer.playMusic("src/videos/get_supply.wav");
            }
        }

    }

    /**
     * 重置道具效果状态
     */
    private void resetPropEffectStatus() {
        activePropType = PropType.NONE;
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
        
        // 处理被炸弹销毁的敌机：加分和掉落道具
        handleBombDestroyedAircrafts();
    }
    
    /**
     * 处理被炸弹销毁的敌机：加分和掉落道具
     */
    private void handleBombDestroyedAircrafts() {
        List<AbstractAircraft> destroyedAircrafts = BombObserver.getDestroyedAircrafts();
        if (!destroyedAircrafts.isEmpty()) {
            for (AbstractAircraft aircraft : destroyedAircrafts) {
                if (aircraft instanceof SuperEliteEnemy) {
                    // 超级精英获得中等分
                    score += 30;
                    BaseProp prop = ((SuperEliteEnemy) aircraft).dropProp();
                    if (prop != null) {
                        props.add(prop);
                    }
                } else if (aircraft instanceof EliteEnemy) {
                    // 精英敌机得分
                    score += 20;
                    BaseProp prop = ((EliteEnemy) aircraft).dropProp();
                    if (prop != null) {
                        props.add(prop);
                    }
                } else if (aircraft instanceof MobEnemy) {
                    // 普通敌机得分
                    score += 10;
                }
            }
            // 清空已处理的列表
            BombObserver.clearDestroyedAircrafts();
        }
    }

    //***********************
    //      Paint 各部分
    //***********************

    /**
     * 重写paint方法
     * 通过重复调用paint方法，实现游戏动画
     *
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // 绘制背景,图片滚动
        g.drawImage(backgroundImage, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(backgroundImage, 0, this.backGroundTop, null);
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

        //绘制道具效果进度条
        paintPropEffectProgressBars(g);

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

    private void paintPropEffectProgressBars(Graphics g) {
        // 根据英雄机当前射击模式校准道具类型
        HeroAircraft.ShootMode currentMode = heroAircraft.getCurrentShootMode();
        if (currentMode == HeroAircraft.ShootMode.STRAIGHT) {
            activePropType = PropType.NONE;
        } else if (currentMode == HeroAircraft.ShootMode.SCATTER) {
            // 如果是散射模式，确保激活的是火力道具
            if (activePropType != PropType.FIRE) {
                activePropType = PropType.NONE;
            }
        } else if (currentMode == HeroAircraft.ShootMode.RING) {
            // 如果是环射模式，确保激活的是超级火力道具
            if (activePropType != PropType.SUPER_FIRE) {
                activePropType = PropType.NONE;
            }
        }
        
        // 检查当前激活的道具类型并绘制相应进度条
        if (activePropType != PropType.NONE) {
            long elapsed = System.currentTimeMillis() - propEffectStartTime;
            
            // 检查道具是否已过期
            if (elapsed >= propEffectDuration) {
                // 道具已过期，重置状态
                activePropType = PropType.NONE;
            } else {
                // 绘制进度条
                float ratio = 1.0f - (float) elapsed / propEffectDuration;
                Color color = (activePropType == PropType.FIRE) ? Color.YELLOW : Color.BLUE;
                
                drawVerticalProgressBar(g, heroAircraft.getLocationX() + ImageManager.HERO_IMAGE.getWidth() / 2 + 10,
                        heroAircraft.getLocationY() - 30, 20, 60, ratio, color);
            }
        } else {
            // 如果有射击模式但没有激活的道具，尝试重新同步状态
            if (currentMode == HeroAircraft.ShootMode.SCATTER) {
                activePropType = PropType.FIRE;
                // 注意：这里我们无法准确知道开始时间，所以进度条可能不准确
            } else if (currentMode == HeroAircraft.ShootMode.RING) {
                activePropType = PropType.SUPER_FIRE;
                // 注意：这里我们无法准确知道开始时间，所以进度条可能不准确
            }
        }
    }

    /**
     * 绘制竖直进度条
     * @param g Graphics对象
     * @param x 进度条x坐标
     * @param y 进度条y坐标
     * @param width 进度条宽度
     * @param height 进度条高度
     * @param ratio 进度比例 (0.0 - 1.0)
     * @param color 进度条颜色
     */
    private void drawVerticalProgressBar(Graphics g, int x, int y, int width, int height, float ratio, Color color) {
        // 确保比例在有效范围内
        ratio = Math.max(0.0f, Math.min(1.0f, ratio));
        
        // 绘制背景
        g.setColor(Color.GRAY);
        g.fillRect(x, y, width, height);
        
        // 绘制进度
        int progressHeight = (int) (height * ratio);
        g.setColor(color);
        g.fillRect(x, y + height - progressHeight, width, progressHeight);
        
        // 绘制边框
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
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