package edu.hitsz.prop;

import edu.hitsz.aircraft.AbstractAircraft;

import java.util.ArrayList;
import java.util.List;

/**
 * 炸弹主题类，实现观察者模式的主题接口
 */
public class BombSubject implements Subject {
    private final List<Observer> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    /**
     * 炸弹效果：通知所有观察者炸弹已激活
     */
    public void bombEffect() {
        notifyObservers();
    }
}