package com.jungle.webflux.handler;

import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;

public class SongHandler {

    // 根据id查询
    public Mono<ServerResponse> getById(ServerRequest request){
        // 获取id值
        String id = request.pathVariable("id");
        // 空值处理
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        // 调用Service方法得到数据
        Mono<User> userMono = userService.getById(Integer.parseInt(id));
        // 把userMono进行转换返回
        return userMono.flatMap(user ->
                ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(userMono))
                        .switchIfEmpty(notFound)
        );
    }

    // 查询多个
    public Mono<ServerResponse> getAll(ServerRequest request){
        // 调用Service得到结果
        Flux<User> users = userService.getAll();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(users, User.class);

    }

    // 保存
    public Mono<ServerResponse> save(ServerRequest request){
        // 获取User对象
        Mono<User> userMono = request.bodyToMono(User.class);
        return ServerResponse.ok().build(userService.save(userMono));
    }


    public static void main(String[] args) {
        // 创建对象
        UserService userService = new UserService();
        UserHandler userHandler = new UserHandler(userService);
        // 创建路由
        RouterFunction<ServerResponse> route = RouterFunctions
                .route(RequestPredicates.GET("/user/{id}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        userHandler::getById)
                .andRoute(RequestPredicates.GET("/users").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        userHandler::getAll);
        // 路由和handler适配
        HttpHandler httpHandler = RouterFunctions.toHttpHandler(route);
        ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
        // 创建服务器
//        HttpServer httpServer = HttpServer.create();
//        httpServer.handle(adapter).bindNow();
        HttpServer httpServer=HttpServer.create().port(8080);
        httpServer.handle(adapter).bindNow().onDispose().block();
    }
}
