package com.myway.crawler.pipeline;

import java.lang.invoke.MethodHandles;
import java.util.Date;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.myway.crawler.bean.WechatMsg;
import com.myway.crawler.dao.WechatMsgMapper;
import com.myway.util.common.StringUtil;
import com.myway.util.common.TimeUtil;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * 
 * 
 * @author zhangy
 * @version 2018年10月19日
 */
@Service
@Scope("prototype")
public class WechatMsgPipeline implements Pipeline {

    final static Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Resource
    WechatMsgMapper wechatMsgMapper;

    @Override
    public void process(ResultItems resultItems, Task task) {
        if (resultItems.isSkip() == false) {
            if (resultItems.get("wechatMsg") != null) {
                WechatMsg wechatMsg = resultItems.get("wechatMsg");
                wechatMsg.setBeforeContent(resultItems.get("beforeContent").toString());
                wechatMsg.setContent(resultItems.get("content").toString());
                if (wechatMsg.getDatetime() != null) {
                    wechatMsg.setPublishTime(new Date(wechatMsg.getDatetime() * 1000L));
                } else {
                    wechatMsg.setPublishTime(TimeUtil.now());
                }
                if (!StringUtil.isEmpty(wechatMsg.getTitle())) {
                    wechatMsg.setTitle(wechatMsg.getTitle().replace("%20", " "));;
                }
                if (!StringUtil.isEmpty(wechatMsg.getDigest())) {
                    wechatMsg.setDigest(wechatMsg.getDigest().replace("%20", " "));;
                }
                WechatMsg exist =
                        wechatMsgMapper.selectTitle(wechatMsg.getWechatUid(), wechatMsg.getTitle());
                if (exist == null) {
                    wechatMsgMapper.insert(wechatMsg);
                }
            }
        }
    }

}
