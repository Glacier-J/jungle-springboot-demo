package com.jungle.mcp.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("song")
public class Song {

    @TableId(type = IdType.ASSIGN_UUID)
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