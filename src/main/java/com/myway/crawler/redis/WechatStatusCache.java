package com.myway.crawler.redis;

/**
 * 
 * 公众号结果缓存
 * 
 * @author zhangy
 * @version 2018年10月11日
 */
public class WechatStatusCache {

    private String uid;
    private String msg;
    private Boolean success;
    private Boolean crawling;
    private Long updateTime;

    private String url;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getCrawling() {
        return crawling;
    }

    public void setCrawling(Boolean crawling) {
        this.crawling = crawling;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
