package edu.hitsz.aircraft;

public class SuperEliteEnemyFactory implements AircraftFactory {
    @Override
    public AbstractAircraft createAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        return new SuperEliteEnemy(locationX, locationY, speedX, speedY, hp);
    }
}