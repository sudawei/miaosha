package com.suwei.miaosha.Controller;

import com.suwei.miaosha.Common.ServerResponse;
import com.suwei.miaosha.Entity.Seckill;
import com.suwei.miaosha.Service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auhter : suwei
 * @Descprition :
 * @Date : Created in 17:23 2017\11\5 0005
 * @Modified By :
 */
@RestController
@RequestMapping("sec")
public class SeckillController {
    @Autowired
    private SeckillService seckillService;

    /**
     * 查询单个秒杀商品
     * @param seckillId
     * @return
     */
    @PostMapping("/select")
    public ServerResponse<Seckill> getSeckillById(Long seckillId) {
        return seckillService.getSeckillById(seckillId);
    }
}
