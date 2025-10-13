package edu.hitsz.aircraft;

import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.prop.FireSupply;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HeroAircraftTest {

    private HeroAircraft heroAircraft;

    @BeforeEach
    void setUp() {
        // 使用反射重置单例实例以便每个测试都从干净的状态开始
        try {
            Field instanceField = HeroAircraft.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (Exception e) {
            fail("无法重置单例实例: " + e.getMessage());
        }
        
        heroAircraft = HeroAircraft.getInstance();
    }

    @Test
    void TestGetInstance() {
        // 测试单例是否正确创建
        assertNotNull(heroAircraft, "英雄机实例不应为null");
        
        // 测试多次获取是否为同一实例
        HeroAircraft anotherInstance = HeroAircraft.getInstance();
        assertSame(heroAircraft, anotherInstance, "多次调用getInstance应返回相同实例");
        
        // 测试初始HP是否正确设置
        assertEquals(1000, heroAircraft.getHp(), "英雄机初始HP应为1000");
    }

    @Test
    void TestShoot() {
        // 测试射击功能
        List<BaseBullet> bullets = heroAircraft.shoot();
        
        // 检查返回的子弹列表不为null
        assertNotNull(bullets, "射击应返回子弹列表");
        
        // 检查默认情况下发射一颗子弹
        assertEquals(1, bullets.size(), "默认应发射1颗子弹");
        
        // 检查子弹类型
        BaseBullet bullet = bullets.get(0);
        assertNotNull(bullet, "子弹不应为null");
    }

    @Test
    void TestIncreaseHp() {
        // 先减少HP再测试增加
        heroAircraft.decreaseHp(500);
        int currentHp = heroAircraft.getHp();
        assertEquals(500, currentHp, "HP应该减少到500");
        
        // 增加HP
        heroAircraft.increaseHp(300);
        assertEquals(800, heroAircraft.getHp(), "HP应该增加到800");
        
        // 测试超过最大HP的情况
        heroAircraft.increaseHp(500);
        assertEquals(1000, heroAircraft.getHp(), "HP不应该超过最大值1000");
    }

    
    @Test
    void TestCrash() {
        // 创建一个火力道具进行碰撞测试
        FireSupply fireSupply = new FireSupply(200, 300, 0, 0);
        
        // 设置英雄机的位置
        heroAircraft.setLocation(200, 300);
        
        // 通过反射设置英雄机的宽高，避免依赖图片资源
        try {
            Field widthField = AbstractFlyingObject.class.getDeclaredField("width");
            Field heightField = AbstractFlyingObject.class.getDeclaredField("height");
            widthField.setAccessible(true);
            heightField.setAccessible(true);
            widthField.set(heroAircraft, 100);
            heightField.set(heroAircraft, 100);
        } catch (Exception e) {
            fail("无法设置英雄机宽高: " + e.getMessage());
        }
        
        // 测试发生碰撞的情况（两物体位置重叠）
        boolean crashResult = heroAircraft.crash(fireSupply);
        assertTrue(crashResult, "当英雄机与火力道具位置重叠时应该发生碰撞");
    }
}