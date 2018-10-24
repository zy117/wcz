package com.myway.spider.pipeline;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.myway.spider.util.RinseHtmlUtil;
import com.myway.spider.config.RinseRule;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * Created by qiyalm on 17/3/24.
 */
@Service
@Scope("prototype")
public class RinseCommPipeline implements Pipeline {

    private static Logger log = LoggerFactory.getLogger(RinseCommPipeline.class);

    @Override
    public void process(ResultItems resultItems, Task task) {
        if (resultItems.isSkip() == false) {

            log.info("数据根据模版规则清洗开始：url-" + resultItems.getRequest().getUrl());
            String html = resultItems.get("beforeContent").toString();
            // 通用模版清洗
            if (resultItems.get("commRinseRules") != null) {
                List<RinseRule> rinseRules = resultItems.get("commRinseRules");
                html = RinseHtmlUtil.rinseHtml(html, rinseRules);
            }
            if (resultItems.get("rinseRules") != null) {
                List<RinseRule> rinseRules = resultItems.get("rinseRules");
                html = RinseHtmlUtil.rinseHtml(html, rinseRules);

            }
            // 设置图片绝对地址
            // html=RinseHtmlUtil.absImgUrl(html,resultItems.getRequest().getUrl());
            resultItems.put("content", html);
        }
    }
}
