package com.myway.crawler.processor;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myway.crawler.bean.WechatMsg;
import com.myway.crawler.constant.Message;
import com.myway.crawler.redis.CaptchaPageCacheService;
import com.myway.crawler.util.RinseHtmlUtil;
import com.myway.util.common.JSONUtil;
import com.myway.util.common.StringUtil;
import us.codecraft.webmagic.Page;

/**
 * 
 * 微信公众号提取
 * 
 * @author zhangy
 * @version 2018年9月18日
 */
@Service
@Scope("prototype")
public class WeChatUpdatePageProcess extends AbstractCommPageProcess {

    final static Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Resource
    CaptchaPageCacheService captchaPageCacheService;

    /**
     * 延迟，ms
     */
    private String delay = "10000:15000:20000";

    public WeChatUpdatePageProcess() {}

    /**
     * 解析页面
     */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void process(Page page) {
        if (page.getUrl().get().equals(getConfig().getSpider().getStartUrl())
                && !(page.getUrl().regex(getConfig().getRule().getDetailRegex()).match())) {
            // 进入搜狗
            updateCrawling();
            page.getResultItems().setSkip(true);
            String uid = page.getHtml()
                    .xpath("//*[@id=\"main\"]/div[4]/ul/li[1]/div/div[2]/p[2]/label/text()").get();
            log.debug("==================================uid:{}", uid);
            if (StringUtils.isNotEmpty(uid) && uid.equals(getConfig().getWechat().getUid())) {
                // 打开公众号
                page.addTargetRequest(page.getHtml().xpath(getConfig().getRule().getLoadListXpath())
                        .links().toString() + "&f=json");
            } else {
                String seccodeImage = page.getHtml().xpath("//*[@id=\"seccodeImage\"]").get();
                if (StringUtils.isNotEmpty(seccodeImage)) {
                    log.error(Message.SERACH_PAGE_FIND_CAPCHA);
                    updateResultFail(Message.SERACH_PAGE_FIND_CAPCHA, page.getUrl().get());
                } else {
                    log.error(Message.WECHAT_NOT_FIND);
                    updateResultFail(Message.WECHAT_NOT_FIND, page.getUrl().get());
                }
            }
        } else if (page.getUrl().toString().indexOf(getConfig().getRule().getListRegex()) == 0) {
            // 公众号列表页
            page.getResultItems().setSkip(true);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String json = page.getRawText();
                if (json == null || json.length() <= 2) {
                    handleListError(page);
                    return;
                } else {
                    Map map = objectMapper.readValue(json, Map.class);
                    if (map.get("ret") == null) {
                        handleListError(page);
                        return;
                    } else if ("0".equals(map.get("ret").toString())) {
                        String msg = String.valueOf(map.get("general_msg_list"));
                        Map msgmap = objectMapper.readValue(msg, Map.class);
                        List<Map> mapList = (List<Map>) msgmap.get("list");
                        if (mapList != null && mapList.size() > 0) {
                            for (Map msgMap : mapList) {
                                Long datetime = extractDateTime(msgMap);
                                if (datetime == null) {
                                    continue;
                                }
                                // 只获取昨日的更新
                                if (this.getConfig().getCondition() != null) {
                                    Long startTime = this.getConfig().getCondition().getStartTime();
                                    Long endTime = this.getConfig().getCondition().getEndTime();
                                    if (datetime.compareTo(startTime) < 0
                                            || datetime.compareTo(endTime) > 0) {
                                        continue;
                                    }
                                }
                                if (msgMap.containsKey("app_msg_ext_info")) {
                                    Map urlmap = (Map) msgMap.get("app_msg_ext_info");
                                    if (urlmap.get("content_url") != null
                                            && urlmap.get("content_url").toString().length() > 0) {
                                        WechatMsg wechatMsg = extractItem(urlmap);
                                        wechatMsg.setDatetime(datetime);
                                        if (!StringUtil.isEmpty(wechatMsg.getContentUrl())) {
                                            page.addTargetRequest(wechatMsg.getContentUrl() + "预处理"
                                                    + JSONUtil.toJSON(wechatMsg));
                                        }
                                    }
                                    int isMulti =
                                            Integer.valueOf(urlmap.get("is_multi").toString());
                                    if (1 == isMulti) {
                                        List<Map> multimapList =
                                                (List<Map>) urlmap.get("multi_app_msg_item_list");
                                        for (Map itemMap : multimapList) {
                                            if (itemMap.get("content_url") != null && itemMap
                                                    .get("content_url").toString().length() > 0) {
                                                WechatMsg wechatMsg = extractItem(itemMap);
                                                wechatMsg.setDatetime(datetime);
                                                if (!StringUtil
                                                        .isEmpty(wechatMsg.getContentUrl())) {
                                                    page.addTargetRequest(wechatMsg.getContentUrl()
                                                            + "预处理" + JSONUtil.toJSON(wechatMsg));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error(Message.EXCEPTION_FIND, e);
                updateResultFail(Message.WECHAT_NOT_FIND, page.getUrl().get());
            }
        } else if (page.getUrl().regex(getConfig().getRule().getDetailRegex()).match()) {
            // 详细页面
            setPutFile(page, getConfig().getRule());
            updateResultSuccess();
        } else {
            page.getResultItems().setSkip(true);
        }
        String[] times = delay.split(":");
        List<Long> list = new ArrayList<Long>();
        for (String s : times) {
            try {
                Long time = Long.valueOf(s);
                list.add(time);
            } catch (Exception e) {
                log.error("===", e);
            }
        }
        if (list.size() > 0) {
            Random random = new Random();
            int i = random.nextInt(list.size());
            try {
                Thread.sleep(list.get(i));
            } catch (InterruptedException var3) {
                log.error("===", var3);
            }
        }
    }

    /**
     * 
     * 处理列表页异常
     * 
     * @param page
     */
    private void handleListError(Page page) {
        log.error(Message.LIST_PAGE_FIND_CAPCHA);
        updateResultFail(Message.LIST_PAGE_FIND_CAPCHA, page.getUrl().get());
        String url = page.getUrl().get();
        if (url.contains("&f=json")) {
            url = url.replace("&f=json", "");
        }
        // 将验证码页面存入缓存
        captchaPageCacheService.insertOrUpdateCache(CaptchaPageCacheService.CAPTCHA_URL, url);
    }

    @SuppressWarnings("rawtypes")
    private Long extractDateTime(Map msgMap) {
        if (msgMap.containsKey("comm_msg_info")) {
            Map comMap = (Map) msgMap.get("comm_msg_info");
            if (comMap.get("datetime") != null) {
                return Long.valueOf(comMap.get("datetime").toString());
            }
        }
        return null;
    }

    /**
     * 
     * 提取内容
     * 
     * @param urlmap
     * @return
     */
    @SuppressWarnings("rawtypes")
    private WechatMsg extractItem(Map urlmap) {
        WechatMsg wechatMsg = new WechatMsg();
        // 链接
        wechatMsg.setContentUrl(urlmap.get("content_url").toString().replaceAll("&amp;", "&"));
        if (urlmap.get("cover") != null && urlmap.get("cover").toString().length() > 0) {
            String coverUrl = urlmap.get("cover").toString().trim();
            String ossUrl = RinseHtmlUtil.uploadImgToOss(coverUrl);
            wechatMsg.setCover(ossUrl);
        }
        if (urlmap.get("author") != null && urlmap.get("author").toString().length() > 0) {
            wechatMsg.setAuthor(urlmap.get("author").toString().trim());
        }
        if (urlmap.get("title") != null && urlmap.get("title").toString().length() > 0) {
            wechatMsg.setTitle(urlmap.get("title").toString().trim());
        }
        if (urlmap.get("digest") != null && urlmap.get("digest").toString().length() > 0) {
            wechatMsg.setDigest(urlmap.get("digest").toString().trim());
        }
        wechatMsg.setWechatName(getConfig().getWechat().getName());
        wechatMsg.setWechatUid(getConfig().getWechat().getUid());
        return wechatMsg;
    }
}
