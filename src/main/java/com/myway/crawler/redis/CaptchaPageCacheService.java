package com.myway.crawler.redis;

import org.springframework.stereotype.Service;
import com.myway.usf.redis.common.KeyGenerator;
import com.myway.usf.redis.service.BaseValuesCacheService;

/**
 * 
 * 存储验证码页
 * 
 * @author zhangy
 * @version 2018年10月17日
 */
@Service
public class CaptchaPageCacheService extends BaseValuesCacheService<String, String> {

    public static final String CAPTCHA_URL = "captchaUrl";

    public static final String CAPTCHA_SCREEN_SHOT = "captchaScreenShot";

    public static final String RESULT_SCREEN_SHOT = "resultScreenShot";

    @Override
    public String setCacheKey() {
        return KeyGenerator.generateKey("wechat", "crawler", "captcha");
    }

    @Override
    public String toCache(String fromObject) {
        return fromObject;
    }

    @Override
    public String toObject(String cache) {
        return cache;
    }

}
