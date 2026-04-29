package com.emo.fish.ai.alibaba.ai.tools;

import org.springframework.ai.tool.annotation.ToolParam;

import java.util.function.Function;

/**
 * 根据天气安排出行活动工具
 * 根据天气情况推荐合适的活动（数据写死）
 */
public class PlanActivityTool implements Function<PlanActivityTool.ActivityRequest, String> {
    
    /**
     * 活动请求参数
     */
    public record ActivityRequest(
            @ToolParam(description = "天气情况描述，例如：晴朗、下雨、阴天、雷阵雨") String weather,
            @ToolParam(description = "城市名称") String city
    ) {}
    
    @Override
    public String apply(ActivityRequest request) {
        String weather = request.weather();
        String city = request.city();
        
        // 根据天气类型推荐活动
        if (weather.contains("晴朗") || weather.contains("阳光明媚") || weather.contains("晴天")) {
            return """
                    🌞 晴朗天气活动推荐：
                    
                    1. 户外登山 - %s周边山区是很好的选择
                    2. 骑行游览 - 城市绿道骑行，感受美好春光
                    3. 野餐聚会 - 公园草坪野餐，享受阳光
                    4. 摄影采风 - 光线充足，适合拍摄风景照
                    5. 放风筝 - 如果风力适中，非常适合放风筝
                    
                    温馨提示：记得涂抹防晒霜，带上太阳镜和帽子！
                    """.formatted(city);
                    
        } else if (weather.contains("小雨") || weather.contains("多云")) {
            return """
                    🌤️ 多云/小雨天气活动推荐：
                    
                    1. 博物馆参观 - %s博物馆是不错的选择
                    2. 室内咖啡厅 - 找一家文艺咖啡厅，享受悠闲时光
                    3. 购物中心 - 逛商场看电影，室内活动不受影响
                    4. 美术馆/展览馆 - 欣赏艺术，陶冶情操
                    5. 室内游泳 - 恒温游泳池，不受天气影响
                    
                    温馨提示：出门记得带伞，路面湿滑注意安全！
                    """.formatted(city);
                    
        } else if (weather.contains("雷阵雨") || weather.contains("大雨") || weather.contains("暴雨")) {
            return """
                    ⛈️ 雷雨天气活动建议：
                    
                    1. 居家休息 - 最安全的选择，在家看看书、追追剧
                    2. 室内健身 - 健身房锻炼，保持运动习惯
                    3. 烘焙烹饪 - 在家尝试做新菜品
                    4. 桌游/棋牌 - 和家人朋友一起玩桌游
                    5. 在线学习 - 利用时间学习新技能
                    
                    ⚠️ 安全提示：
                    - 尽量避免外出
                    - 不要在树下、电线杆下避雨
                    - 关闭不必要的电器设备
                    - 注意防雷防滑
                    """.formatted(city);
                    
        } else if (weather.contains("阴")) {
            return """
                    ☁️ 阴天活动推荐：
                    
                    1. 城市漫步 - 温度适宜，适合citywalk
                    2. 公园散步 - %s的公园风景不错
                    3. 户外拍照 - 阴天光线柔和，适合人像摄影
                    4. 骑行观光 - 不会太晒，骑行很舒服
                    5. 露天茶座 - 找个户外茶座喝杯茶
                    
                    温馨提示：阴天紫外线依然存在，注意防晒！
                    """.formatted(city);
                    
        } else {
            return """
                    🌈 通用活动建议：
                    
                    当前天气：%s
                    
                    1. 根据具体天气情况选择合适的户外活动
                    2. 注意查看实时天气预报
                    3. 做好相应的防护措施
                    4. 安全第一，享受美好时光！
                    """.formatted(weather);
        }
    }
}
