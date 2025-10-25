package edu.hitsz.prop;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.strategy.ScatterShootStrategy;

import java.util.List;

public class FireSupply extends BaseProp {
    public FireSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }


    @Override
    public void effect(HeroAircraft heroAircraft, List<AbstractAircraft> eliteEnemies, List<BaseBullet> enemyBullets) {
        heroAircraft.switchToScatterShoot();
        
        // 创建效果线程
        Thread effectThread = new Thread(() -> {
            try {
                Thread.sleep(ScatterShootStrategy.SCATTER_SHOOT_DURATION);
                // 效果结束后重置射击模式
                heroAircraft.resetShootMode();
            } catch (InterruptedException e) {
                // 线程被中断，说明拾取了相同或更高级的道具，不需要重置射击模式
            }
        });
        
        // 设置为当前激活的效果线程
        heroAircraft.setActiveEffectThread(effectThread);
        effectThread.start();
    }
}