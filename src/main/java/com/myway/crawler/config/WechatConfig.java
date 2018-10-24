package com.myway.crawler.config;

/**
 * 
 * 公众号配置
 * 
 * @author zhangy
 * @version 2018年9月18日
 */
public class WechatConfig {
    private Integer id;

    private String name;

    private String uid;

    private Integer status;

    /**
     * 名称占位符
     */
    public final static String QUERY_NAME_SLOT = "#name#";
    /**
     * 起始url
     */
    public final static String WECHAT_START_URL =
            "http://weixin.sogou.com/weixin?type=1&s_from=input&query=#name#&ie=utf8&_sug_=n&_sug_type_=";

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return this.uid;
    }

    public String getUrl() {
        return WECHAT_START_URL.replace(QUERY_NAME_SLOT, getUid());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
