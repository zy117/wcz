package com.myway.spider.downloader;

import com.myway.spider.config.WebmagicConfig;
import us.codecraft.webmagic.downloader.AbstractDownloader;

/**
 * 
 * @author zhangy
 *
 */
public abstract class CustomAbstractDownloader extends AbstractDownloader {


    public WebmagicConfig getConfig() {
        return config;
    }

    public void setConfig(WebmagicConfig config) {
        this.config = config;
    }

    private WebmagicConfig config;

}
