package com.xgrt.aiCodeLearn.ai;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiHelperServiceTest {
    @Resource
    private AiHelperService aiHelperService;

    @Test
    void chatTest() {
        String result = aiHelperService.chat("你好我是鑫哥rt，你是谁，为你取一个名称");
        System.out.println(result);
    }

    @Test
    void chatTestMemory() {
        String result = aiHelperService.chat("你好我是鑫哥rt，你是谁，为你自己取一个比较好记的中文名称，我们开始聊吧");
        System.out.println(result);

        result = aiHelperService.chat("我叫什么，你为自己取得名称是什么");
        System.out.println(result);

        result = aiHelperService.chat("再以你中文名称给自己取一个英文名称吧！");
        System.out.println(result);
    }

    @Test
    void chatTestConstruct() {
        String message="我是学习时长两年半的Java开发者，学完了 MySQL、SSM、SpringBoot，正在学习 springCloud和AI，给我个学习报告";
        AiHelperService.Report report = aiHelperService.chatForReport(message);
        System.out.println(report);
    }

}