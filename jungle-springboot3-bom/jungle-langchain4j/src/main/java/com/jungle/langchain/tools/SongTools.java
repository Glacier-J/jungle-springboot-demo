package com.jungle.langchain.tools;

import com.jungle.langchain.pojo.Song;
import dev.langchain4j.agent.tool.Tool;

import java.util.HashMap;
import java.util.Map;

//@Component
public class SongTools {

    private final static Map<String, Song> toolMap = new HashMap<>();

    public SongTools() {
        toolMap.put("20210522153941", Song.builder().id("20210522153941").name("宠坏").singer("李俊佑、潘柚彤").build());
        toolMap.put("20210522154331", Song.builder().id("20210522154331").name("爱一点").singer("王力宏、章子怡").build());
        toolMap.put("20210522154653", Song.builder().id("20210522154653").name("《画江湖之换世门生 原声带》").singer("排骨教主").build());
    }

    @Tool
    public Song selectById(String id) {
        return toolMap.getOrDefault(id, new Song());
    }

    @Tool
    public Song removeById(String id) {
        return toolMap.remove(id);
    }


}