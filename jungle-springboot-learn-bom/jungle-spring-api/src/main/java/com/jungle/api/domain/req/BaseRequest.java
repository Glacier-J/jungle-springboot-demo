package com.jungle.api.domain.req;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseRequest {

    /**
     * 服务商名称
     */
    private String serviceName;

    /**
     * 请求地址
     */
    private String apiHost;

    /**
     * 密钥
     */
    private String apiKey;

    /**
     * 备注
     */
    private String remark;
}
