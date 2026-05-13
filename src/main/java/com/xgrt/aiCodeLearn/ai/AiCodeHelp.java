package com.xgrt.aiCodeLearn.ai;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiCodeHelp {

    private final QwenChatModel qwenChatModel; //也可以是 ChatModel，但是推荐使用精确的类型，防止注入错误

    private  static final String SYSTEM_MESSAGE = """
            你是一位专业统计学家助手。你需要掌握以下核心能力：
            1. 统计学专业知识
               - 精通统计学术语（如均值、方差、假设检验等）
               - 熟悉各类统计分布（正态分布、泊松分布等）
               - 掌握置信区间计算方法
               - 擅长概率计算与分析
               - 具备假设检验实施能力
            
            2. 数据可视化技能
               - 能正确选择和应用统计图表（柱状图、箱线图等）
               - 确保图表标注规范准确
            
            3. 问题解决流程
               - 收到统计相关问题后，需按照以下步骤处理：
                 a) 明确问题涉及的统计概念
                 b) 识别可用的数据来源
                 c) 选择适当的统计方法
                 d) 执行计算分析
                 e) 验证结果合理性
            
            4. 输出规范
               - 所有计算结果必须标注数据来源
               - 需说明采用的统计方法和假设条件
               - 如涉及估算，必须标明置信区间
            
            处理请求请按照以下步骤响应：
            1. 确认问题涉及的关键统计指标
            2. 列举可能的官方数据来源（如央行数据库）
            3. 说明拟采用的统计方法
            4. 提供初步估算结果及误差范围
            5. 标注数据获取限制和假设条件
            
            注意：所有分析必须基于可验证的统计数据，禁止主观臆测。如遇数据缺失情况，需明确说明信息缺口及可能影响。
            """;


    /**
     * 简单对话
     * @param message 用户输入内容
     * @return AI输出内容
     */
    public String chat(String message) {
//        SystemMessage systemMessage=new SystemMessage(SYSTEM_MESSAGE);//这样也可
        SystemMessage systemMessage=SystemMessage.from(SYSTEM_MESSAGE);
        UserMessage userMessage = UserMessage.from(message);//根据输入创建用户消息
        ChatResponse chatResponse = qwenChatModel.chat(userMessage,systemMessage);//将用户消息传递给模型，获取响应
        AiMessage aiMessage=chatResponse.aiMessage();//从响应中提取AI消息
        log.info("AI输出: {}", aiMessage.toString());//输出AI消息
        return aiMessage.text();//返回AI消息文本内容
    }

    /**
     * 接收UserMessage类型的参数，返回AI的输出
     * @param userMessage 用户输入内容（可以是任意格式，推荐使用List<Message>来传递）
     * @return AI输出内容
     */
    public String chatWithMessage(UserMessage userMessage) {
        ChatResponse chatResponse = qwenChatModel.chat(userMessage);//将用户消息传递给模型，获取响应
        AiMessage aiMessage=chatResponse.aiMessage();//从响应中提取AI消息
        log.info("AI输出: {}", aiMessage.toString());//输出AI消息
        return aiMessage.text();//返回AI消息文本内容
    }

    public GenerationResult callWithMessage() throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("You are a helpful assistant.")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content("你是谁？")
                .build();
        GenerationParam param = GenerationParam.builder()
                // 若没有配置环境变量，请用阿里云百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                // 模型列表：https://help.aliyun.com/model-studio/getting-started/models
                .model("qwen-plus")
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        return gen.call(param);
    }

}
