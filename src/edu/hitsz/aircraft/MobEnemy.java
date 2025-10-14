package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.strategy.NoneShootStrategy;

import java.util.List;

/**
 * 普通敌机
 * 不可射击
 *
 * @author hitsz
 */
public class MobEnemy extends AbstractAircraft {

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        // 普通敌机不发射子弹，使用空射击策略
        this.shootStrategy = new NoneShootStrategy();
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT ) {
            vanish();
        }
    }

    @Override
    public List<BaseBullet> shoot() {
        // 普通敌机不能射击，使用策略模式
        return shootStrategy.shoot(
                this.getLocationX(),
                this.getLocationY(),
                this.speedX,
                this.getSpeedY(),
                this.getDirection(),
                this.getShootNum(),
                this.getPower());
    }

    @Override
    public int getDirection() {
        return 1; // 向下
    }

    @Override
    public int getShootNum() {
        return 0;
    }

    @Override
    public int getPower() {
        return 0;
    }

}