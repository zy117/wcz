package com.myway.spider.config;

import java.util.*;

/**
 * Created by qiyamac on 2017/3/24.
 */
public class WebmagicConfig {
    private SiteConfig site;

    private SpiderConfig spider;

    private WechatConfig wechat;

    private RuleMatchConfig rule;
    
    private List<RinseRule> rinseRules;

    public void setSite(SiteConfig site) {
        this.site = site;
    }

    public SiteConfig getSite() {
        return this.site;
    }

    public void setSpider(SpiderConfig spider) {
        this.spider = spider;
    }

    public SpiderConfig getSpider() {
        return this.spider;
    }

    public void setWechat(WechatConfig wechat) {
        this.wechat = wechat;
    }

    public WechatConfig getWechat() {
        return this.wechat;
    }

    public void setRule(RuleMatchConfig rule) {
        this.rule = rule;
    }

    public RuleMatchConfig getRule() {
        return this.rule;
    }

    public List<RinseRule> getRinseRules() {
        return rinseRules;
    }

    public void setRinseRules(List<RinseRule> rinseRules) {
        this.rinseRules = rinseRules;
    }
}
