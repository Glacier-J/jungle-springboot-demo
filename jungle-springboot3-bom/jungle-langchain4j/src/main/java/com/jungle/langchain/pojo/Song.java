package com.jungle.langchain.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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