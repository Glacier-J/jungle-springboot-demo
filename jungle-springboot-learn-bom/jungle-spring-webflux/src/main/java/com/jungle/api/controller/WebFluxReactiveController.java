package com.jungle.api.controller;

import com.jungle.api.entity.Song;
import com.jungle.api.service.IWebFluxReactiveDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/flux/reactive")
public class WebFluxReactiveController {

    private final IWebFluxReactiveDemoService service;

    @GetMapping("/{id}")
    public Mono<Song> queryById(@PathVariable String id) {
        return Mono.just(service.queryById(id));
    }

    @PostMapping("/save")
    public Mono<Void> queryById(@RequestBody Song song) {
        service.save(song);
        return Mono.empty();
    }
}
