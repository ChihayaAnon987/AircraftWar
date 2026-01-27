package edu.hitsz.prop;

public class FireSupplyFactory implements PropFactory {
    @Override
    public BaseProp createProp(int locationX, int locationY, int speedX, int speedY) {
        return new FireSupply(locationX, locationY, speedX, speedY);
    }
}