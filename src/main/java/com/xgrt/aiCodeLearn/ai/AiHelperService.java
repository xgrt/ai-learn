package com.xgrt.aiCodeLearn.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;

import java.util.List;

//@AiService //不推荐使用，配置困难
public interface AiHelperService {
    @SystemMessage(fromResource = "system-prompt.txt")
    String chat(String userMessage);

    @SystemMessage(fromResource = "system-prompt.txt")
    String chat(@MemoryId Long memoryId, String userMessage);

    @SystemMessage(fromResource = "system-prompt.txt")
    Report chatForReport(String userMessage);

    /**
     * 学习报告
     * record：
     * @param name
     * @param suggestionList
     */
    record Report(String name, List<String> suggestionList){};
}
