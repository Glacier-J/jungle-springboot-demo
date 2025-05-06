package com.jungle.es.repository;

import com.jungle.es.entity.Song;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends ElasticsearchRepository<Song, String> {

}