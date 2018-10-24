package com.myway.crawler.downloader;

import com.myway.crawler.config.WebmagicConfig;
import us.codecraft.webmagic.downloader.AbstractDownloader;

/**
 * 
 * @author zhangy
 *
 */
public abstract class AbstractCustomDownloader extends AbstractDownloader {


    public WebmagicConfig getConfig() {
        return config;
    }

    public void setConfig(WebmagicConfig config) {
        this.config = config;
    }

    private WebmagicConfig config;

}
