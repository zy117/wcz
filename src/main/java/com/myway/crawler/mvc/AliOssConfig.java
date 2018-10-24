package com.myway.crawler.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import com.myway.usf.common.oss.OssConfig;

/**
 * 
 * 
 * @author zhangy
 * @version 2018年10月19日
 */
@Configuration
public class AliOssConfig {

    @Autowired
    private Environment env;

    @Bean
    public OssConfig getOssConfig() {
        OssConfig ossConfig = new OssConfig();
        ossConfig.setEndPoint(env.getProperty("oss.end.point"));
        ossConfig.setAccessKey(env.getProperty("oss.access.key"));
        ossConfig.setAccessKeySecret(env.getProperty("oss.access.key.secret"));
        ossConfig.setBucket(env.getProperty("oss.bucket"));
        ossConfig.setDomainUrl(env.getProperty("oss.domain.url"));
        ossConfig.setImgShowBaseUrl(env.getProperty("oss.img.show.base.url"));
        ossConfig.setRmsDir(env.getProperty("oss.rms.dir"));
        ossConfig.setRootDir(env.getProperty("oss.root.dir"));
        ossConfig.setUploadDir(env.getProperty("oss.upload.dir"));
        return ossConfig;
    }
}
