package com.example.client;

import com.bstek.urule.Utils;
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
                       @RequestParam BigDecimal fifteenDay
                       ) throws IOException {
        //创建一个KnowledgeSession对象
        KnowledgeService knowledgeService = (KnowledgeService) Utils.getApplicationContext().getBean(KnowledgeService.BEAN_ID);
        KnowledgePackage knowledgePackage = knowledgeService.getKnowledge("aaa/bag");
        KnowledgeSession session = KnowledgeSessionFactory.newKnowledgeSession(knowledgePackage);

        Map<String, Object> param = new HashMap();
        param.put("threeDay", threeDay);
        param.put("sevenDay", sevenDay);
        param.put("fifteenDay", fifteenDay);
        session.fireRules(param);

        Boolean result = (Boolean) session.getParameter("result");
        System.out.println(result);
        return String.valueOf(result);
    }
}
