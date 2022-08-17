package com.swhy.swhypractice.utils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

/**
 * Redis配置类的编写，各项属性对应配置文件中Redis的配置
 */
@Component
public class RedisConfig{
    @Resource
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 读取数据
     */
    public String getString(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 写入数据
     */
    public boolean setString(final String key, String value) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 更新数据
     */
    public boolean getAndSetString(final String key, String value) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().getAndSet(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除数据
     */
    public boolean delete(final String key) {
        boolean result = false;
        try {
            redisTemplate.delete(key);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }




}
