package com.example.client;

import com.bstek.urule.Utils;
import com.bstek.urule.runtime.KnowledgePackage;
import com.bstek.urule.runtime.KnowledgeSession;
import com.bstek.urule.runtime.KnowledgeSessionFactory;
import com.bstek.urule.runtime.service.KnowledgeService;

import java.io.IOException;
import java.util.Map;

public class UruleUtils {

    /**
     * 得到urule参数
     *
     * @param knowledgePackageName 知识包名称
     * @return {@code Map<String, String>}
     * @throws IOException ioexception
     */
    public static Map<String, String> getUruleParameters(String knowledgePackageName) throws IOException {
        KnowledgeService knowledgeService = (KnowledgeService) Utils.getApplicationContext().getBean(KnowledgeService.BEAN_ID);
        KnowledgePackage knowledgePackage = knowledgeService.getKnowledge(knowledgePackageName);
        KnowledgeSession session = KnowledgeSessionFactory.newKnowledgeSession(knowledgePackage);
        return knowledgePackage.getParameters();
    }

    /**
     * 调用规则
     *
     * @param knowledgePackageName 知识包名称
     * @param param                参数
     * @param returnKey            返回键
     * @return {@code Boolean}
     * @throws IOException ioexception
     */
    public static Object callUrule(String knowledgePackageName,
                              Map<String, Object> param,
                              String returnKey
    ) throws IOException {
        KnowledgeService knowledgeService = (KnowledgeService) Utils.getApplicationContext().getBean(KnowledgeService.BEAN_ID);
        KnowledgePackage knowledgePackage = knowledgeService.getKnowledge(knowledgePackageName);
        KnowledgeSession session = KnowledgeSessionFactory.newKnowledgeSession(knowledgePackage);
        session.fireRules(param);
        return session.getParameter(returnKey);
    }

}
