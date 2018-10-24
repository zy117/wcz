package com.myway.spider.url;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.myway.spider.config.DetailxpathConfig;
import com.myway.spider.config.RinseRule;
import com.myway.spider.config.RuleMatchConfig;
import com.myway.spider.config.SiteConfig;
import com.myway.spider.config.SpiderConfig;
import com.myway.spider.config.WebmagicConfig;
import com.myway.spider.config.WechatConfig;
import com.myway.spider.downloader.WeChatDownloader;
import com.myway.spider.pipeline.RinseCommPipeline;
import com.myway.spider.processor.WeChatUpdatePageProcess;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

public class DxmmWechat {

    public static WebmagicConfig CONFIG;

    static {
        CONFIG = new WebmagicConfig();
        SpiderConfig spider = new SpiderConfig();
        spider.setProcesser("weChatUpdatePageProcess");
        spider.setDownloader("weChatDownloader");
        spider.setThread(1);
        spider.setSiteid("1");
        spider.setStartUrl(
                "http://weixin.sogou.com/weixin?type=1&s_from=input&query=%E4%B8%81%E9%A6%99%E5%A6%88%E5%A6%88&ie=utf8&_sug_=n&_sug_type_=");
        CONFIG.setSpider(spider);
        SiteConfig site = new SiteConfig();
        site.setDomain("mp.weixin.qq.com");
        site.setRetry(3);
        site.setSleepTime(5000);
        site.setUserAgent(
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3)AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36");
        CONFIG.setSite(site);
        WechatConfig wechat = new WechatConfig();
        wechat.setName("丁香妈妈");
        wechat.setUid("DingXiangMaMi");
        wechat.setUrl(
                "http://weixin.sogou.com/weixin?type=1&s_from=input&query=%E4%B8%81%E9%A6%99%E5%A6%88%E5%A6%88&ie=utf8&_sug_=n&_sug_type_=");
        CONFIG.setWechat(wechat);
        RuleMatchConfig rule = new RuleMatchConfig();
        rule.setDetailregex("http://mp\\.weixin\\.qq\\.com/s?\\S+");
        rule.setListregex("http://mp.weixin.qq.com/profile?src");
        rule.setLoadlistxpath("//*[@id='main']/div[4]/ul/li[1]/div/div[2]/p[1]");
        rule.setListxpath("");
        DetailxpathConfig title = new DetailxpathConfig();
        title.setName("title");
        title.setReg("");
        title.setValue("//*[@id='activity-name']/text()");
        DetailxpathConfig publicTime = new DetailxpathConfig();
        publicTime.setName("publicTime");
        publicTime.setReg("");
        publicTime.setValue("//*[@id='publish_time']/text()");
        publicTime.setSimpleDateFormat("yyyy-MM-dd");
        DetailxpathConfig author = new DetailxpathConfig();
        author.setName("author");
        author.setReg("");
        author.setValue("//*[@id='meta_content']/text()");
        DetailxpathConfig beforeContent = new DetailxpathConfig();
        beforeContent.setName("beforeContent");
        beforeContent.setReg("");
        beforeContent.setValue("//*[@id='js_content']");
        List<DetailxpathConfig> detailxpath = new ArrayList<DetailxpathConfig>();
        detailxpath.add(title);
        detailxpath.add(publicTime);
        detailxpath.add(author);
        detailxpath.add(beforeContent);
        rule.setDetailxpath(detailxpath);
        CONFIG.setRule(rule);
        List<RinseRule> rinseRules = new ArrayList<>();
        //处理图片宽高
        RinseRule imgWidth = new RinseRule();
        imgWidth.setAction("add");
        imgWidth.setType("attr");
        imgWidth.setCssquery("img");
        imgWidth.setName("width");
        imgWidth.setValue("100%");
        rinseRules.add(imgWidth);
        RinseRule imgHeight = new RinseRule();
        imgHeight.setAction("add");
        imgHeight.setType("attr");
        imgHeight.setCssquery("img");
        imgHeight.setName("height");
        imgHeight.setValue("auto");
        rinseRules.add(imgHeight);
        RinseRule imgWidthReplace = new RinseRule();
        imgWidthReplace.setAction("replace");
        imgWidthReplace.setType("attr");
        imgWidthReplace.setCssquery("img");
        imgWidthReplace.setName("style");
        imgWidthReplace.setSource("width: \\d+[.\\d+]px");
        imgWidthReplace.setTarget("width: 100%");
        rinseRules.add(imgWidthReplace);
        RinseRule imgHeightReplace = new RinseRule();
        imgHeightReplace.setAction("replace");
        imgHeightReplace.setType("attr");
        imgHeightReplace.setCssquery("img");
        imgHeightReplace.setName("style");
        imgHeightReplace.setSource("width: auto");
        imgHeightReplace.setTarget("width: 100%");
        rinseRules.add(imgHeightReplace);
        //复制视频链接
        RinseRule video = new RinseRule();
        video.setAction("copy");
        video.setType("attr");
        video.setCssquery(".video_iframe");
        video.setSource("data-src");
        video.setTarget("src");
        rinseRules.add(video);
        //复制图片链接
        RinseRule img = new RinseRule();
        img.setAction("copy");
        img.setType("attr");
        img.setCssquery("img");
        img.setSource("data-src");
        img.setTarget("src");
        rinseRules.add(img);
        //清除跳转链接
        RinseRule href = new RinseRule();
        href.setAction("delete");
        href.setCssquery("a");
        href.setType("attr");
        href.setName("href");
        rinseRules.add(href);
        //清除图片来源
        RinseRule imgSo = new RinseRule();
        imgSo.setAction("delete");
        imgSo.setCssquery("span[style*=\"color: rgb(206, 206, 206);\"]");
        imgSo.setType("node");
        rinseRules.add(imgSo);
        CONFIG.setRinseRules(rinseRules);
    }

}
