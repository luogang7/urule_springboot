package com.example.client;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.bstek.urule.Utils;
import com.bstek.urule.model.rete.JsonUtils;
import com.bstek.urule.runtime.KnowledgePackage;
import com.bstek.urule.runtime.KnowledgeSession;
import com.bstek.urule.runtime.KnowledgeSessionFactory;
import com.bstek.urule.runtime.service.KnowledgeService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;


@RestController
@ResponseBody
public class RuleController {
    @GetMapping("noStockUpRule")
    public String noStockUpRule(@RequestParam BigDecimal threeDay,
                                @RequestParam BigDecimal sevenDay,
                                @RequestParam BigDecimal fifteenDay,
                                @RequestParam BigDecimal oneDay,
                                @RequestParam String saleattrParam,
                                @RequestParam String devTime
    ) throws IOException {
        //创建一个KnowledgeSession对象
        KnowledgeService knowledgeService = (KnowledgeService) Utils.getApplicationContext().getBean(KnowledgeService.BEAN_ID);
        KnowledgePackage knowledgePackage = knowledgeService.getKnowledge("purchase_rule/no_stock_up");
        KnowledgeSession session = KnowledgeSessionFactory.newKnowledgeSession(knowledgePackage);
        //获取规则参数
        Map<String, String> parameters = knowledgePackage.getParameters();
        System.out.println(JSONUtil.toJsonStr(parameters));
        Map<String, Object> param = new HashMap();
        List<String> list = new ArrayList<>();
        list.add("春");
        list.add("夏");
        list.add("秋");
        list.add("冬");
        param.put("threeDay", threeDay);
        param.put("sevenDay", sevenDay);
        param.put("fifteenDay", fifteenDay);
        param.put("oneDay", oneDay);
        param.put("sale_attr_param", saleattrParam);
        param.put("sale_attr_rs", list);
        param.put("devTime", devTime);
        TimeInterval timer = DateUtil.timer();
//        for (int i = 0; i < 100000; i++) {
        session.fireRules(param);
//        }
        long interval = timer.interval();//花费毫秒数
        timer.intervalRestart();//返回花费时间，并重置开始时间
        timer.intervalMinute();//花费分钟数
        System.out.println("执行毫秒数" + interval);
        Boolean result = (Boolean) session.getParameter("result");
        System.out.println(result);
        return String.valueOf(result);
    }


    @GetMapping("productStatusRule")
    public String productStatusRule(@RequestParam Integer threedaySalesVolume,@RequestParam String saleAttribute) throws IOException {

        Map<String, Object> param = new HashMap();
        param.put("threedaySalesVolume", threedaySalesVolume);
        param.put("developTime", DateUtil.date());
        param.put("saleAttribute",saleAttribute);
        TimeInterval timer = DateUtil.timer();
        String result = (String) UruleUtils.callUrule("product_rule/product_status", param, "result");
        long interval = timer.interval();//花费毫秒数
        System.out.println("执行毫秒数" + interval);
        System.out.println(result);
        return String.valueOf(result);
    }

}
