package edu.hitsz.prop;

public class BombSupplyFactory implements PropFactory {
    @Override
    public BaseProp createProp(int locationX, int locationY, int speedX, int speedY) {
        return new BombSupply(locationX, locationY, speedX, speedY);
    }
}