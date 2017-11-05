package com.suwei.miaosha.Controller;

import com.github.pagehelper.PageInfo;
import com.suwei.miaosha.Common.ResponseStatusCode;
import com.suwei.miaosha.Common.ServerResponse;
import com.suwei.miaosha.Entity.Seckill;
import com.suwei.miaosha.Service.SeckillService;
import com.suwei.miaosha.Vo.Exposer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
}
