package edu.hitsz.prop;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;

import java.util.List;

public class FireSupply extends BaseProp {
    public FireSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }


    @Override
    public void effect(HeroAircraft heroAircraft, List<AbstractAircraft> eliteEnemies, List<BaseBullet> enemyBullets) {
        System.out.println("FireSupply active!");
    }
}