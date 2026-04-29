package com.emo.fish.ai.alibaba.ai.tools;

import org.springframework.ai.tool.annotation.ToolParam;

import java.util.function.Function;

/**
 * 获取城市天气工具
 * 返回指定城市的天气信息（数据写死）
 */
public class GetCityWeatherTool implements Function<GetCityWeatherTool.WeatherRequest, String> {
    
    /**
     * 天气请求参数
     */
    public record WeatherRequest(
            @ToolParam(description = "城市名称，例如：北京、上海、杭州") String city
    ) {}
    
    @Override
    public String apply(WeatherRequest request) {
        // 模拟不同城市的天气数据
        String city = request.city();
        
        return switch (city) {
            case "北京", "Beijing" -> 
                "北京今天天气晴朗，温度22°C，空气质量良好，适合户外活动。";
            case "上海", "Shanghai" -> 
                "上海今天多云转小雨，温度18°C，建议携带雨伞。";
            case "杭州", "Hangzhou" -> 
                "杭州今天阳光明媚，温度25°C，西湖风景正好，非常适合出游。";
            case "深圳", "Shenzhen" -> 
                "深圳今天阴天，温度28°C，湿度较大，闷热天气。";
            case "广州", "Guangzhou" -> 
                "广州今天雷阵雨，温度26°C，注意防雷电。";
            default -> 
                city + "暂无此地天气预报";
        };
    }
}
