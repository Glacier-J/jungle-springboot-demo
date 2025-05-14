package com.jungle.ai.service;

public interface VectorService {

    /**
     * 存储
     * @param sourceFile 文件路径
     */
    void load(String sourceFile);

    /**
     * 搜索
     */
    void search();
}
