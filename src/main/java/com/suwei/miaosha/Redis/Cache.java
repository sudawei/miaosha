package com.suwei.miaosha.Redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Auhter : suwei
 * @Descprition : 操作redis的基本类
 * @Date : Created in 10:24 2017\11\7 0007
 * @Modified By :
 */
@Component
public class Cache {
    @Autowired
    private RedisTemplate<String,Object> redisTemplateObject;

    //opsForValue相关操作
    public void opsForValueSet(String key ,Object value){
        redisTemplateObject.opsForValue().set(key, value);
    }
    public void opsForValueSet(String key ,Object value,long timeout,TimeUnit unit){
        redisTemplateObject.opsForValue().set(key, value,timeout,unit);
    }
    public Object opsForValueGet(String key){
        return redisTemplateObject.opsForValue().get(key);
    }

    //opsForHash相关操作
    public void opsForHashPut(String key ,String hashKey ,Object value){
        redisTemplateObject.opsForHash().put(key, hashKey, value);
    }
    public void opsForHashDel(String key ,String hashKey){
        redisTemplateObject.opsForHash().delete(key, hashKey);
    }
    public Boolean opsForHashHasKey(String key ,String hashKey){
        return redisTemplateObject.opsForHash().hasKey(key, hashKey);
    }
    public Object opsForHashGet(String key ,String hashKey){
        return redisTemplateObject.opsForHash().get(key, hashKey);
    }
    public Set<Object> opsForHashKeys(String key){
        return redisTemplateObject.opsForHash().keys(key);
    }
    public List<?> opsForHashValues(String key){
        return redisTemplateObject.opsForHash().values(key);
    }

    //opsForSet相关操作
    public void opsForSetAdd(String key,Object value){
        redisTemplateObject.opsForSet().add(key, value);
    }
    public Set<?> opsForSetRange(String key){
        return redisTemplateObject.opsForSet().members(key);
    }
    public void opsForSetDel(String key,Object value){
        redisTemplateObject.opsForSet().remove(key, value);
    }

    //opsForList相关操作
    public void opsForListRightPush(String groupId,Object value){
        redisTemplateObject.opsForList().rightPush(groupId, value);
    }
    public Object opsForListLeftPop(String groupId){
        return redisTemplateObject.opsForList().leftPop(groupId);
    }
    public Long opsForListSize(String groupId){
        return redisTemplateObject.opsForList().size(groupId);
    }
    public List<?> opsForListRange(String groupId, final long start, final long end){
        return redisTemplateObject.opsForList().range(groupId, start, end);
    }
    //基本操作 删除、存在
    public Boolean hasKey(String key){
        return redisTemplateObject.hasKey(key);
    }
    public void delKey(String key){
        redisTemplateObject.delete(key);
    }
    public long ttl(String key, final TimeUnit timeUnit){
        return redisTemplateObject.getExpire(key,timeUnit);
    }
    public void expire(String key, long timeout, TimeUnit unit){
        redisTemplateObject.expire(key, timeout, unit);
    }
}
