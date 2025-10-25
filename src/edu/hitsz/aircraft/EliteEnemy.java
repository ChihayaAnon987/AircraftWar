package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.prop.*;
import edu.hitsz.strategy.StraightShootStrategy;

import java.util.List;
import java.util.Random;

/**
 * 精英敌机
 * 可以射击
 *
 * @author ChihayaAnon
 */
public class EliteEnemy extends AbstractAircraft {
    /**攻击方式 */

    /**
     * 子弹一次发射数量
     */
    private int shootNum = 1;

    /**
     * 子弹伤害
     */
    private int power = 20;

    /**
     * 子弹射击方向 (向上发射：-1，向下发射：1)
     */
    private int direction = 1;

    // 定义三种道具工厂
    private static final PropFactory hpSupplyFactory = new HpSupplyFactory();
    private static final PropFactory fireSupplyFactory = new FireSupplyFactory();
    private static final PropFactory bombSupplyFactory = new BombSupplyFactory();
    
    // 道具掉落概率配置 (总和应该为100)
    private static final int HP_SUPPLY_PROBABILITY = 10;
    private static final int FIRE_SUPPLY_PROBABILITY = 20;
    private static final int BOMB_SUPPLY_PROBABILITY = 70;

    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        // 精英敌机使用直射策略
        this.shootStrategy = new StraightShootStrategy(false);
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT ) {
            vanish();
        }
    }

    /**
     * 通过射击产生子弹
     * @return 射击出的子弹List
     */
    @Override
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

    /**
     * 精英敌机坠毁后随机掉落道具
     * @return 掉落的道具，如果没有掉落则返回 null
     */
    public BaseProp dropProp() {
        // 50%概率掉落道具
        Random random = new Random();
        if (random.nextBoolean()) {
            // 根据配置的概率生成道具
            int propRand = random.nextInt(100);
            if (propRand < HP_SUPPLY_PROBABILITY) {
                return hpSupplyFactory.createProp(this.getLocationX(), this.getLocationY(), 0, 3);
            } else if (propRand < HP_SUPPLY_PROBABILITY + FIRE_SUPPLY_PROBABILITY) {
                return fireSupplyFactory.createProp(this.getLocationX(), this.getLocationY(), 0, 3);
            } else {
                return bombSupplyFactory.createProp(this.getLocationX(), this.getLocationY(), 0, 3);
            }
        }
        return null;
    }
}