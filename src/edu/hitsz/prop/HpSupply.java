package edu.hitsz.prop;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;

import java.util.List;

public class HpSupply extends BaseProp {
    private int hpIncrease = 40; // 回复40点血量

    public HpSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void effect(HeroAircraft heroAircraft, List<AbstractAircraft> eliteEnemies, List<BaseBullet> enemyBullets) {
        heroAircraft.increaseHp(hpIncrease);
    }
}