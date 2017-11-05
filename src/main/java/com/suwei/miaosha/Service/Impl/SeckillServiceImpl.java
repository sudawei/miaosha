package com.suwei.miaosha.Service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.suwei.miaosha.Common.MD5Util;
import com.suwei.miaosha.Common.ResponseStatusCode;
import com.suwei.miaosha.Common.SeckillStatusCode;
import com.suwei.miaosha.Common.ServerResponse;
import com.suwei.miaosha.Dao.SeckillMapper;
import com.suwei.miaosha.Dao.SuccesskilledMapper;
import com.suwei.miaosha.Entity.Seckill;
import com.suwei.miaosha.Entity.Successkilled;
import com.suwei.miaosha.Entity.SuccesskilledKey;
import com.suwei.miaosha.Exception.RepeatKillException;
import com.suwei.miaosha.Exception.SeckillCloseException;
import com.suwei.miaosha.Exception.SeckillException;
import com.suwei.miaosha.Service.SeckillService;
import com.suwei.miaosha.Vo.Exposer;
import com.suwei.miaosha.Vo.SeckillExecution;
import com.suwei.miaosha.Vo.SuccessSecKilledWithSeckill;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Auhter : suwei
 * @Descprition : 秒杀接口的实现
 * @Date : Created in 15:27 2017\11\5 0005
 * @Modified By :
 */
@Service("seckillService")
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private SeckillMapper seckillMapper;

    @Autowired
    private SuccesskilledMapper successkilledMapper;



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
        Seckill seckill = seckillMapper.selectByPrimaryKey(seckillId);
        if(seckill == null){
            Exposer exposer = new Exposer(false, seckillId);
            return ServerResponse.createByError(exposer);
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

        // 减库存
        Date killDate = new Date();
        int updateCount = seckillMapper.reduceNumber(seckillId,killDate);
        if(updateCount <= 0){
            return ServerResponse.createByErrorMsg("秒杀已结束");
        }else {
            // 记录购买行为
            Successkilled successkilled = new Successkilled();
            successkilled.setSeckillId(seckillId);
            successkilled.setUserPhone(userPhone);
            successkilled.setCreateTime(killDate);
            successkilled.setState(SeckillStatusCode.SUCCESS.getCode());
            int insertCount = successkilledMapper.insertSelective(successkilled);
            if(insertCount <= 0){
                //插入失败，服务器内部错误
                return ServerResponse.createByErrorCodeMsg(ResponseStatusCode.INNER_ERROR.getCode(),ResponseStatusCode.INNER_ERROR.getDesc());
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
     * 检验是否重复秒杀
     * @param successkilledKey
     * @return
     */
    @Override
    public ServerResponse checkSuccessSeckilled(SuccesskilledKey successkilledKey){
        Successkilled successkilled = successkilledMapper.selectByPrimaryKey(successkilledKey);
        if(successkilled != null){
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
