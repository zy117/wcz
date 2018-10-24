package com.myway.crawler.controller;

import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.myway.crawler.config.WechatConfig;
import com.myway.crawler.redis.CaptchaPageCacheService;
import com.myway.crawler.redis.WechatStatusCacheService;
import com.myway.crawler.service.CrawlerAdminService;
import com.myway.crawler.service.WechatService;
import com.myway.usf.mvc.base.BaseController;
import com.myway.usf.mvc.common.ResponseResultEntity;
import com.myway.util.common.ParamUtil;

/**
 * 
 * 爬取公众号管理
 * 
 * @author zhangy
 * @version 2018年10月11日
 */
@RestController
@RequestMapping("/wechat")
public class WechatController extends BaseController {

    @Resource
    WechatService wechatService;

    @Resource
    WechatStatusCacheService wechatStatusCacheService;

    @Resource
    CrawlerAdminService crawlerAdminService;

    @Resource
    CaptchaPageCacheService captchaPageCacheService;

    /**
     * 
     * 获取公众号列表
     * 
     * @return
     */
    @RequestMapping("/getWechatList.do")
    public ResponseResultEntity<?> getWechatList() {
        return buildSuccessResponse("请求成功", wechatService.getWechatList());
    }

    /**
     * 
     * 添加公众号
     * 
     * @param AccountVO
     * @param wechatConfig
     * @return
     */
    @RequestMapping("/addWechat.do")
    public ResponseResultEntity<?> addWechat(@RequestBody WechatConfig wechatConfig) {
        ParamUtil.checkMStrParam(wechatConfig.getName(), "name", "公众号名称");
        ParamUtil.checkMStrParam(wechatConfig.getUid(), "uid", "微信号");
        wechatService.addWechat(wechatConfig.getName().trim(), wechatConfig.getUid().trim());
        return buildSuccessResponse("添加成功");
    }

    /**
     * 
     * 移除公众号
     * 
     * @param AccountVO
     * @param wechatConfig
     * @return
     */
    @RequestMapping("/deleteWechat.do")
    public ResponseResultEntity<?> deleteWechat(@RequestBody WechatConfig wechatConfig) {
        ParamUtil.checkMIntParam(wechatConfig.getId(), "id");
        wechatService.delete(wechatConfig.getId());
        return buildSuccessResponse("删除成功");
    }

    /**
     * 
     * 获取上次的结果
     * 
     * @return
     */
    @RequestMapping("/getResult.do")
    public ResponseResultEntity<?> getResult() {
        return buildSuccessResponse("请求成功", wechatStatusCacheService.getAllCache());
    }

    /**
     * 
     * 清除状态
     * 
     * @return
     */
    @RequestMapping("/reset.do")
    public ResponseResultEntity<?> reset() {
        return buildSuccessResponse("请求成功", wechatStatusCacheService.clean());
    }

    /**
     * 
     * 全部运行
     * 
     * @return
     */
    @RequestMapping("/run.do")
    public ResponseResultEntity<?> run() {
        wechatService.runAll();
        return buildSuccessResponse("执行中");
    }

    /**
     * 
     * 全部运行
     * 
     * @return
     */
    @RequestMapping("/runOne.do")
    public ResponseResultEntity<?> runOne(@RequestParam("uid") String uid,
            @RequestParam(name = "date", required = false) String date,
            @RequestParam(name = "async", defaultValue = "true") String async) {
        if ("true".equals(async)) {
            crawlerAdminService.runOne(uid, Boolean.TRUE, date);
        } else {
            crawlerAdminService.runOne(uid, Boolean.FALSE, date);
        }
        return buildSuccessResponse("执行中");
    }

    /**
     * 
     * 获取二维码
     * 
     * @return
     */
    @RequestMapping("/getCaptcha.do")
    public ResponseResultEntity<?> getCaptcha() {
        return buildSuccessResponse("请求成功", crawlerAdminService.getCaptcha());
    }

    /**
     * 
     * 设置验证码
     * 
     * @return
     */
    @RequestMapping("/setCaptcha.do")
    public ResponseResultEntity<?> setCaptcha(@RequestParam("captcha") String captcha) {
        ParamUtil.checkMStrParam(captcha, "captcha", "验证码");
        crawlerAdminService.sendCaptcha(captcha);
        return buildSuccessResponse("请求成功,请重新检查运行状态");
    }

    /**
     * 
     * 获取缓存
     * 
     * @param key
     * @return
     */
    @RequestMapping("/getCache.do")
    public ResponseResultEntity<?> getCache(@RequestParam("key") String key) {
        ParamUtil.checkMStrParam(key, "key");
        return buildSuccessResponse("请求成功", captchaPageCacheService.getCache(key));
    }
}
