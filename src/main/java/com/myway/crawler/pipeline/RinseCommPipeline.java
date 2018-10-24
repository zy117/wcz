package com.myway.crawler.pipeline;

import java.lang.invoke.MethodHandles;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.myway.crawler.util.RinseHtmlUtil;
import com.myway.util.common.StringUtil;
import com.myway.crawler.config.RinseRule;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * 
 * 清晰工具
 * 
 * @author zhangy
 * @version 2018年9月18日
 */
@Service
@Scope("prototype")
public class RinseCommPipeline implements Pipeline {

    final static Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void process(ResultItems resultItems, Task task) {
        if (resultItems.isSkip() == false) {
            log.info("数据根据模版规则清洗开始：url-" + resultItems.getRequest().getUrl());
            String html = resultItems.get("beforeContent").toString();
            if (StringUtil.isEmpty(html)) {
                log.info("beforeContent empty：skip-" + resultItems.getRequest().getUrl());
                resultItems.setSkip(true);
                return;
            }
            if (resultItems.get("rinseRules") != null) {
                List<RinseRule> rinseRules = resultItems.get("rinseRules");
                html = RinseHtmlUtil.rinseHtml(html, rinseRules);
            }
            html = RinseHtmlUtil.imgHandleOss(html);
            resultItems.put("content", html);
        }
    }
}
