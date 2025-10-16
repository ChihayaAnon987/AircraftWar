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
    public void effect(HeroAircraft heroAircraft, List<AbstractAircraft> eliteEnemies, List<BaseBullet> enemyBullets) {
        System.out.println("BombSupply active!");
        
        // 播放炸弹爆炸音效
        MusicPlayer.getMusicPlayer().playMusic("src/videos/bomb_explosion.wav");
        
        // 清除场上所有敌机子弹
        for (BaseBullet bullet : enemyBullets) {
            bullet.vanish(); // 使子弹消失
        }
    }
}