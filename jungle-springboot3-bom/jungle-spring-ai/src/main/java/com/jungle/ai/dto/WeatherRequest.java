package com.jungle.ai.dto;


import com.jungle.ai.enums.Unit;
import org.springframework.ai.tool.annotation.ToolParam;

public record WeatherRequest(@ToolParam(description = "一个地点名称") String location, Unit unit) {
}