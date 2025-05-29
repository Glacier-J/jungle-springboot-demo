package com.jungle.api.controller;

import com.jungle.api.domain.req.PageQueryVideosRequest;
import com.jungle.api.domain.resp.PageQueryVideosResponse;
import com.jungle.api.service.ApiCoverrService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * coverr 视频素材 api
 * <a href="https://api.coverr.co/docs/videos/#list-videos">...</a>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/coverr")
public class ApiCoverrController {

    private final ApiCoverrService service;

    /**
     * list a general list of top videos
     * 分页查询视频素材
     * @param pageQueryVideosRequest 入参
     * @return
     */
    @GetMapping("/videos")
    public PageQueryVideosResponse videos(PageQueryVideosRequest pageQueryVideosRequest) {
        return service.videos(pageQueryVideosRequest);
    }

}
