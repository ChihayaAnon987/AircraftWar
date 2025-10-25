package edu.hitsz.prop;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.aircraft.MobEnemy;
import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.aircraft.SuperEliteEnemy;
import edu.hitsz.bullet.BaseBullet;

import java.util.List;
import java.util.ArrayList;

/**
 * 炸弹观察者类，实现观察者接口
 */
public class BombObserver implements Observer {
    private final AbstractAircraft aircraft;
    private final BaseBullet bullet;
    private static List<AbstractAircraft> destroyedAircrafts = new ArrayList<>();

    public BombObserver(AbstractAircraft aircraft) {
        this.aircraft = aircraft;
        this.bullet = null;
    }

    public BombObserver(BaseBullet bullet) {
        this.bullet = bullet;
        this.aircraft = null;
    }

    @Override
    public void update() {
        // 处理敌机
        if (aircraft != null) {
            // 根据不同类型的敌机执行不同的操作
            if (aircraft instanceof MobEnemy) {
                // 普通敌机：直接消失
                aircraft.vanish();
                // 添加到销毁列表中
                destroyedAircrafts.add(aircraft);
            } else if (aircraft instanceof EliteEnemy) {
                // 精英敌机：直接消失
                aircraft.vanish();
                // 添加到销毁列表中
                destroyedAircrafts.add(aircraft);
            } else if (aircraft instanceof SuperEliteEnemy) {
                // 超级精英敌机：血量减少
                aircraft.decreaseHp(50);
                // 如果血量降到0以下，添加到销毁列表中
                if (aircraft.notValid()) {
                    destroyedAircrafts.add(aircraft);
                }
            }
            // Boss敌机不受影响，不执行任何操作
        }
        
        // 处理敌机子弹
        if (bullet != null) {
            // 子弹在炸弹效果下直接消失
            bullet.vanish();
        }
    }

    public AbstractAircraft getAircraft() {
        return aircraft;
    }
    
    public BaseBullet getBullet() {
        return bullet;
    }
    
    /**
     * 获取被销毁的敌机列表
     * @return 被销毁的敌机列表
     */
    public static List<AbstractAircraft> getDestroyedAircrafts() {
        return destroyedAircrafts;
    }
    
    /**
     * 清空被销毁的敌机列表
     */
    public static void clearDestroyedAircrafts() {
        destroyedAircrafts.clear();
    }
}