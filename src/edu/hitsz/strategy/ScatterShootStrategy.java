package edu.hitsz.strategy;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 散射策略
 */
public class ScatterShootStrategy implements ShootStrategy {
    private boolean isHero;

    public static int SCATTER_SHOOT_DURATION = 50000;

    public ScatterShootStrategy(boolean isHero) {
        this.isHero = isHero;
    }

    @Override
    public List<BaseBullet> shoot(int x, int y, int speedX, int speedY, int direction, int shootNum, int power) {
        List<BaseBullet> res = new LinkedList<>();
        int bulletX = x;
        int bulletY = y + direction * 2;
        
        int baseSpeedY = speedY;
        int speedy = (direction > 0) ? (baseSpeedY + direction * 5) : (baseSpeedY + direction * 10);
        
        BaseBullet bullet;
        for (int i = 0; i < shootNum; i++) {
            // 子弹发射位置相对飞机位置向前偏移，多颗子弹横向分散
            // 横向速度分配
            int speedx;
            if (i == 0) speedx = -2;
            else if (i == 1) speedx = 0;
            else speedx = 2;

            int bx = bulletX + (i * 2 - shootNum + 1) * 10;

            // 根据是英雄机还是敌机创建不同类型的子弹
            if (isHero) {
                bullet = new HeroBullet(bx, bulletY, speedx + speedX, speedy, power);
            } else {
                bullet = new EnemyBullet(bx, bulletY, speedx + speedX, speedy, power);
            }
            res.add(bullet);
        }
        return res;
    }
}