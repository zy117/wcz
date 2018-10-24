package com.myway.spider.mvc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.myway.**.dao")
public class MybatisConfig {

}
