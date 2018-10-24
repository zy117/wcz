package com.myway.crawler.config;

/**
 * 
 * 详情的xpath解析配置
 * 
 * @author zhangy
 * @version 2018年9月18日
 */
public class DetailXpathConfig {

    @Override
    public String toString() {
        return "DetailXpathConfig [name=" + name + ", reg=" + reg + ", value=" + value
                + ", simpleDateFormat=" + simpleDateFormat + "]";
    }

    private String name;

    private String reg;

    private String value;

    private String simpleDateFormat;

    public DetailXpathConfig(String name, String reg, String value) {
        this.name = name;
        this.reg = reg;
        this.value = value;
    }

    public DetailXpathConfig(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public DetailXpathConfig() {}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }

    public String getReg() {
        return this.reg;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public String getSimpleDateFormat() {
        return simpleDateFormat;
    }

    public void setSimpleDateFormat(String simpleDateFormat) {
        this.simpleDateFormat = simpleDateFormat;
    }
}
