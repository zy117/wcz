package com.myway.crawler.config;

import java.util.List;

/**
 * 
 * 爬虫配置
 * 
 * @author zhangy
 * @version 2018年9月18日
 */
public class SpiderConfig {
    /**
     * 站点ID
     */
    private String siteId;

    /**
     * 线程数
     */
    private Integer thread;

    /**
     * 提取工具
     */
    private String processer;

    /**
     * 处理列表
     */
    private List<String> pipeline;

    /**
     * 下载工具
     */
    private String downloader;

    /**
     * 开始url
     */
    private String startUrl;

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public Integer getThread() {
        return thread;
    }

    public void setThread(Integer thread) {
        this.thread = thread;
    }

    public String getProcesser() {
        return processer;
    }

    public void setProcesser(String processer) {
        this.processer = processer;
    }

    public List<String> getPipeline() {
        return pipeline;
    }

    public void setPipeline(List<String> pipeline) {
        this.pipeline = pipeline;
    }

    public String getDownloader() {
        return downloader;
    }

    public void setDownloader(String downloader) {
        this.downloader = downloader;
    }

    public String getStartUrl() {
        return startUrl;
    }

    public void setStartUrl(String startUrl) {
        this.startUrl = startUrl;
    }
}
