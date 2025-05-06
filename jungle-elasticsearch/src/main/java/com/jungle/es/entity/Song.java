package com.jungle.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "songs")
public class Song {

    @Id
    @Field(type= FieldType.Keyword)
    private String id;

    /**
     * 歌曲名
     */
    @Field(type= FieldType.Text, analyzer = "ik_max_word")
    private String name;

    /**
     * 描述信息
     */
    @Field(type= FieldType.Text, analyzer = "ik_max_word")
    private String note;

    /**
     * 歌手
     */
    @Field(type= FieldType.Text, analyzer = "ik_max_word")
    private String singer;
}