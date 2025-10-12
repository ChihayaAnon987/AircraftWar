package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.prop.*;
import edu.hitsz.strategy.RingShootStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Boss敌机
 * 实现环射弹道，同时发射20颗子弹，呈环形
 *
 * @author Lingma
 */
public class BossEnemy extends AbstractAircraft {
    /**
     * 子弹数量
     */
    private int shootNum = 20;

    /**
     * 子弹伤害
     */
    private int power = 15;

    /**
     * 子弹射击方向 (向上发射：-1，向下发射：1)
     */
    private int direction = 1;

    // 定义四种道具工厂
    private static final PropFactory hpSupplyFactory = new HpSupplyFactory();
    private static final PropFactory fireSupplyFactory = new FireSupplyFactory();
    private static final PropFactory bombSupplyFactory = new BombSupplyFactory();
    private static final PropFactory superFireSupplyFactory = new SuperFireSupplyFactory();
    
    // 道具掉落概率配置 (总和应该为100)
    private static final int HP_SUPPLY_PROBABILITY = 25;
    private static final int FIRE_SUPPLY_PROBABILITY = 5;
    private static final int BOMB_SUPPLY_PROBABILITY = 10;
    private static final int SUPER_FIRE_SUPPLY_PROBABILITY = 60;

    /**
     * 构造函数
     * @param locationX x坐标
     * @param locationY y坐标
     * @param speedX x轴速度
     * @param speedY y轴速度
     * @param hp 生命值
     */
    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        // Boss敌机使用环射策略
        this.shootStrategy = new RingShootStrategy(false);
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

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
     * Boss 敌机坠毁后随机掉落 0..3 个道具
     * @return 道具列表（可能为空列表）
     */
    public List<BaseProp> dropProps() {
        List<BaseProp> res = new ArrayList<>();
        Random random = new Random();
        int count = random.nextInt(4); // 0..3
        // 为避免掉落道具重叠，仅通过水平偏移调整初始位置，保持垂直下落（speedX = 0）
        int spacing = 30; // 每个道具的水平间隔(px)，可根据图片宽度调整
        for (int i = 0; i < count; i++) {
            // 根据配置的概率生成道具
            int propRand = random.nextInt(100);
            int offsetIndex = i - (count - 1) / 2; // 居中偏移
            int dropX = this.getLocationX() + offsetIndex * spacing;
            int dropY = this.getLocationY();
            int speedX = 0;
            int speedY = 5;
            
            BaseProp prop;
            if (propRand < HP_SUPPLY_PROBABILITY) {
                prop = hpSupplyFactory.createProp(dropX, dropY, speedX, speedY);
            } else if (propRand < HP_SUPPLY_PROBABILITY + FIRE_SUPPLY_PROBABILITY) {
                prop = fireSupplyFactory.createProp(dropX, dropY, speedX, speedY);
            } else if (propRand < HP_SUPPLY_PROBABILITY + FIRE_SUPPLY_PROBABILITY + BOMB_SUPPLY_PROBABILITY) {
                prop = bombSupplyFactory.createProp(dropX, dropY, speedX, speedY);
            } else {
                prop = superFireSupplyFactory.createProp(dropX, dropY, speedX, speedY);
            }
            
            if (prop != null) {
                res.add(prop);
            }
        }
        return res;
    }
}