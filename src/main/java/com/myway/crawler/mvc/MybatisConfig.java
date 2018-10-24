package com.myway.crawler.mvc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * 
 * @author zhangy
 * @version 2018年9月18日
 */
@Configuration
@MapperScan(basePackages = "com.myway.**.dao")
public class MybatisConfig {

}
