package com.myway.crawler.service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import javax.annotation.Resource;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.myway.crawler.config.WechatConfig;
import com.myway.crawler.dao.WechatConfigMapper;
import com.myway.crawler.redis.CaptchaPageCacheService;
import com.myway.crawler.redis.WechatStatusCache;
import com.myway.crawler.redis.WechatStatusCacheService;
import com.myway.util.common.StringUtil;
import com.myway.util.exception.MyWayException;

/**
 * 
 * 执行
 * 
 * @author zhangy
 * @version 2018年10月16日
 */
@Service
public class CrawlerAdminService {

    final static Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Resource
    WechatConfigMapper wechatConfigMapper;
    @Resource
    WechatService wechatService;
    @Resource
    WechatStatusCacheService wechatStatusCacheService;

    @Resource
    CaptchaPageCacheService captchaPageCacheService;

    /**
     * 私有
     */
    private static WebDriver driver;

    /**
     * 
     * 退出
     */
    protected void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    /**
     * 
     * 初始化
     * 
     * @return
     */
    protected void initDriver() {
        quitDriver();
        driver = new FirefoxDriver();
    }

    /**
     * 
     * 运行某个
     * 
     * @param uid 微信号
     * @param async 异步
     */
    public void runOne(String uid, Boolean async, String date) {
        WechatConfig wechatConfig = wechatConfigMapper.getByUid(uid);
        if (wechatConfig != null) {
            WechatStatusCache wechatStatusCache =
                    wechatStatusCacheService.getCache(wechatConfig.getUid());
            if (wechatStatusCache == null || wechatStatusCache.getCrawling() == null
                    || !wechatStatusCache.getCrawling()) {
                if (async) {
                    wechatService.asyncRun(wechatConfig, date);
                } else {
                    wechatService.syncRun(wechatConfig, date);
                }
            }
        }
    }

    /**
     * 
     * 运行全部
     */
    public void run() {
        wechatService.runAll();
    }

    public String getCaptcha() {
        String ret = null;
        List<WechatConfig> list = wechatConfigMapper.getConfigList();
        if (!list.isEmpty()) {
            WechatConfig wechatConfig = list.get(0);
            wechatService.syncRun(wechatConfig, null);
            String url = captchaPageCacheService.getCache(CaptchaPageCacheService.CAPTCHA_URL);
            if (!StringUtil.isEmpty(url)) {
                log.info("check url :{}", url);
                initDriver();
                driver.get(url);
                try {
                    WebElement webElement = driver.findElement(By.id("verify_img"));
                    if (webElement != null) {
                        log.info("check url :{}", webElement.getAttribute("src"));
                        ret = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
                        captchaPageCacheService
                                .insertOrUpdate(CaptchaPageCacheService.CAPTCHA_SCREEN_SHOT, ret);
                    } else {
                        log.info("no element");
                    }
                } catch (NoSuchElementException e) {
                    try {
                        driver.findElement(By.className("profile_info"));
                        captchaPageCacheService.clean(CaptchaPageCacheService.CAPTCHA_URL);
                        quitDriver();
                        throw new MyWayException("", "页面可正常访问");
                    } catch (NoSuchElementException e2) {
                        throw new MyWayException("", "页面已过期，请重新获取验证码");
                    }
                }
            }
        }
        return ret;
    }

    public void sendCaptcha(String captcha) {
        if (captcha.length() != 4) {
            throw new MyWayException("", "请输入4位验证码");
        }
        if (driver != null) {
            try {
                WebElement webElement = driver.findElement(By.id("verify_img"));
                log.info("check url :{}", webElement.getAttribute("src"));
                WebElement input = driver.findElement(By.id("input"));
                log.info("input {}", captcha);
                input.sendKeys(captcha);
                WebElement submit = driver.findElement(By.id("bt"));
                submit.click();
            } catch (NoSuchElementException e) {
                throw new MyWayException("", "获取验证码页面失败");
            }
            try {
                // 验证结果
                Thread.sleep(1000);
                String base64 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
                captchaPageCacheService.insertOrUpdate(CaptchaPageCacheService.RESULT_SCREEN_SHOT,
                        base64);
                driver.findElement(By.id("verify_result"));
                log.warn("new captcha is find!");
                captchaPageCacheService.insertOrUpdate(CaptchaPageCacheService.CAPTCHA_SCREEN_SHOT,
                        base64);
                throw new MyWayException("", "验证码输入错误:" + base64);
            } catch (InterruptedException e) {
                throw new MyWayException("", "验证失败");
            } catch (NoSuchElementException e) {
                log.info("set captcha success");
                captchaPageCacheService.clean(CaptchaPageCacheService.CAPTCHA_URL);
                quitDriver();
            }
        } else {
            log.warn("driver is null");
            throw new MyWayException("", "请重新获取验证码");
        }
    }
}
