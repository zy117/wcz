package com.myway.crawler.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 解析规则
 * 
 * @author zhangy
 * @version 2018年9月18日
 */
public class RuleMatchConfig {
    /**
     * 微信规则
     */
    public static final RuleMatchConfig WECHAT_RULE_CONFIG = new RuleMatchConfig();
    static {
        WECHAT_RULE_CONFIG.setDetailRegex("http://mp\\.weixin\\.qq\\.com/s?\\S+");
        WECHAT_RULE_CONFIG.setListRegex("http://mp.weixin.qq.com/profile?src");
        WECHAT_RULE_CONFIG.setLoadListXpath("//*[@id='main']/div[4]/ul/li[1]/div/div[2]/p[1]");
        WECHAT_RULE_CONFIG.setListXpath("");
        List<DetailXpathConfig> detailxpath = new ArrayList<DetailXpathConfig>();
        detailxpath.add(new DetailXpathConfig("beforeContent", "", "//*[@id='js_content']"));
        WECHAT_RULE_CONFIG.setDetailXpath(detailxpath);
    }
    /**
     * 列表url的regex
     */
    private String listRegex;

    /**
     * 列表xpath
     */
    private String listXpath;

    /**
     * 详情url的regex
     */
    private String detailRegex;

    /**
     * 提取列表xpath
     */
    private String loadListXpath;

    private String itemXpath;

    private String itemImageXpath;

    /**
     * 详情xpath列表
     */
    private List<DetailXpathConfig> detailXpath;

    public String getListRegex() {
        return listRegex;
    }

    public void setListRegex(String listRegex) {
        this.listRegex = listRegex;
    }

    public String getListXpath() {
        return listXpath;
    }

    public void setListXpath(String listXpath) {
        this.listXpath = listXpath;
    }

    public String getDetailRegex() {
        return detailRegex;
    }

    public void setDetailRegex(String detailRegex) {
        this.detailRegex = detailRegex;
    }

    public String getLoadListXpath() {
        return loadListXpath;
    }

    public void setLoadListXpath(String loadListXpath) {
        this.loadListXpath = loadListXpath;
    }

    public String getItemXpath() {
        return itemXpath;
    }

    public void setItemXpath(String itemXpath) {
        this.itemXpath = itemXpath;
    }

    public String getItemImageXpath() {
        return itemImageXpath;
    }

    public void setItemImageXpath(String itemImageXpath) {
        this.itemImageXpath = itemImageXpath;
    }

    public List<DetailXpathConfig> getDetailXpath() {
        return detailXpath;
    }

    public void setDetailXpath(List<DetailXpathConfig> detailXpath) {
        this.detailXpath = detailXpath;
    }


}
