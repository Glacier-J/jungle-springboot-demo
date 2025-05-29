package com.jungle.api.domain.req;

import lombok.Data;

@Data
public class PageQueryVideosRequest {
    /**
     * 页码，默认从 0 开始
     */
    private Integer page = 0;

    /**
     * 每页视频数量，默认 20
     */
    private Integer pageSize = 10;

    /**
     * 搜索关键字，默认空字符串
     */
    private String query = "";

    /**
     * 排序方式，可选值：date、popular，默认 popular
     */
    private String sort = "popular";

    /**
     * 是否在响应中添加 urls 属性，默认 false
     */
    private Boolean urls = false;

}
