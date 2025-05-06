package com.jungle.es.mapper;

import com.jungle.es.entity.Song;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SongMapper {

    /**
     * 查询全部歌曲
     * @return List<Song>
     */
    List<Song> selectAll();
}