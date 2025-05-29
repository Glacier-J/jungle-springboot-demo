package com.jungle.api.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jungle.api.config.RequestConfig;
import com.jungle.api.constant.ApiConstant;
import com.jungle.api.domain.req.PageQueryVideosRequest;
import com.jungle.api.domain.resp.PageQueryVideosResponse;
import com.jungle.api.entity.Song;
import com.jungle.api.service.ApiCoverrService;
import com.jungle.api.utils.OkHttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiCoverrServiceImpl implements ApiCoverrService {

    //模拟 DB
    private final Map<String, Song> songs = new HashMap<>();

    private final RequestConfig requestConfig;

    @Override
    public void save(Song song) {
        songs.put(song.getId(), song);
    }

    @Override
    public PageQueryVideosResponse videos(PageQueryVideosRequest pageQueryVideosRequest) {
        OkHttpUtil okHttpUtil = requestConfig.getOkHttpUtil(ApiConstant.COVERR_NAME);
        String path = "/videos" + buildParameter(pageQueryVideosRequest);
        Request videosRequest = okHttpUtil.createGetRequest(path);
        String body = okHttpUtil.executeRequest(videosRequest);

        PageQueryVideosResponse pageQueryVideosResponse = new PageQueryVideosResponse();
        JSONObject jsonObjectBody = JSONUtil.parseObj(body);
        pageQueryVideosResponse.setBody(jsonObjectBody.toBean(new TypeReference<>() {
        }));
        return pageQueryVideosResponse;
    }

    @NotNull
    private static String buildParameter(PageQueryVideosRequest pageQueryVideosRequest) {
        return "?page=" + pageQueryVideosRequest.getPage() +
                "&page_size=" + pageQueryVideosRequest.getPageSize() +
                "&query=" + pageQueryVideosRequest.getQuery() +
                "&sort=" + pageQueryVideosRequest.getSort() +
                "&urls=" + pageQueryVideosRequest.getUrls();
    }
}
