package com.suwei.miaosha.Controller;

import com.github.pagehelper.PageInfo;
import com.suwei.miaosha.Common.ResponseStatusCode;
import com.suwei.miaosha.Common.ServerResponse;
import com.suwei.miaosha.Entity.Seckill;
import com.suwei.miaosha.Entity.SuccesskilledKey;
import com.suwei.miaosha.Exception.SeckillException;
import com.suwei.miaosha.Service.SeckillService;
import com.suwei.miaosha.Vo.Exposer;
import com.suwei.miaosha.Vo.SeckillExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @Auhter : suwei
 * @Descprition :
 * @Date : Created in 17:23 2017\11\5 0005
 * @Modified By :
 */
@RestController
@RequestMapping("seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;


    @GetMapping("/list")
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                       @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        //获取列表页
        ServerResponse<PageInfo> seckillList = seckillService.getSeckillList(pageNum,pageSize);
        return seckillList;
    }

    /**
     * 查询单个秒杀商品
     * @param seckillId
     * @return
     */
    @GetMapping("/{seckillId}/detail")
    public ServerResponse<Seckill> detail(@PathVariable("seckillId") Long seckillId) {
        if(seckillId == null){
            return ServerResponse.createByErrorCodeMsg(ResponseStatusCode.NEED_LOGIN.getCode(),"未登录，请先先去登录");
        }
        return seckillService.getSeckillById(seckillId);
    }

    /**
     * 暴露秒杀的接口地址：秒杀开启时输出接口地址，否则输出系统时间和秒杀时间
     * @param seckillId
     * @return
     */
    @GetMapping("/{seckillId}/exposer")
    public ServerResponse<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
        if(seckillId == null){
            return ServerResponse.createByErrorCodeMsg(ResponseStatusCode.NEED_LOGIN.getCode(),"未登录，请先先去登录");
        }
        return seckillService.exportSeckillUrl(seckillId);
    }

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param md5
     * @return
     * @throws SeckillException
     */
    @GetMapping("/{seckillId}/{md5}/execute")
    public ServerResponse<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId, @PathVariable("md5")String md5) throws SeckillException {
        Long userPhone = 15979305570L;
        System.out.println(seckillId + md5);
        if(userPhone == null){
            return ServerResponse.createByErrorCodeMsg(ResponseStatusCode.NEED_LOGIN.getCode(),"未登录，请先先去登录");
        }
        try {
            ServerResponse<SeckillExecution> response = seckillService.executeSeckill(new SuccesskilledKey(seckillId, userPhone), md5);
            return response;
        }catch (SeckillException e){
            return ServerResponse.createByErrorMsg(e.getMessage());
        }

    }

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param md5
     * @return
     * @throws SeckillException
     */
    @PostMapping("/{seckillId}/{md5}/executeByProcedure")
    public ServerResponse<SeckillExecution> executeByProcedure(@PathVariable("seckillId") Long seckillId, @PathVariable("md5")String md5) throws SeckillException {
        Long userPhone = 15979305570L;
        if(userPhone == null){
            return ServerResponse.createByErrorCodeMsg(ResponseStatusCode.NEED_LOGIN.getCode(),"未登录，请先先去登录");
        }
        try {
            ServerResponse<SeckillExecution> response = seckillService.executeSeckillProcedure(new SuccesskilledKey(seckillId, userPhone), md5);
            return response;
        }catch (SeckillException e){
            return ServerResponse.createByErrorMsg(e.getMessage());
        }

    }

    /**
     * 获取系统当前时间
     * @return
     */
    @GetMapping("/time/now")
    public ServerResponse<Long> time(){
        Date now = new Date();
        return ServerResponse.createBySuccess(now.getTime());
    }
}
