package edu.hitsz.prop;

import edu.hitsz.bullet.BaseBullet;

/**
 * 子弹观察者类，实现观察者接口
 */
public class BulletObserver implements Observer {
    private final BaseBullet bullet;

    public BulletObserver(BaseBullet bullet) {
        this.bullet = bullet;
    }

    @Override
    public void update() {
        // 子弹在炸弹效果下直接消失
        bullet.vanish();
    }

    public BaseBullet getBullet() {
        return bullet;
    }
}