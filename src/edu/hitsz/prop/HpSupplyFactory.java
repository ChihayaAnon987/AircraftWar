package edu.hitsz.prop;

public class HpSupplyFactory implements PropFactory {
    @Override
    public BaseProp createProp(int locationX, int locationY, int speedX, int speedY) {
        return new HpSupply(locationX, locationY, speedX, speedY);
    }
}