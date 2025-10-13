package edu.hitsz.aircraft;

public interface AircraftFactory {
    AbstractAircraft createAircraft(int locationX, int locationY, int speedX, int speedY, int hp);
}