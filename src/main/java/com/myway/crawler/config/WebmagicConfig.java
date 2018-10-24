package com.myway.crawler.config;

import java.util.*;

/**
 * 
 * 爬虫配置
 * 
 * @author zhangy
 * @version 2018年9月18日
 */
public class WebmagicConfig {
    private SiteConfig site;

    private SpiderConfig spider;

    private WechatConfig wechat;

    private RuleMatchConfig rule;

    private List<RinseRule> rinseRules;

    private ConditionConfig condition;

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

    public ConditionConfig getCondition() {
        return condition;
    }

    public void setCondition(ConditionConfig condition) {
        this.condition = condition;
    }
}
