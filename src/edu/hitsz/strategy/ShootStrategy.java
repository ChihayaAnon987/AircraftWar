package edu.hitsz.strategy;

import edu.hitsz.bullet.BaseBullet;

import java.util.List;

/**
 * 射击策略接口
 */
public interface ShootStrategy {
    /**
     * 根据给定参数生成子弹列表
     * @param x 射击位置x坐标
     * @param y 射击位置y坐标
     * @param speedX 飞机x轴速度
     * @param speedY 飞机y轴速度
     * @param direction 子弹方向
     * @param shootNum 子弹数量
     * @param power 子弹威力
     * @return 子弹列表
     */
    List<BaseBullet> shoot(int x, int y, int speedX, int speedY, int direction, int shootNum, int power);
}