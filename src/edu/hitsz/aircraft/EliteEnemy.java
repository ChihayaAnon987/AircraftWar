package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.prop.BaseProp;
import edu.hitsz.prop.BombSupply;
import edu.hitsz.prop.FireSupply;
import edu.hitsz.prop.HpSupply;

import java.util.LinkedList;
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

    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
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
        List<BaseBullet> res = new LinkedList<>();
        int x = this.getLocationX();
        int y = this.getLocationY() + direction*2;
        int speedX = 0;
        int speedY = this.getSpeedY() + direction*4;
        BaseBullet bullet;
        for(int i=0; i<shootNum; i++){
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            bullet = new HeroBullet(x + (i*2 - shootNum + 1)*10, y, speedX, speedY, power);
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
     * 精英敌机坠毁后随机掉落道具
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
                    return new HpSupply(this.getLocationX(), this.getLocationY(), 0, 3);
                case 1:
                    return new FireSupply(this.getLocationX(), this.getLocationY(), 0, 3);
                case 2:
                    return new BombSupply(this.getLocationX(), this.getLocationY(), 0, 3);
                default:
                    return null;
            }
        }
        return null;
    }
}