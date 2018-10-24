package com.myway.crawler.constant;

/**
 * 
 * 异常消息
 * 
 * @author zhangy
 * @version 2018年10月11日
 */
public interface Message {

    String WECHAT_NOT_FIND = "搜狗查询没有查询到相关公众号!";

    String SERACH_PAGE_FIND_CAPCHA = "搜狗查询公众号出现验证码!";

    String LIST_PAGE_FIND_CAPCHA = "微信公众号爬取异常，系统检测到爬虫，请输入验证码！";

    String EXCEPTION_FIND = "未知异常";
}
