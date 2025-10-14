package edu.hitsz.strategy;

import edu.hitsz.bullet.BaseBullet;

import java.util.Collections;
import java.util.List;

/**
 * 空射击策略
 * 不产生任何子弹
 */
public class NoneShootStrategy implements ShootStrategy {

    @Override
    public List<BaseBullet> shoot(int x, int y, int speedX, int speedY, int direction, int shootNum, int power) {
        // 返回一个空的子弹列表
        return Collections.emptyList();
    }
}