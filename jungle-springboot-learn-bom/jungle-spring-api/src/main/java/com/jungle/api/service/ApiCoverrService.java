package com.jungle.api.service;

import com.jungle.api.domain.req.PageQueryVideosRequest;
import com.jungle.api.domain.resp.PageQueryVideosResponse;
import com.jungle.api.entity.Song;

public interface ApiCoverrService {

    /**
     * 保存数据
     * @param  song 歌曲
     * @return ai 生成内容
     */
    void save(Song song);

    PageQueryVideosResponse videos(PageQueryVideosRequest pageQueryVideosRequest);
}
