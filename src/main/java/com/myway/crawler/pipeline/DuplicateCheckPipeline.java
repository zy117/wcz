package com.myway.crawler.pipeline;

import javax.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.myway.crawler.bean.WechatMsg;
import com.myway.crawler.dao.WechatMsgMapper;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * 
 * 重复性检查
 * 
 * @author zhangy
 * @version 2018年10月19日
 */
@Service
@Scope("prototype")
public class DuplicateCheckPipeline implements Pipeline {

    @Resource
    WechatMsgMapper wechatMsgMapper;

    @Override
    public void process(ResultItems resultItems, Task task) {
        if (resultItems.isSkip() == false) {
            if (resultItems.get("wechatMsg") != null) {
                WechatMsg wechatMsg = resultItems.get("wechatMsg");
                WechatMsg exist =
                        wechatMsgMapper.selectTitle(wechatMsg.getWechatUid(), wechatMsg.getTitle());
                if (exist != null) {
                    resultItems.setSkip(true);
                }
            }
        }
    }

}
