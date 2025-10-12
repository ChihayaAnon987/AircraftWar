package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.prop.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 超级精英敌机
 * 实现散射弹道
 *
 * @author ChihayaAnon
 */
public class SuperEliteEnemy extends AbstractAircraft {
    /**
     * 子弹发射数量
     */
    private int shootNum = 3;

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

    /**
     * 散射角度范围
     */
    private double spreadAngle = Math.PI / 6; // 30度

    public SuperEliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
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
        List<BaseBullet> res = new LinkedList<>();
        int x = this.getLocationX();
        int y = this.getLocationY() + direction * 2;

        int baseSpeedX = this.speedX;
        int baseSpeedY = this.getSpeedY();
        int speedx;
        int speedy = (direction > 0) ? (baseSpeedY + direction * 5) : (baseSpeedY + direction * 10);
        BaseBullet bullet;

        for (int i = 0; i < shootNum; i++) {
            // 子弹发射位置相对飞机位置向前偏移，多颗子弹横向分散
            // 横向速度分配：-2,0,2
            if (i == 0) speedx = -2;
            else if (i == 1) speedx = 0;
            else speedx = 2;

            int bx = x + (i * 2 - shootNum + 1) * 10;

            // 敌机发射均为 EnemyBullet（向下或向上由 speedy 控制）
            bullet = new EnemyBullet(bx, y, speedx + baseSpeedX, speedy, power);
            res.add(bullet);
        }

        return res;
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
     * 超级精英敌机坠毁后随机掉落道具
     * @return 掉落的道具，如果没有掉落则返回 null
     */
    public BaseProp dropProp() {
        // 50%概率掉落道具
        Random random = new Random();
        if (random.nextBoolean()) {
            // 随机生成一种道具
            int propType = random.nextInt(3);
            switch (propType) {
                case 0:
                    return hpSupplyFactory.createProp(this.getLocationX(), this.getLocationY(), 0, 3);
                case 1:
                    return fireSupplyFactory.createProp(this.getLocationX(), this.getLocationY(), 0, 3);
                case 2:
                    return bombSupplyFactory.createProp(this.getLocationX(), this.getLocationY(), 0, 3);
                default:
                    return null;
            }
        }
        return null;
    }
}