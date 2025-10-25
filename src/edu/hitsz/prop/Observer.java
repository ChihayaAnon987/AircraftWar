package edu.hitsz.prop;

/**
 * 观察者模式 - 观察者接口
 */
public interface Observer {
    /**
     * 更新方法，当主题状态改变时调用
     */
    void update();
}