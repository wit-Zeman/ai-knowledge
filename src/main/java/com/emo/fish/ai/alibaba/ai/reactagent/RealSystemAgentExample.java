package com.emo.fish.ai.alibaba.ai.reactagent;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.emo.fish.ai.alibaba.ai.tools.UserLocationTool;
import com.emo.fish.ai.alibaba.ai.tools.WeatherForLocationTool;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

public class RealSystemAgentExample {

    static String SYSTEM_PROMPT = """
            You are an expert weather forecaster, who speaks in puns.
            
            You have access to two tools:
            
            - get_weather_for_location: use this to get the weather for a specific location
            - get_user_location: use this to get the user's location
            
            If a user asks you for the weather, make sure you know the location.
            If you can tell from the question that they mean wherever they are,
            use the get_user_location tool to find their location.
            """;

    public static void main(String[] args) throws GraphRunnerException {

        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(System.getenv("AI_DASHSCOPE_API_KEY"))
                .build();

        ChatModel chatModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(DashScopeChatOptions.builder()
                        .model(DashScopeChatModel.DEFAULT_MODEL_NAME)
                        .temperature(0.5)
                        .maxToken(1000)
                        .build())
                .build();

        // 创建工具回调
        ToolCallback getWeatherTool = FunctionToolCallback
                .builder("getWeatherForLocation", new WeatherForLocationTool())
                .description("Get weather for a given city")
                .inputType(WeatherForLocationTool.WeatherRequest.class)
                .build();

        ToolCallback getUserLocationTool = FunctionToolCallback
                .builder("getUserLocation", new UserLocationTool())
                .description("Retrieve user location based on user ID")
                .inputType(UserLocationTool.UserLocationRequest.class)
                .build();

        // 创建 agent
        ReactAgent agent = ReactAgent.builder()
                .name("weather_pun_agent")
                .model(chatModel)
                .systemPrompt(SYSTEM_PROMPT)
                .tools(getUserLocationTool, getWeatherTool)
                .outputType(ResponseFormat.class)
                .saver(new MemorySaver())
                .build();

// threadId 是给定对话的唯一标识符
        RunnableConfig runnableConfig = RunnableConfig.builder().threadId("1").addMetadata("user_id", "1").build();

// 第一次调用
        AssistantMessage response = agent.call("what is the weather outside?", runnableConfig);
        System.out.println(response.getText());
// 输出类似：
// Florida is still having a 'sun-derful' day! The sunshine is playing
// 'ray-dio' hits all day long! I'd say it's the perfect weather for
// some 'solar-bration'!

// 注意我们可以使用相同的 threadId 继续对话
        response = agent.call("thank you!", runnableConfig);
        System.out.println(response.getText());
// 输出类似：
// You're 'thund-erfully' welcome! It's always a 'breeze' to help you
// stay 'current' with the weather.
    }
}
