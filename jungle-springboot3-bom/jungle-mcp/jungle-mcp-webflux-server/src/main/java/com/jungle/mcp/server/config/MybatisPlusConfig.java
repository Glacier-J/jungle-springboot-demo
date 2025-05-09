package com.jungle.mcp.server.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * mybatis-plus配置类(下方注释有插件介绍)
 *
 * @author Lion Li
 */
@EnableTransactionManagement(proxyTargetClass = true)
@Configuration
@MapperScan("com.jungle.mcp.server.mapper")
public class MybatisPlusConfig {

//    @Bean
//    public MybatisPlusInterceptor mybatisPlusInterceptor() {
//        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        // 分页插件
//        interceptor.addInnerInterceptor(paginationInnerInterceptor());
//        return interceptor;
//    }
//
//    /**
//     * 分页插件，自动识别数据库类型
//     */
//    public PaginationInnerInterceptor paginationInnerInterceptor() {
//        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
//        // 设置最大单页限制数量，默认 500 条，-1 不受限制
//        paginationInnerInterceptor.setMaxLimit(-1L);
//        // 分页合理化
//        paginationInnerInterceptor.setOverflow(true);
//        return paginationInnerInterceptor;
//    }

}
