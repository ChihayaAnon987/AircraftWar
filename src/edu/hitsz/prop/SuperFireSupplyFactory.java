package edu.hitsz.prop;

public class SuperFireSupplyFactory implements PropFactory {
    @Override
    public BaseProp createProp(int locationX, int locationY, int speedX, int speedY) {
        return new SuperFireSupply(locationX, locationY, speedX, speedY);
    }
}