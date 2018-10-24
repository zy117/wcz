package com.myway.crawler.config;

/**
 * 
 * 站点配置
 * 
 * @author zhangy
 * @version 2018年9月18日
 */
public class SiteConfig {

    /**
     * 微信配置
     */
    public static final SiteConfig WECHAT_SITE_CONFIG = new SiteConfig();
    static {
        WECHAT_SITE_CONFIG.setDomain("mp.weixin.qq.com");
        WECHAT_SITE_CONFIG.setRetry(3);
        WECHAT_SITE_CONFIG.setSleepTime(5000);
        WECHAT_SITE_CONFIG.setUserAgent(
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3)AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36");
    }

    private String userAgent;

    private String domain;

    private int retry;

    private String charset;

    private int sleepTime;

    public Boolean getDynamicProxy() {
        return dynamicProxy;
    }

    public void setDynamicProxy(Boolean dynamicProxy) {
        this.dynamicProxy = dynamicProxy;
    }

    private Boolean dynamicProxy;;

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public int getRetry() {
        return this.retry;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public int getSleepTime() {
        return this.sleepTime;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
