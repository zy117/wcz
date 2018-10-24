package com.myway.crawler.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.myway.crawler.config.RinseRule;

/**
 * 
 * 获取公众号的清洗规则
 * 
 * @author zhangy
 * @version 2018年10月9日
 */
public interface WechatRinseRuleMapper {

    List<RinseRule> getRinseRuleList(@Param("id") Integer id);
}
