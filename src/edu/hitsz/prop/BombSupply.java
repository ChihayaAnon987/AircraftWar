package edu.hitsz.prop;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.music.MusicPlayer;
import edu.hitsz.bullet.BaseBullet;

import java.util.List;

public class BombSupply extends BaseProp {
    public BombSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void effect(HeroAircraft heroAircraft, List<AbstractAircraft> enemyAircrafts, List<BaseBullet> enemyBullets) {
        System.out.println("BombSupply active!");
        
        // 播放炸弹爆炸音效
        MusicPlayer.getMusicPlayer().playMusic("src/videos/bomb_explosion.wav");
        
        // 使用观察者模式处理炸弹效果
        BombSubject bombSubject = new BombSubject();
        
        // 为每个敌机创建观察者并添加到主题中
        for (AbstractAircraft enemy : enemyAircrafts) {
            BombObserver observer = new BombObserver(enemy);
            bombSubject.addObserver(observer);
        }
        
        // 为每个敌机子弹创建观察者并添加到主题中
        for (BaseBullet bullet : enemyBullets) {
            BombObserver observer = new BombObserver(bullet);
            bombSubject.addObserver(observer);
        }
        
        // 激活炸弹效果
        bombSubject.bombEffect();
    }
}