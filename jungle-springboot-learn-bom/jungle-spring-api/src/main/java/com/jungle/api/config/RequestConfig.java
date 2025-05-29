package com.jungle.api.config;

import com.jungle.api.constant.ApiConstant;
import com.jungle.api.domain.req.BaseRequest;
import com.jungle.api.utils.OkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class RequestConfig {

    private final Map<String, BaseRequest> requestServiceMap = new HashMap<>();

    private final Map<String, OkHttpUtil> okHttpUtilMap = new HashMap<>();

    @PostConstruct
    public void init() {
        log.info(">>> 初始化Http请求相关信息.");
        //初始化服务商信息
        initRequestServiceMessage();

        //初始化OkHttpUtil
        initializeOkHttpUtil(getRequestService(ApiConstant.COVERR_NAME));
    }

    private void initRequestServiceMessage() {
        BaseRequest coverrService = BaseRequest.builder()
                .serviceName(ApiConstant.COVERR_NAME)
                .apiHost(ApiConstant.COVERR_BASE_URL)
                .apiKey(ApiConstant.COVERR_API_KEY)
                .build();
        requestServiceMap.put(ApiConstant.COVERR_NAME, coverrService);
    }

    private BaseRequest getRequestService(String serviceName) {
        return requestServiceMap.get(serviceName);
    }


    private void initializeOkHttpUtil(BaseRequest baseRequest) {
        OkHttpUtil okHttpUtil = new OkHttpUtil();
        okHttpUtil.setApiHost(baseRequest.getApiHost());
        okHttpUtil.setApiKey(baseRequest.getApiKey());
        okHttpUtilMap.put(baseRequest.getServiceName(), okHttpUtil);
    }

    public OkHttpUtil getOkHttpUtil(String serviceName) {
        return okHttpUtilMap.get(serviceName);
    }

}
