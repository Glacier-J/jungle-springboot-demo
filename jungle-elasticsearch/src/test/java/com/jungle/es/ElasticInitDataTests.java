package com.jungle.es;

import com.jungle.es.entity.Song;
import com.jungle.es.mapper.SongMapper;
import com.jungle.es.repository.SongRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ElasticInitDataTests {

    @Autowired
    private SongMapper songMapper;

    @Autowired
    private SongRepository repository;

    @Test
    void initData() {
        List<Song> list = songMapper.selectAll();

        repository.saveAll(list);
    }

    @Test
    public void testES(){
        System.out.println(repository.findById("20210522155118"));
    }

}