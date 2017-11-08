package com.suwei.miaosha.Service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.suwei.miaosha.Common.MD5Util;
import com.suwei.miaosha.Common.ResponseStatusCode;
import com.suwei.miaosha.Common.SeckillStatusCode;
import com.suwei.miaosha.Common.ServerResponse;
import com.suwei.miaosha.Dao.SeckillMapper;
import com.suwei.miaosha.Dao.SuccesskilledMapper;
import com.suwei.miaosha.Dao.cache.RedisDao;
import com.suwei.miaosha.Entity.Seckill;
import com.suwei.miaosha.Entity.Successkilled;
import com.suwei.miaosha.Entity.SuccesskilledKey;
import com.suwei.miaosha.Exception.RepeatKillException;
import com.suwei.miaosha.Exception.SeckillCloseException;
import com.suwei.miaosha.Exception.SeckillException;
import com.suwei.miaosha.Redis.Cache;
import com.suwei.miaosha.Service.SeckillService;
import com.suwei.miaosha.Vo.Exposer;
import com.suwei.miaosha.Vo.SeckillExecution;
import com.suwei.miaosha.Vo.SuccessSecKilledWithSeckill;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.MapUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auhter : suwei
 * @Descprition : 秒杀接口的实现
 * @Date : Created in 15:27 2017\11\5 0005
 * @Modified By :
 */
