package com.jungle.mcp.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jungle.mcp.server.entity.Song;

import java.util.List;

public interface SongMapper extends BaseMapper<Song> {

    /**
     * 查询全部歌曲
     * @return List<Song>
     */
    List<Song> selectAll();
}