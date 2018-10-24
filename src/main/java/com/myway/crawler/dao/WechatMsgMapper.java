package com.myway.crawler.dao;

import org.apache.ibatis.annotations.Param;
import com.myway.crawler.bean.WechatMsg;

/**
 * 
 * 
 * @author zhangy
 * @version 2018年10月19日
 */
public interface WechatMsgMapper {

    Integer insert(WechatMsg wechartMsg);

    WechatMsg selectTitle(@Param("wechatUid") String wechatUid, @Param("title") String title);
}
