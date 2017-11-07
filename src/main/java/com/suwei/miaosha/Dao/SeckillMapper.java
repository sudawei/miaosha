package com.suwei.miaosha.Dao;

import com.suwei.miaosha.Entity.Seckill;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;


public interface SeckillMapper {
    int deleteByPrimaryKey(Long seckillId);

    int insert(Seckill record);

    int insertSelective(Seckill record);

    int updateByPrimaryKeySelective(Seckill record);

    int updateByPrimaryKey(Seckill record);

    /**
     * 减库存
     * @param seckillId
     * @param killTime
     * @return
     */
    int reduceNumber(@Param("seckillId")Long seckillId, @Param("killTime")Date killTime);

    /**
     * 根据id查询秒杀商品
     * @param seckillId
     * @return
     */
    Seckill selectByPrimaryKey(Long seckillId);


    /**
     * 根据偏移量查询秒杀商品
     * @return
     */
    List<Seckill> query();

    void killByProcedure(Map<String,Object> paramMap);
}