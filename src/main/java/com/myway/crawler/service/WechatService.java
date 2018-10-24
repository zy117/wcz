package com.myway.crawler.service;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.myway.crawler.config.WechatConfig;
import com.myway.crawler.dao.WechatConfigMapper;
import com.myway.crawler.redis.WechatStatusCache;
import com.myway.crawler.redis.WechatStatusCacheService;
import com.myway.crawler.task.TaskService;
import com.myway.util.exception.MyWayException;

/**
 * 
 * 
 * @author zhangy
 * @version 2018年10月11日
 */
@Service
public class WechatService {

    @Resource
    WechatConfigMapper wechatConfigMapper;

    @Resource
    WechatStatusCacheService wechatStatusCacheService;

    @Resource
    TaskService taskService;

    /**
     * 
     * 新增
     * 
     * @param name
     * @param uid
     */
    @Transactional
    public void addWechat(String name, String uid) {
        WechatConfig wechatConfig = wechatConfigMapper.getByUid(uid);
        if (wechatConfig != null) {
            throw new MyWayException("", "微信号已存在");
        }
        wechatConfigMapper.add(name, uid);
    }

    /**
     * 
     * 获取公众号列表
     * 
     * @return
     */
    public List<WechatConfig> getWechatList() {
        List<WechatConfig> list = wechatConfigMapper.getConfigList();
        for (WechatConfig wechatConfig : list) {
            WechatStatusCache wechatStatusCache =
                    wechatStatusCacheService.getCache(wechatConfig.getUid());
            if (wechatStatusCache == null) {
                wechatConfig.setStatus(null);
            } else {
                if (wechatStatusCache.getSuccess()) {
                    wechatConfig.setStatus(1);
                } else if (wechatStatusCache.getCrawling()) {
                    wechatConfig.setStatus(2);
                } else {
                    wechatConfig.setStatus(3);
                }
            }
        }
        return list;
    }

    /**
     * 
     * 删除
     * 
     * @param id
     */
    @Transactional
    public void delete(Integer id) {
        wechatConfigMapper.delete(id);
    }

    /**
     * 
     * 全部运行
     */
    @Async
    public void runAll() {
        taskService.task();
    }

    @Async
    public void asyncRun(WechatConfig wechatConfig, String date) {
        taskService.one(wechatConfig, date);
    }

    public void syncRun(WechatConfig wechatConfig, String date) {
        taskService.one(wechatConfig, date);
    }

}
