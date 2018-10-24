package com.myway.crawler.config;

import java.util.Date;
import com.myway.util.common.TimeUtil;

/**
 * 
 * 采集条件
 * 
 * @author zhangy
 * @version 2018年9月30日
 */
public class ConditionConfig {

    private Date startDate;
    private Date endDate;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getStartTime() {
        return startDate.getTime() / 1000L;
    }

    public Long getEndTime() {
        return endDate.getTime() / 1000L;
    }

    /**
     * 
     * 默认获取昨天的
     * 
     * @return
     */
    public final static ConditionConfig defaultCondition() {
        ConditionConfig defaultCondition = new ConditionConfig();
        defaultCondition.setStartDate(TimeUtil.yesterday());
        defaultCondition.setEndDate(TimeUtil.now());
        return defaultCondition;
    }
}
