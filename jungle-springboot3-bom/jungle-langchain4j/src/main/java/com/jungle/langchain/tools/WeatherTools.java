package com.jungle.langchain.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

public class WeatherTools {


    @Tool("返回提供城市的天气预报")
    public String getWeather(
            @P("应返回天气预报的城市") String city,
            TemperatureUnit temperatureUnit
    ) {
        System.out.println(">>>>>>>>>>>>>> Weather forecast for city: " + city + ", temperatureUnit: " + temperatureUnit);
        return city + "很凉快，20" + temperatureUnit.name();
    }

    public enum TemperatureUnit {
        CELSIUS, FAHRENHEIT
    }
}
