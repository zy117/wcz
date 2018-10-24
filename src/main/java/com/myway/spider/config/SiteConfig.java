package com.myway.spider.config;

/**
 * Created by qiyamac on 2017/3/24.
 */
public class SiteConfig {
	private String userAgent;

	private String domain;

	private int retry;

	private String charset;

	private int sleepTime;

	public Boolean getDynamicProxy() {
		return dynamicProxy;
	}

	public void setDynamicProxy(Boolean dynamicProxy) {
		this.dynamicProxy = dynamicProxy;
	}

	private Boolean dynamicProxy;;

	public void setUserAgent(String UserAgent) {
		this.userAgent = UserAgent;
	}

	public String getUserAgent() {
		return this.userAgent;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getDomain() {
		return this.domain;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}

	public int getRetry() {
		return this.retry;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

	public int getSleepTime() {
		return this.sleepTime;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
}
