package com.myway.crawler.bean;

import java.util.Date;

/**
 * 
 *
 * @author zhangy
 * @version 2018年10月19日
 */
public class WechatMsg {
    @Override
    public String toString() {
        return "WechartMsg [title=" + title + ", author=" + author + ", contentUrl=" + contentUrl
                + ", cover=" + cover + ", digest=" + digest + ", datetime=" + datetime + "]";
    }

    private Integer id;
    private String wechatUid;
    private String wechatName;
    /**
     * 标题
     */
    private String title;
    /**
     * 作者
     */
    private String author;
    /**
     * 详情链接
     */
    private String contentUrl;
    /**
     * 封面图
     */
    private String cover;
    /**
     * 摘要
     */
    private String digest;
    /**
     * 发布时间
     */
    private Long datetime;

    /**
     * 原内容
     */
    private String beforeContent;

    /**
     * 清洗后的内容
     */
    private String content;

    private Date publishTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public Long getDatetime() {
        return datetime;
    }

    public void setDatetime(Long datetime) {
        this.datetime = datetime;
    }

    public String getBeforeContent() {
        return beforeContent;
    }

    public void setBeforeContent(String beforeContent) {
        this.beforeContent = beforeContent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWechatUid() {
        return wechatUid;
    }

    public void setWechatUid(String wechatUid) {
        this.wechatUid = wechatUid;
    }

    public String getWechatName() {
        return wechatName;
    }

    public void setWechatName(String wechatName) {
        this.wechatName = wechatName;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
