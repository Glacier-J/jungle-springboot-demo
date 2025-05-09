package com.jungle.webflux.service;

import com.jungle.webflux.entity.Song;

public interface IWebFluxDemoService {

    /**
     * 保存数据
     * @param  song 歌曲
     * @return ai 生成内容
     */
    void save(Song song);

    Song queryById(String id);
}
