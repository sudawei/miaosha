package com.suwei.miaosha.Dao;

import com.suwei.miaosha.Entity.Successkilled;
import com.suwei.miaosha.Entity.SuccesskilledKey;
import org.springframework.stereotype.Repository;

@Repository
public interface SuccesskilledMapper {
    int deleteByPrimaryKey(SuccesskilledKey key);

    int insert(Successkilled record);


    int updateByPrimaryKeySelective(Successkilled record);

    int updateByPrimaryKey(Successkilled record);

    /**
     * 插入购买明细，可过滤重复
     * @param record
     * @return
     */
    int insertSelective(Successkilled record);


    /**
     * 根据联合主键查询Successkilled并且携带秒杀实体对象
     * @param key
     * @return
     */
    Successkilled selectByPrimaryKey(SuccesskilledKey key);


}