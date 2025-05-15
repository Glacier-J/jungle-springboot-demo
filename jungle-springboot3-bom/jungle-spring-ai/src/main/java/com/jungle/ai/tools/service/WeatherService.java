package com.jungle.ai.tools.service;


import com.jungle.ai.dto.WeatherRequest;
import com.jungle.ai.dto.WeatherResponse;
import com.jungle.ai.enums.Unit;

import java.util.function.Function;

public class WeatherService implements Function<WeatherRequest, WeatherResponse> {

    public WeatherResponse apply(WeatherRequest request) {
        System.out.println("获取当前位置的天气");
        return new WeatherResponse(33.0, Unit.C);
    }




}

