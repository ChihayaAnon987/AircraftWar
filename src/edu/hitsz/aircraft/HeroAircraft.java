package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.strategy.RingShootStrategy;
import edu.hitsz.strategy.ScatterShootStrategy;
import edu.hitsz.strategy.StraightShootStrategy;

import java.util.List;

/**
 * 英雄飞机，游戏玩家操控
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {

    /**
     * 英雄机实例（单例模式）
     */
    private static HeroAircraft instance = null;

    /*攻击方式 */

    /**
     * 子弹一次发射数量
     */
    private int shootNum = 1;

    /**
     * 子弹伤害
     */
    private int power = 30;

    /**
     * 子弹射击方向 (向上发射：-1，向下发射：1)
     */
    private int direction = -1;
    
    /**
     * 当前射击模式
     */
    private ShootMode currentShootMode = ShootMode.STRAIGHT;
    
    /**
     * 当前激活的效果线程
     */
    private Thread activeEffectThread = null;
    
    /**
     * 道具效果结束回调
     */
    private Runnable propEffectEndCallback = null;

    /**
     * 私有构造方法，防止外部实例化
     * @param locationX 英雄机位置x坐标
     * @param locationY 英雄机位置y坐标
     * @param speedX 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param speedY 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param hp    初始生命值
     */
    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        // 英雄机默认使用直射策略
        this.shootStrategy = new StraightShootStrategy(true);
    }

    public void setHp(int hp) {
        this.hp = hp;
    }


    /**
     * 射击模式枚举
     */
    public enum ShootMode {
        STRAIGHT,    // 直射
        SCATTER,     // 散射
        RING         // 环射
    }
    /**
     * 获取英雄机实例（单例模式）
     * @return 英雄机实例
     */
    public static HeroAircraft getInstance() {
        if (instance == null) {
            instance = new HeroAircraft(
                    0, 
                    0, 
                    0, 
                    0, 
                    1000);
        }
        return instance;
    }

    /**
     * 设置英雄机位置
     * @param locationX x坐标
     * @param locationY y坐标
     */
    public void setLocation(int locationX, int locationY) {
        this.setLocation((double)locationX, (double)locationY);
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    @Override
    /*
      通过射击产生子弹
      @return 射击出的子弹List
     */
    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(
                this.getLocationX(),
                this.getLocationY(),
                this.speedX,
                this.getSpeedY(),
                direction,
                shootNum,
                power
        );
    }

    @Override
    public int getDirection() {
        return direction;
    }

    @Override
    public int getShootNum() {
        return shootNum;
    }

    @Override
    public int getPower() {
        return power;
    }

    public void setShootNum(int num){
        shootNum = num;
    }
    
    /**
     * 切换到散射模式（仅当当前不是环射模式时）
     */
    public void switchToScatterShoot() {
        // 中断之前的道具效果线程
        if (activeEffectThread != null && activeEffectThread.isAlive()) {
            activeEffectThread.interrupt();
        }
        
        if (currentShootMode != ShootMode.RING) {
            this.shootStrategy = new ScatterShootStrategy(true);
            this.shootNum = 3;
            this.currentShootMode = ShootMode.SCATTER;
        }
    }
    
    /**
     * 切换到环射模式
     */
    public void switchToRingShoot() {
        // 中断之前的道具效果线程
        if (activeEffectThread != null && activeEffectThread.isAlive()) {
            activeEffectThread.interrupt();
        }
        
        this.shootStrategy = new RingShootStrategy(true);
        this.shootNum = 20;
        this.currentShootMode = ShootMode.RING;
    }
    
    /**
     * 重置射击模式为直射
     */
    public void resetShootMode() {
        // 中断之前的道具效果线程
        if (activeEffectThread != null && activeEffectThread.isAlive()) {
            activeEffectThread.interrupt();
        }
        
        this.shootStrategy = new StraightShootStrategy(true);
        this.shootNum = 1;
        this.currentShootMode = ShootMode.STRAIGHT;
        
        // 道具效果结束时调用回调
        if (propEffectEndCallback != null) {
            propEffectEndCallback.run();
        }
    }
    
    /**
     * 设置道具效果结束回调
     * @param callback 回调函数
     */
    public void setPropEffectEndCallback(Runnable callback) {
        this.propEffectEndCallback = callback;
    }
    
    /**
     * 获取当前射击模式
     * @return 当前射击模式
     */
    public ShootMode getCurrentShootMode() {
        return currentShootMode;
    }
    
    /**
     * 设置当前激活的效果线程
     * @param thread 效果线程
     */
    public void setActiveEffectThread(Thread thread) {
        // 如果已有激活的效果线程，先中断它
        if (activeEffectThread != null && activeEffectThread.isAlive()) {
            activeEffectThread.interrupt();
        }
        this.activeEffectThread = thread;
    }
}