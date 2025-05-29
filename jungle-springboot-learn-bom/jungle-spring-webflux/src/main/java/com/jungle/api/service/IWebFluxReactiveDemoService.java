package com.jungle.api.service;

import com.jungle.api.entity.Song;

public interface IWebFluxReactiveDemoService {

    /**
     * 保存数据
     * @param  song 歌曲
     * @return ai 生成内容
     */
    void save(Song song);

    Song queryById(String id);
}
