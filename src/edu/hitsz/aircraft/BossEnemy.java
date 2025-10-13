package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.prop.*;
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

    // 定义三种道具工厂
    private static final PropFactory hpSupplyFactory = new HpSupplyFactory();
    private static final PropFactory fireSupplyFactory = new FireSupplyFactory();
    private static final PropFactory bombSupplyFactory = new BombSupplyFactory();

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
        List<BaseBullet> res = new ArrayList<>();
        int x = this.getLocationX();
        int y = this.getLocationY();
        
        // 环射弹道：20颗子弹呈环形发射
        // 使用极坐标计算每个子弹的角度和位置
        for (int i = 0; i < shootNum; i++) {
            double angle = 2 * Math.PI * i / shootNum; // 均匀分布的角度
            
            // 计算子弹的x和y位置
            int bulletX = (int) (x + 30 * Math.cos(angle)); // 30是半径
            int bulletY = (int) (y + 30 * Math.sin(angle));
            
            // 计算子弹的速度（朝向圆心）
            int bulletSpeedX = (int) (Math.cos(angle) * 5); // 5是速度大小
            int bulletSpeedY = (int) (Math.sin(angle) * 5);
            
            // 创建子弹
            BaseBullet bullet = new EnemyBullet(bulletX, bulletY, bulletSpeedX, bulletSpeedY, power);
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
            int propType = random.nextInt(3);
            // 计算水平偏移，使道具围绕 Boss 中心分布
            int offsetIndex = i - (count - 1) / 2; // 居中偏移
            int dropX = this.getLocationX() + offsetIndex * spacing;
            int dropY = this.getLocationY();
            int speedX = 0;
            int speedY = 5;
            switch (propType) {
                case 0:
                    res.add(hpSupplyFactory.createProp(dropX, dropY, speedX, speedY));
                    break;
                case 1:
                    res.add(fireSupplyFactory.createProp(dropX, dropY, speedX, speedY));
                    break;
                case 2:
                    res.add(bombSupplyFactory.createProp(dropX, dropY, speedX, speedY));
                    break;
                default:
                    break;
            }
        }
        return res;
    }
}