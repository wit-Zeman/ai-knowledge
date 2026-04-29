package com.emo.fish.ai.alibaba.ai.reactagent;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;


public class RealAgentExample {
    public static void main(String[] args) throws GraphRunnerException {


        // 初始化 ChatModel
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(System.getenv("AI_DASHSCOPE_API_KEY"))
                .build();

        ChatModel chatModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .build();

        // 定义天气查询工具的输入参数类
        record WeatherRequest(String city) {}

        // 定义天气查询工具
        java.util.function.Function<WeatherRequest, String> weatherFunction = (WeatherRequest request) -> {
            return "It's always sunny in " + request.city() + "!";
        };

        ToolCallback weatherTool = FunctionToolCallback.builder("get_weather", weatherFunction)
                .description("Get weather for a given city")
                .inputType(WeatherRequest.class)
                .build();

        // 创建 agent
        ReactAgent agent = ReactAgent.builder()
                .name("weather_agent")
                .model(chatModel)
                .tools(weatherTool)
                .systemPrompt("You are a helpful assistant")
                .saver(new MemorySaver())
                .build();

        // 运行 agent
        AssistantMessage response = agent.call("杭州天气如何?");
        System.out.println(response.getText());
    }
}
