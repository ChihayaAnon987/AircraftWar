package edu.hitsz.prop;

public interface PropFactory {
    BaseProp createProp(int locationX, int locationY, int speedX, int speedY);
}