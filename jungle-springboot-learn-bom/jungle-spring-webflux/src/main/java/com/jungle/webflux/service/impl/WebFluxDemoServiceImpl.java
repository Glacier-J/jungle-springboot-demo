package com.jungle.webflux.service.impl;

import com.jungle.webflux.entity.Song;
import com.jungle.webflux.service.IWebFluxDemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebFluxDemoServiceImpl implements IWebFluxDemoService {

    //模拟 DB
    private final Map<String, Song> songs = new HashMap<>();

    @Override
    public void save(Song song) {
        songs.put(song.getId(), song);
    }

    @Override
    public Song queryById(String id) {
        return songs.get(id);
    }
}
