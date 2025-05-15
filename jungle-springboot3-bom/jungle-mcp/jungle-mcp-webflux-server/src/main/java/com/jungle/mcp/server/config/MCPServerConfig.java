package com.jungle.mcp.server.config;

import com.jungle.mcp.server.service.OpenMeteoService;
import com.jungle.mcp.server.service.SongService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MCPServerConfig {

    /**
     * 配置大模型可以调用的方法
     * @param openMeteoService 天气相关功能
     * @param songService 歌曲相关功能
     * @return
     */
    @Bean
    public ToolCallbackProvider weatherTools(OpenMeteoService openMeteoService,
                                             SongService songService/*,
                                             WeatherService weatherService*/) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(openMeteoService, songService/*, weatherService*/)
                .build();
    }
}
