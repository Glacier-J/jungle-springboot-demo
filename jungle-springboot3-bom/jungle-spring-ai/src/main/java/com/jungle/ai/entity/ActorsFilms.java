package com.jungle.ai.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;
import java.util.List;

@JsonPropertyOrder({"actor", "movies"}) //自定义属性排序
public record ActorsFilms(String actor, List<MovieMessage> movies) {

    public record MovieMessage(String movieName, LocalDate releaseTime){}
}