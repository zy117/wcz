package com.myway.spider.processor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.myway.spider.config.DetailxpathConfig;
import com.myway.spider.config.RinseRule;
import com.myway.spider.config.RuleMatchConfig;
import com.myway.spider.config.SiteConfig;
import com.myway.spider.config.SpiderConfig;
import com.myway.spider.config.WebmagicConfig;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

/**
 * Created by qiyamac on 2017/3/24.
 */
public abstract class AbstractCommPageProcess implements PageProcessor {

    private static Logger log = LoggerFactory.getLogger(AbstractCommPageProcess.class);


    private static final String STRURLRUN_PREFIX = "starturl_";

    protected String uuid;

    public WebmagicConfig getConfig() {
        return config;
    }

    public void setConfig(WebmagicConfig config) {
        this.config = config;
    }

    private WebmagicConfig config;

    public abstract void init(WebmagicConfig config);

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
        page.putField("siteId", Long.valueOf(spiderConfig.getSiteid()));

        if (ruleMatchConfig.getDetailxpath() == null) {
            return page;
        }
        for (DetailxpathConfig rule : ruleMatchConfig.getDetailxpath()) {
            // 处理时间
            log.debug("================{}", rule.toString());
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
                    log.debug("================{}", rule.getValue());
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

    public String getStrurlrunKey() {
        return STRURLRUN_PREFIX + uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
