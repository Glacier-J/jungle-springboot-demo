package com.jungle.webflux.entity;

import lombok.Data;

@Data
public class Song {

    private String id;

    /**
     * 歌曲名
     */
    private String name;

    /**
     * 描述信息
     */
    private String note;

    /**
     * 歌手
     */
    private String singer;
}