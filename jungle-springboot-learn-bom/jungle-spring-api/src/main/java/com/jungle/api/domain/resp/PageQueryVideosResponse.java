package com.jungle.api.domain.resp;

import lombok.Data;

import java.util.Map;

@Data
public class PageQueryVideosResponse {

    /**
     * 搜索关键字，默认空字符串
     */
    private Map<String,Object> body;

}
