package com.myway.crawler.redis;

import org.springframework.stereotype.Service;
import com.myway.usf.redis.common.KeyGenerator;
import com.myway.usf.redis.service.BaseHashCacheService;

/**
 * 
 * 结果缓存
 * 
 * @author zhangy
 * @version 2018年10月11日
 */
@Service
public class WechatStatusCacheService
        extends BaseHashCacheService<WechatStatusCache, WechatStatusCache> {

    @Override
    public String setCacheKey() {
        return KeyGenerator.generateKey("wechat", "crawler", "result");
    }

    @Override
    public WechatStatusCache toCache(WechatStatusCache fromObject) {
        return fromObject;
    }

    @Override
    public WechatStatusCache toObject(WechatStatusCache cache) {
        return cache;
    }
}
