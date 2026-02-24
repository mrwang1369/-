package com.pethealth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethealth.handler.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/**
 * Redis服务类 - 封装Redis常用操作
 * 支持对象序列化，简化缓存使用
 */
@Service
@Slf4j
public class RedisService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // ==================== 字符串操作 =====================

    /**
     * 设置字符串值
     */
    public void set(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("Redis设置字符串失败 key: {}", key, e);
            throw new BusinessException("Redis操作失败");
        }
    }

    /**
     * 设置字符串值并指定过期时间
     */
    public void set(String key, String value, long timeout, TimeUnit timeUnit) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        } catch (Exception e) {
            log.error("Redis设置字符串失败 key: {}", key, e);
            throw new BusinessException("Redis操作失败");
        }
    }

    /**
     * 获取字符串值
     */
    public String get(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis获取字符串失败 key: {}", key, e);
            throw new BusinessException("Redis操作失败");
        }
    }

    // ==================== 对象操作 ====================

    /**
     * 设置对象值（JSON序列化）
     */
    public <T> void setObject(String key, T obj) {
        try {
            // 直接使用 RedisTemplate 存储对象，无需手动序列化
            redisTemplate.opsForValue().set(key, obj);
        } catch (Exception e) {
            log.error("Redis设置对象失败 key: {}", key, e);
            throw new BusinessException("Redis操作失败");
        }
    }

    /**
     * 设置对象值并指定过期时间（使用RedisTemplate 自动序列化）
     */
    public <T> void setObject(String key, T obj, long timeout, TimeUnit timeUnit) {
        try {
            // 直接使用 RedisTemplate 存储对象，无需手动序列化
            redisTemplate.opsForValue().set(key, obj, timeout, timeUnit);
        } catch (Exception e) {
            log.error("Redis设置对象失败 key: {}", key, e);
            throw new BusinessException("Redis操作失败");
        }
    }

    /**
     * 获取对象值（使用 RedisTemplate 自动反序列化）
     */
    public <T> T getObject(String key, Class<T> clazz) {
        try {
            // 直接使用 RedisTemplate 获取对象，无需手动反序列化
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj == null) {
                return null;
            }
            // 强制转换为目标类型
            return clazz.cast(obj);
        } catch (Exception e) {
            log.error("Redis获取对象失败 key: {}", key, e);
            throw new BusinessException("Redis操作失败");
        }
    }

    // ==================== 过期时间操作 ====================

    /**
     * 设置过期时间
     */
    public boolean expire(String key, long timeout, TimeUnit timeUnit) {
        try {
            Boolean result = redisTemplate.expire(key, timeout, timeUnit);
            return result != null && result;
        } catch (Exception e) {
            log.error("Redis设置过期时间失败 key: {}", key, e);
            throw new BusinessException("Redis操作失败");
        }
    }

    /**
     * 获取剩余过期时间
     */
    public long getExpire(String key, TimeUnit timeUnit) {
        try {
            Long expire = redisTemplate.getExpire(key, timeUnit);
            return expire != null ? expire : -2;
        } catch (Exception e) {
            log.error("Redis获取过期时间失败 key: {}", key, e);
            throw new BusinessException("Redis操作失败");
        }
    }

    // ==================== 存在性检查 ====================

    /**
     * 检查key是否存在
     */
    public boolean hasKey(String key) {
        try {
            Boolean result = redisTemplate.hasKey(key);
            return result != null && result;
        } catch (Exception e) {
            log.error("Redis检查key存在性失败 key: {}", key, e);
            throw new BusinessException("Redis操作失败");
        }
    }

    // ==================== 删除操作 ====================

    /**
     * 删除key
     */
    public boolean delete(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            return result != null && result;
        } catch (Exception e) {
            log.error("Redis删除key失败 key: {}", key, e);
            throw new BusinessException("Redis操作失败");
        }
    }

    /**
     * 批量删除key
     */
    public long delete(String... keys) {
        try {
            Long result = redisTemplate.delete(java.util.Arrays.asList(keys));
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("Redis批量删除key失败", e);
            throw new BusinessException("Redis操作失败");
        }
    }

    // ==================== 递增递减操作 ====================

    /**
     * 递增
     */
    public long increment(String key, long delta) {
        try {
            Long result = stringRedisTemplate.opsForValue().increment(key, delta);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("Redis递增操作失败 key: {}", key, e);
            throw new BusinessException("Redis操作失败");
        }
    }

    /**
     * 递减
     */
    public long decrement(String key, long delta) {
        try {
            Long result = stringRedisTemplate.opsForValue().decrement(key, delta);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("Redis递减操作失败 key: {}", key, e);
            throw new BusinessException("Redis操作失败");
        }
    }

    // ==================== 哈希操作 ====================

    /**
     * 设置哈希字段值
     */
    public void hset(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
        } catch (Exception e) {
            log.error("Redis设置哈希字段失败 key: {}, field: {}", key, field, e);
            throw new BusinessException("Redis操作失败");
        }
    }

    /**
     * 获取哈希字段值
     */
    public Object hget(String key, String field) {
        try {
            return redisTemplate.opsForHash().get(key, field);
        } catch (Exception e) {
            log.error("Redis获取哈希字段失败 key: {}, field: {}", key, field, e);
            throw new BusinessException("Redis操作失败");
        }
    }

    // ==================== 缓存键生成方法 ====================

    /**
     * 生成宠物品种字典的缓存key
     */
    public String getPetBreedCacheKey() {
        return "cache:pet:breed:dict";
    }

    /**
     * 生成用户信息的缓存key
     */
    public String getUserCacheKey(Long userId) {
        return String.format("cache:user:info:%d", userId);
    }

    /**
     * 生成宠物信息的缓存key
     */
    public String getPetCacheKey(Long petId) {
        return String.format("cache:pet:info:%d", petId);
    }
}
