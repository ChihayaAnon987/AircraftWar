package edu.hitsz.strategy;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 直射策略
 */
public class StraightShootStrategy implements ShootStrategy {
    private boolean isHero;

    public StraightShootStrategy(boolean isHero) {
        this.isHero = isHero;
    }

    @Override
    public List<BaseBullet> shoot(int x, int y, int speedX, int speedY, int direction, int shootNum, int power) {
        List<BaseBullet> res = new LinkedList<>();
        int bulletX = x;
        int bulletY = y + direction * 2;
        int bulletSpeedX = speedX;
        int bulletSpeedY = speedY + direction * (isHero ? 10 : 4);
        
        BaseBullet bullet;
        for (int i = 0; i < shootNum; i++) {
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            int offsetX = (i * 2 - shootNum + 1) * 10;
            if (isHero) {
                bullet = new HeroBullet(bulletX + offsetX, bulletY, 0, bulletSpeedY, power);
            } else {
                bullet = new EnemyBullet(bulletX + offsetX, bulletY, 0, bulletSpeedY, power);
            }
            res.add(bullet);
        }
        return res;
    }
}