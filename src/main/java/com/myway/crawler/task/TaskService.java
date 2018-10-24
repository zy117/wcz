package com.myway.crawler.task;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.myway.crawler.config.ConditionConfig;
import com.myway.crawler.config.RinseRule;
import com.myway.crawler.config.RuleMatchConfig;
import com.myway.crawler.config.SiteConfig;
import com.myway.crawler.config.SpiderConfig;
import com.myway.crawler.config.WebmagicConfig;
import com.myway.crawler.config.WechatConfig;
import com.myway.crawler.dao.WechatConfigMapper;
import com.myway.crawler.dao.WechatRinseRuleMapper;
import com.myway.crawler.downloader.WeChatDownloader;
import com.myway.crawler.pipeline.DuplicateCheckPipeline;
import com.myway.crawler.pipeline.RinseCommPipeline;
import com.myway.crawler.pipeline.WechatMsgPipeline;
import com.myway.crawler.processor.WeChatUpdatePageProcess;
import com.myway.crawler.redis.WechatStatusCache;
import com.myway.crawler.redis.WechatStatusCacheService;
import com.myway.util.common.ParamUtil;
import com.myway.util.common.StringUtil;
import com.myway.util.common.TimeUtil;
import com.myway.util.exception.MyWayException;
import us.codecraft.webmagic.Spider;

/**
 * 
 * 任务调度
 * 
 * @author zhangy
 * @version 2018年9月30日
 */
@Service
public class TaskService {
    final static Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Resource
    WeChatUpdatePageProcess weChatUpdatePageProcess;
    @Resource
    WeChatDownloader weChatDownloader;

    @Resource
    DuplicateCheckPipeline duplicateCheckPipeline;
    @Resource
    RinseCommPipeline rinseCommPipeline;
    @Resource
    WechatMsgPipeline wechatMsgPipeline;
    @Resource
    WechatConfigMapper wechatConfigMapper;
    @Resource
    WechatRinseRuleMapper wechatRinseRuleMapper;

    @Resource
    WechatStatusCacheService wechatStatusCacheService;

    /**
     * 
     * 定时任务
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void task() {
        List<WechatConfig> list = wechatConfigMapper.getConfigList();
        List<WebmagicConfig> configs = new ArrayList<>(list.size());
        for (WechatConfig wechatConfig : list) {
            WebmagicConfig webmagicConfig = new WebmagicConfig();
            webmagicConfig.setSite(SiteConfig.WECHAT_SITE_CONFIG);
            webmagicConfig.setRule(RuleMatchConfig.WECHAT_RULE_CONFIG);
            webmagicConfig.setWechat(wechatConfig);
            webmagicConfig.setCondition(ConditionConfig.defaultCondition());
            SpiderConfig spider = new SpiderConfig();
            spider.setProcesser("weChatUpdatePageProcess");
            spider.setDownloader("weChatDownloader");
            spider.setThread(1);
            spider.setSiteId(wechatConfig.getId().toString());
            spider.setStartUrl(wechatConfig.getUrl());
            webmagicConfig.setSpider(spider);
            List<RinseRule> rinseRules =
                    wechatRinseRuleMapper.getRinseRuleList(wechatConfig.getId());
            webmagicConfig.setRinseRules(rinseRules);
            configs.add(webmagicConfig);
        }
        for (WebmagicConfig config : configs) {
            try {
                Spider.create(weChatUpdatePageProcess.init(config))
                        .addUrl(config.getSpider().getStartUrl()).setDownloader(weChatDownloader)
                        .addPipeline(duplicateCheckPipeline).addPipeline(rinseCommPipeline)
                        .addPipeline(wechatMsgPipeline).thread(config.getSpider().getThread())
                        .run();
            } catch (Exception e) {
                log.error("error " + config.getWechat().getName(), e);
                WechatStatusCache wechatStatusCache = new WechatStatusCache();
                wechatStatusCache.setUid(config.getWechat().getUid());
                wechatStatusCache.setSuccess(Boolean.FALSE);
                wechatStatusCache.setCrawling(Boolean.FALSE);
                wechatStatusCache.setMsg(e.getMessage());
                wechatStatusCache.setUpdateTime(System.currentTimeMillis());
                wechatStatusCacheService.insertOrUpdateCache(config.getWechat().getUid(),
                        wechatStatusCache);
            }
        }
    }

    public void one(WechatConfig wechatConfig, String date) {
        try {
            WebmagicConfig webmagicConfig = new WebmagicConfig();
            webmagicConfig.setSite(SiteConfig.WECHAT_SITE_CONFIG);
            webmagicConfig.setRule(RuleMatchConfig.WECHAT_RULE_CONFIG);
            webmagicConfig.setWechat(wechatConfig);
            webmagicConfig.setCondition(ConditionConfig.defaultCondition());
            if (!StringUtil.isEmpty(date)) {
                Date checkDate = ParamUtil.checkMDateParam(date, "yyyy-MM-dd", "date");
                if (checkDate.after(TimeUtil.now())
                        || checkDate.before(TimeUtil.add(TimeUtil.now(), Calendar.DATE, -9))) {
                    throw new MyWayException("", "仅支持最近10天内的公众号文章");
                }
                webmagicConfig.getCondition().setStartDate(checkDate);
                webmagicConfig.getCondition().setEndDate(TimeUtil.add(checkDate, Calendar.DATE, 1));
            }
            SpiderConfig spider = new SpiderConfig();
            spider.setProcesser("weChatUpdatePageProcess");
            spider.setDownloader("weChatDownloader");
            spider.setThread(1);
            spider.setSiteId(wechatConfig.getId().toString());
            spider.setStartUrl(wechatConfig.getUrl());
            webmagicConfig.setSpider(spider);
            List<RinseRule> rinseRules =
                    wechatRinseRuleMapper.getRinseRuleList(wechatConfig.getId());
            webmagicConfig.setRinseRules(rinseRules);
            Spider.create(weChatUpdatePageProcess.init(webmagicConfig))
                    .addUrl(webmagicConfig.getSpider().getStartUrl())
                    .setDownloader(weChatDownloader).addPipeline(duplicateCheckPipeline)
                    .addPipeline(rinseCommPipeline).addPipeline(wechatMsgPipeline)
                    .thread(webmagicConfig.getSpider().getThread()).run();
        } catch (Exception e) {
            log.error("error " + wechatConfig.getName(), e);
            WechatStatusCache wechatStatusCache = new WechatStatusCache();
            wechatStatusCache.setUid(wechatConfig.getUid());
            wechatStatusCache.setSuccess(Boolean.FALSE);
            wechatStatusCache.setCrawling(Boolean.FALSE);
            wechatStatusCache.setMsg(e.getMessage());
            wechatStatusCache.setUpdateTime(System.currentTimeMillis());
            wechatStatusCacheService.insertOrUpdateCache(wechatConfig.getUid(), wechatStatusCache);
        }
    }
}
