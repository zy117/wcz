package com.myway.crawler.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.myway.crawler.config.WechatConfig;

/**
 * 
 * 
 * @author zhangy
 * @version 2018年10月8日
 */
public interface WechatConfigMapper {

    /**
     * 
     * 获取公众号列表
     * 
     * @return
     */
    List<WechatConfig> getConfigList();

    /**
     * 
     * 根据Uid获取
     * 
     * @param uid
     * @return
     */
    WechatConfig getByUid(@Param("uid") String uid);

    /**
     * 
     * 新增
     * 
     * @param name
     * @param uid
     * @return
     */
    Integer add(@Param("name") String name, @Param("uid") String uid);

    /**
     * 
     * 删除
     * 
     * @param id
     */
    Integer delete(Integer id);
}
