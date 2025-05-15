package com.jungle.ai.tools;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 提供给大模型调用的工具函数-description-强烈建议提供详细的描述
 */
public class DateTimeTools {

    /**
     * 让模型在需要时调用此工具。
     *
     * @return
     */
    @Tool(description = "获取Jungle的爱好")
    public String getHobby() {
        System.out.println("获取Jungle的爱好");
        return "骑行、远行、游戏、音乐";
    }

    @Tool(description = "获取当前时间")
    public String getCurrentDateTime() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("获取当前时间" + now);
        return now;
    }

    @Tool(description = "将提供的时间设置成闹钟")
    //除了 @ToolParam 注释之外，还可以使用 Swagger 的 @Schema 注释或 Jackson 的 @JsonProperty。
    public void setAlarm(@ToolParam(description = "yyyy-MM-dd HH:mm:ss格式的时间") String time) {
        LocalDateTime alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("闹钟设置时间为： " + time);
    }

    @Tool(description = "将提供的时间设置成闹钟")
    //除了 @ToolParam 注释之外，还可以使用 Swagger 的 @Schema 注释或 Jackson 的 @JsonProperty。
    public void testContext(@ToolParam(description = "这是一个id") Long id, ToolContext toolContext) {
        Map<String, Object> context = toolContext.getContext();
        String tenantId = (String) context.get("tenantId");
        System.out.println("上下文信息： " + context);
    }

}
