package com.suwei.miaosha.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @Auhter : suwei
 * @Descprition :
 * @Date : Created in 11:33
 * @Modified By :
 */
@Controller
public class TemplateController {
    /**
     * 返回html模板.
     */

    @RequestMapping("/helloHtml")
    public String helloHtml(Map<String,Object> map){

        map.put("hello","from TemplateController.helloHtml");
        return"/helloHtml";
    }
}
