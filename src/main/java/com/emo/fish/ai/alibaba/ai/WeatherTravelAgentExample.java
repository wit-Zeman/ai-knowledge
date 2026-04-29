package com.emo.fish.ai.alibaba.ai;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.emo.fish.ai.alibaba.ai.tools.GetCityWeatherTool;
import com.emo.fish.ai.alibaba.ai.tools.PlanActivityTool;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

/**
 * 天气出行助手 Agent 示例
 * 
 * 功能：
 * 1. 查询城市天气
 * 2. 根据天气推荐出行活动
 */
public class WeatherTravelAgentExample {

    // 系统提示词
    static String SYSTEM_PROMPT = """
            你是一个专业的天气出行助手，可以帮助用户查询天气并推荐合适的出行活动。
            
            你有两个工具可以使用：
            1. getCityWeather - 查询指定城市的天气情况
            2. planActivity - 根据天气情况推荐合适的出行活动
            
            工作流程：
            1. 当用户询问某个城市的天气时，先使用 getCityWeather 工具查询天气
            2. 然后根据查询到的天气情况，使用 planActivity 工具推荐合适的活动
            3. 最后给用户一个综合性的建议，包括天气信息和活动推荐
            
            注意事项：
            - 如果用户只说了城市名，先查询天气，再推荐活动
            - 如果用户直接问"今天适合做什么"，需要先确认城市，再查询天气
            - 回答要友好、详细，给用户实用的建议
            - 如果无对应城市天气，直接回答查询不到，也不推荐活动
            """;

    public static void main(String[] args) throws GraphRunnerException {

        // 1. 初始化 DashScope API
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(System.getenv("AI_DASHSCOPE_API_KEY"))
                .build();

        // 2. 创建 ChatModel
        ChatModel chatModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .build();

        // 3. 创建工具回调
        ToolCallback getCityWeatherTool = FunctionToolCallback
                .builder("getCityWeather", new GetCityWeatherTool())
                .description("查询指定城市的天气情况。需要提供城市名称作为参数。")
                .inputType(GetCityWeatherTool.WeatherRequest.class)
                .build();

        ToolCallback planActivityTool = FunctionToolCallback
                .builder("planActivity", new PlanActivityTool())
                .description("根据天气情况推荐合适的出行活动。需要提供天气描述和城市名称作为参数。")
                .inputType(PlanActivityTool.ActivityRequest.class)
                .build();

        // 4. 创建 ReactAgent
        ReactAgent agent = ReactAgent.builder()
                .name("weather_travel_agent")
                .model(chatModel)
                .instruction(SYSTEM_PROMPT)
                .tools(getCityWeatherTool, planActivityTool)
                .saver(new MemorySaver())  // 启用记忆功能，支持多轮对话
                .build();

//        // 5. 测试 Agent - 第一轮对话
//        System.out.println("========== 测试 1: 查询天气并推荐活动 ==========");
//        AssistantMessage response = agent.call("杭州今天天气怎么样？有什么好玩的吗？");
//        System.out.println("用户: 杭州今天天气怎么样？有什么好玩的吗？");
//        System.out.println("助手: " + response.getText());
//        System.out.println();
//
//        // 6. 测试 Agent - 第二轮对话（使用记忆功能）
//        System.out.println("========== 测试 2: 继续对话 ==========");
//        response = agent.call("那如果下雨呢？");
//        System.out.println("用户: 那如果下雨呢？");
//        System.out.println("助手: " + response.getText());
//        System.out.println();
//
//        // 7. 测试其他城市
//        System.out.println("========== 测试 3: 查询其他城市 ==========");
//        response = agent.call("北京呢？");
//        System.out.println("用户: 北京呢？");
//        System.out.println("助手: " + response.getText());
//        System.out.println();

        // 8. 单独创建一个新的 Agent 测试广州
        System.out.println("========== 测试 4: 新对话 - 广州 ==========");
        ReactAgent newAgent = ReactAgent.builder()
                .name("weather_travel_agent_2")
                .model(chatModel)
                .systemPrompt(SYSTEM_PROMPT)
                .tools(getCityWeatherTool, planActivityTool)
                .saver(new MemorySaver())
                .build();

        AssistantMessage response = newAgent.call("广州适合出游吗？");
        System.out.println("用户: 广州适合出游吗？");
        System.out.println("助手: " + response.getText());
        System.out.println();

        // 美国旧金山天气测试
        response = newAgent.call("美国旧金山呢？");
        System.out.println("用户: 美国旧金山呢？");
        System.out.println("助手: " + response.getText());
        System.out.println();
    }
}
