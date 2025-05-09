package com.jungle.mcp.server.service;

import com.jungle.mcp.server.entity.Song;
import com.jungle.mcp.server.mapper.SongMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SongService {

    @Resource
    private SongMapper songMapper;

    /**
     *
     * @param id 参数类型 为String要确定好，不然LLM会因为类型不匹配无法调用
     * @return
     */
    @Tool(description = "根据ID查询歌曲信息")
    public Song selectById(@ToolParam(description = "ID") String id) {
        Song song = songMapper.selectById(id);
        log.info(">>>>>> 根据ID查询歌曲信息 id:{}, 结果：{}",id, song);
        return song;
    }
}
