package com.emo.fish.ai.alibaba.ai.tools;

import org.springframework.ai.tool.annotation.ToolParam;

import java.util.function.Function;

// 天气查询工具
public class WeatherForLocationTool implements Function<WeatherForLocationTool.WeatherRequest, String> {
    
    // 定义请求参数类
    public record WeatherRequest(
            @ToolParam(description = "The city name") String city
    ) {}
    
    @Override
    public String apply(WeatherRequest request) {
        return "It's always sunny in " + request.city() + "!";
    }
}
