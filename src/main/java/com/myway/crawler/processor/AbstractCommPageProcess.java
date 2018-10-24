package com.myway.crawler.processor;

import java.lang.invoke.MethodHandles;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.myway.crawler.config.DetailXpathConfig;
import com.myway.crawler.config.RinseRule;
import com.myway.crawler.config.RuleMatchConfig;
import com.myway.crawler.config.SiteConfig;
import com.myway.crawler.config.SpiderConfig;
import com.myway.crawler.config.WebmagicConfig;
import com.myway.crawler.redis.WechatStatusCache;
import com.myway.crawler.redis.WechatStatusCacheService;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

/**
 * 
 * 通用提取
 * 
 * @author zhangy
 * @version 2018年9月18日
 */
public abstract class AbstractCommPageProcess implements PageProcessor {

    final static Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Resource
    protected WechatStatusCacheService wechatStatusCacheService;

    private WebmagicConfig config;

    public WebmagicConfig getConfig() {
        return config;
    }

    public void setConfig(WebmagicConfig config) {
        this.config = config;
    }

    /**
     * 配置站点 {@inheritDoc}
     */
    @Override
    public Site getSite() {
        SiteConfig siteConfig = this.config.getSite();
        Site site = Site.me().setDomain(siteConfig.getDomain()).setTimeOut(5000)
                .setSleepTime(siteConfig.getSleepTime()).setUserAgent(siteConfig.getUserAgent())
                .setCycleRetryTimes(siteConfig.getRetry()).setRetryTimes(siteConfig.getRetry())
                .setRetrySleepTime(siteConfig.getSleepTime());
        if (siteConfig.getCharset() != null) {
            site.setCharset(siteConfig.getCharset());
        }
        return site;
    }

    public Page setPutFile(Page page, RuleMatchConfig ruleMatchConfig) {
        setRinseRule(page);
        SpiderConfig spiderConfig = config.getSpider();
        page.putField("siteId", Long.valueOf(spiderConfig.getSiteId()));
        if (ruleMatchConfig.getDetailXpath() == null) {
            return page;
        }
        for (DetailXpathConfig rule : ruleMatchConfig.getDetailXpath()) {
            // 处理时间
            if (StringUtils.isNotEmpty(rule.getSimpleDateFormat())) {
                String publishDate = "";
                Date publishTime;
                DateFormat simpleDateFormat = new SimpleDateFormat(rule.getSimpleDateFormat());
                try {
                    if (StringUtils.isNotEmpty(rule.getValue())) {
                        Selectable node = page.getHtml().xpath(rule.getValue());
                        if (StringUtils.isNotEmpty(rule.getReg())) {
                            node = node.regex(rule.getReg());
                        }
                        publishDate = node.toString();
                    } else {
                        publishDate = page.getHtml().regex(rule.getReg()).toString();
                    }
                    publishTime = simpleDateFormat.parse(publishDate);
                    // 如果时间没有包含年份,则默认使用当前年
                    if (!((SimpleDateFormat) simpleDateFormat).toPattern().contains("yyyy")) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(publishTime);
                        calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                        publishTime = calendar.getTime();
                    }
                } catch (Exception e) {
                    // 解析错误当前时间
                    publishTime = new Date();
                }
                publishDate = simpleDateFormat.format(publishTime);
                page.putField(rule.getName(), publishDate);
            } else {
                Selectable node;
                if (StringUtils.isNotEmpty(rule.getValue())) {
                    node = page.getHtml().xpath(rule.getValue());
                    if (StringUtils.isNotEmpty(rule.getReg())) {
                        node = node.regex(rule.getReg());
                    }
                    page.putField(rule.getName(), node);
                } else {
                    page.putField(rule.getName(), page.getHtml().regex(rule.getReg()).toString());
                }
            }
        }
        return page;
    }

    public void setRinseRule(Page page) {
        List<RinseRule> rinseRules = getConfig().getRinseRules();
        if (rinseRules != null) {
            page.putField("rinseRules", rinseRules);
        }
    }

    /**
     * 
     * 初始化配置
     * 
     * @param config
     * @return
     */
    public AbstractCommPageProcess init(WebmagicConfig config) {
        setConfig(config);
        return this;
    };

    /**
     * 
     * 正在爬取
     */
    protected void updateCrawling() {
        WechatStatusCache wechatStatusCache = new WechatStatusCache();
        wechatStatusCache.setSuccess(Boolean.FALSE);
        wechatStatusCache.setCrawling(Boolean.TRUE);
        wechatStatusCache.setUpdateTime(System.currentTimeMillis());
        wechatStatusCache.setUid(config.getWechat().getUid());
        wechatStatusCacheService.insertOrUpdateCache(config.getWechat().getUid(),
                wechatStatusCache);
    }

    /**
     * 
     * 成功
     */
    protected void updateResultSuccess() {
        WechatStatusCache wechatStatusCache = new WechatStatusCache();
        wechatStatusCache.setSuccess(Boolean.TRUE);
        wechatStatusCache.setCrawling(Boolean.FALSE);
        wechatStatusCache.setUpdateTime(System.currentTimeMillis());
        wechatStatusCache.setUid(config.getWechat().getUid());
        wechatStatusCacheService.insertOrUpdateCache(config.getWechat().getUid(),
                wechatStatusCache);
    }

    /**
     * 
     * 失败
     * 
     * @param msg
     */
    protected void updateResultFail(String msg, String url) {
        WechatStatusCache wechatStatusCache = new WechatStatusCache();
        wechatStatusCache.setSuccess(Boolean.FALSE);
        wechatStatusCache.setUrl(url);
        wechatStatusCache.setMsg(msg);
        wechatStatusCache.setCrawling(Boolean.FALSE);
        wechatStatusCache.setUpdateTime(System.currentTimeMillis());
        wechatStatusCache.setUid(config.getWechat().getUid());
        wechatStatusCacheService.insertOrUpdateCache(config.getWechat().getUid(),
                wechatStatusCache);
    }
}
