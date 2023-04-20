package com.example.client;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.bstek.urule.Utils;
import com.bstek.urule.model.rete.JsonUtils;
import com.bstek.urule.runtime.KnowledgePackage;
import com.bstek.urule.runtime.KnowledgeSession;
import com.bstek.urule.runtime.KnowledgeSessionFactory;
import com.bstek.urule.runtime.service.KnowledgeService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


@RestController
@ResponseBody
public class RuleController {
    @GetMapping("rule")
    public String rule(@RequestParam BigDecimal threeDay,
                       @RequestParam BigDecimal sevenDay,
                       @RequestParam BigDecimal fifteenDay,
                       @RequestParam BigDecimal oneDay
                       ) throws IOException {
        //创建一个KnowledgeSession对象
        KnowledgeService knowledgeService = (KnowledgeService) Utils.getApplicationContext().getBean(KnowledgeService.BEAN_ID);
        KnowledgePackage knowledgePackage = knowledgeService.getKnowledge("aaa/bag");
        KnowledgeSession session = KnowledgeSessionFactory.newKnowledgeSession(knowledgePackage);
        //获取规则参数
        Map<String, String> parameters = knowledgePackage.getParameters();
        System.out.println(parameters);
        Map<String, Object> param = new HashMap();
        param.put("threeDay", threeDay);
        param.put("sevenDay", sevenDay);
        param.put("fifteenDay", fifteenDay);
        param.put("oneDay", oneDay);
        TimeInterval timer = DateUtil.timer();
        for (int i = 0; i < 100000; i++) {
            session.fireRules(param);
        }
        long interval = timer.interval();//花费毫秒数
        timer.intervalRestart();//返回花费时间，并重置开始时间
        timer.intervalMinute();//花费分钟数
        System.out.println(interval);
        Boolean result = (Boolean) session.getParameter("result");
        System.out.println(result);
        return String.valueOf(result);
    }
}