@Service("seckillService")
public class SeckillServiceImpl implements SeckillService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillMapper seckillMapper;

    @Autowired
    private SuccesskilledMapper successkilledMapper;


    @Autowired
    private RedisDao redisDao;

    /**
     * 查询全部秒杀的商品
     *
     * @return
     */
    @Override
    public ServerResponse<PageInfo> getSeckillList(int pageNum, int pageSize ) {
        //startPage--start
        PageHelper.startPage(pageNum,pageSize);

        //填充自己的sql查询逻辑
        List<Seckill> seckillList = seckillMapper.query();
        PageInfo pageResult = new PageInfo(seckillList);
        pageResult.setList(seckillList);

        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * 查询单个秒杀商品
     * @param seckillId
     * @return
     */
    @Override
    public ServerResponse<Seckill> getSeckillById(Long seckillId) {
        Seckill seckill = seckillMapper.selectByPrimaryKey(seckillId);
        if(seckill == null){
            return ServerResponse.createByErrorMsg("该秒杀商品不存在");
        }
        return ServerResponse.createBySuccess(seckill);
    }

    /**
     * 暴露秒杀的接口地址：秒杀开启时输出接口地址，否则输出系统时间和秒杀时间
     *
     * @param seckillId
     */
    @Override
    public ServerResponse<Exposer> exportSeckillUrl(Long seckillId) {
        Seckill seckill = redisDao.getSeckill(seckillId);
        //如果redis中没有缓存
        if(seckill == null){
            seckill = seckillMapper.selectByPrimaryKey(seckillId);
            //如果数据库中没有该秒杀商品
            if(seckill == null){
                Exposer exposer = new Exposer(false, seckillId);
                return ServerResponse.createByError(exposer);
            }
            redisDao.setSeckill(seckillId,seckill);
        }

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //系统当前时间
        Date now = new Date();

        if(now.getTime() < startTime.getTime() || now.getTime() > endTime.getTime()){
            return ServerResponse.createByError(new Exposer(false,seckillId,now.getTime(),startTime.getTime(),endTime.getTime()));
        }

        String md5 = MD5Util.MD5EncodeUtf8(seckillId.toString());
        return ServerResponse.createBySuccess(new Exposer(true,md5,seckillId));
    }

    /**
     * 执行秒杀操作
     *
     * @param successkilledKey
     * @param md5
     */
    @Override
    public ServerResponse<SeckillExecution> executeSeckill(SuccesskilledKey successkilledKey, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        Long seckillId = successkilledKey.getSeckillId();
        Long userPhone = successkilledKey.getUserPhone();

        ServerResponse response = this.checkSuccessSeckilled(successkilledKey);
        if(!response.isSuccess()){
            return response;
        }
        if(StringUtils.isBlank(md5) || !StringUtils.equals(md5,MD5Util.MD5EncodeUtf8(seckillId.toString()))){
            return ServerResponse.createByErrorMsg("参数错误，请重试！");
        }

        //执行秒杀逻辑：减库存 + 记录购买行为
        Date killDate = new Date();

        Successkilled successkilled = new Successkilled();
        successkilled.setSeckillId(seckillId);
        successkilled.setUserPhone(userPhone);
        successkilled.setCreateTime(killDate);
        successkilled.setState(SeckillStatusCode.SUCCESS.getCode());
        //先记录购买行为
        int insertCount = successkilledMapper.insertSelective(successkilled);
        if(insertCount <= 0){
            //插入失败，服务器内部错误
            return ServerResponse.createByErrorCodeMsg(ResponseStatusCode.INNER_ERROR.getCode(),ResponseStatusCode.INNER_ERROR.getDesc());
        }else {
            // 再减库存
            int updateCount = seckillMapper.reduceNumber(seckillId,killDate);
            if(updateCount <= 0){
                //rowback
               throw new SeckillException("秒杀结束");
            }
            //插入成功，组装SuccessSecKilledWithSeckill对象
            response = this.assembleSuccessSecKilledWithSeckill(successkilledKey);
            if(!response.isSuccess()){
                return response;
            }
            return ServerResponse.createBySuccess(new SeckillExecution(seckillId,SeckillStatusCode.SUCCESS.getCode(),SeckillStatusCode.SUCCESS.getDesc(), (SuccessSecKilledWithSeckill) response.getData()));
        }
    }

    /**
     * 执行秒杀操作by 存储过程
     *
     * @param successkilledKey
     * @param md5
     */
    @Override
    public ServerResponse<SeckillExecution> executeSeckillProcedure(SuccesskilledKey successkilledKey, String md5) {
        Long seckillId = successkilledKey.getSeckillId();
        Long userPhone = successkilledKey.getUserPhone();

        ServerResponse response = this.checkSuccessSeckilled(successkilledKey);
        if(!response.isSuccess()){
            return response;
        }
        if(StringUtils.isBlank(md5) || !StringUtils.equals(md5,MD5Util.MD5EncodeUtf8(seckillId.toString()))){
            return ServerResponse.createByErrorMsg("参数错误，请重试！");
        }

        //执行秒杀逻辑：减库存 + 记录购买行为
        Date killDate = new Date();

        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("seckillId",seckillId);
        paramMap.put("phone",userPhone);
        paramMap.put("killTime",killDate);
        paramMap.put("result",null);

        //执行存储过程，result被赋值
        try {
            seckillMapper.killByProcedure(paramMap);
            //获取result
            int result = (Integer) paramMap.get("result");
            if(result == 1){
                response = this.assembleSuccessSecKilledWithSeckill(successkilledKey);
                if(!response.isSuccess()){
                    return response;
                }

                return ServerResponse.createBySuccess(new SeckillExecution(seckillId,SeckillStatusCode.SUCCESS.getCode(),SeckillStatusCode.SUCCESS.getDesc(),
                        (SuccessSecKilledWithSeckill) response.getData()));
            }
            return ServerResponse.createByErrorCodeMsg(300,"秒杀失败");
        }catch (Exception e){
            logger.error("执行存储过程出错",e);
            return  ServerResponse.createByErrorCodeMsg(ResponseStatusCode.INNER_ERROR.getCode(),"执行存储过程出错");
        }
    }

    /**
     * 检验是否重复秒杀
     * @param successkilledKey
     * @return
     */
    @Override
    public ServerResponse checkSuccessSeckilled(SuccesskilledKey successkilledKey){
        //从redis缓存中取出successkilled
        Successkilled successkilled = redisDao.getSuccesskilled(successkilledKey);
        if(successkilled != null){
            return ServerResponse.createByErrorMsg("请勿重复秒杀");
        }
        //没有successkilled，则从数据库中查询出来
        Successkilled successkilledFromMysql = successkilledMapper.selectByPrimaryKey(successkilledKey);
        if(successkilledFromMysql != null){
            //将successkilledKey缓存进redis中,以便检查是否重复秒杀
            redisDao.setSuccesskilled(successkilledKey,successkilledFromMysql);
            return ServerResponse.createByErrorMsg("请勿重复秒杀");
        }
        return ServerResponse.createBySuccess();
    }

    /**
     * 组装SuccessSecKilledWithSeckill对象
     * @param successkilledKey
     * @return
     */
    private ServerResponse<SuccessSecKilledWithSeckill> assembleSuccessSecKilledWithSeckill (SuccesskilledKey successkilledKey){
        SuccessSecKilledWithSeckill result = new SuccessSecKilledWithSeckill();
        Successkilled successkilled = successkilledMapper.selectByPrimaryKey(successkilledKey);
        if(successkilled == null){
            return ServerResponse.createByErrorMsg("没有对应的秒杀明细");
        }
        result.setSeckillId(successkilled.getSeckillId());
        result.setCreateTime(successkilled.getCreateTime());
        result.setUserPhone(successkilled.getUserPhone());

        //查询seckillId对应的商品详情
        Seckill seckill = seckillMapper.selectByPrimaryKey(successkilled.getSeckillId());
        result.setSeckill(seckill);

        return ServerResponse.createBySuccess(result);
    }
}
