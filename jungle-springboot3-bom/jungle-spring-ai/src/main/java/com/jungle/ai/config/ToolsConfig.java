package com.jungle.ai.config;

import com.jungle.ai.dto.WeatherRequest;
import com.jungle.ai.dto.WeatherResponse;
import com.jungle.ai.tools.service.WeatherService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

/**
 * proxyBeanMethods = false
 */
@Configuration(proxyBeanMethods = false)
public class ToolsConfig {

    /**
     * 动态规范-将工具函数交由 spring 统一管理
     * @return
     */
    @Bean
    @Description("获取当前位置的天气")
    public Function<WeatherRequest, WeatherResponse> currentWeather() {
        return new WeatherService();
    }

}
