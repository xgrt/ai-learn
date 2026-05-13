package com.xgrt.aiCodeLearn.ai.factory;

import com.xgrt.aiCodeLearn.ai.AiHelperService;
import com.xgrt.aiCodeLearn.redis.RedisUtils;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AiHelperServiceFactory implements ChatMemoryStore {
    private final ChatModel qwenChatModel;
    private final RedisUtils redisUtils;

    @Bean
    public AiHelperService createAiHelperService() {
        //创建一个记忆对象，可以不创建，没有记忆功能，当前配置是10条消息，会有用户消息隔离
        ChatMemory chatMemory= MessageWindowChatMemory.builder()
                .chatMemoryStore(new AiHelperServiceFactory(qwenChatModel,redisUtils))
                .maxMessages(10)
                .id("xgrtTestAi")
                .build();

        //构建一个AiServices对象，用于创建AiHelperService接口的实现类
        return AiServices.builder(AiHelperService.class)
                .chatModel(qwenChatModel)
                .chatMemory(chatMemory)
                .chatMemoryProvider(memoryId->MessageWindowChatMemory.withMaxMessages(10))
                .build();//运用了反射的机制，创建了AiHelperService接口的实现类
    }

    /**
     * 获取聊天记录
     * @param memoryId 记忆Id
     * @return 聊天记录
     */
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        //从Redis中获取聊天记录
        Object o = redisUtils.get(memoryId.toString());
        if (o == null){
            return new ArrayList<>();
        }
        String str = o.toString();
        return ChatMessageDeserializer.messagesFromJson(str);
    }
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        //将聊天记录存入Redis
        redisUtils.set(memoryId.toString(),ChatMessageSerializer.messagesToJson(list));
    }

    @Override
    public void deleteMessages(Object memoryId) {
        //删除Redis中的聊天记录
        redisUtils.del(memoryId.toString());
    }
}
