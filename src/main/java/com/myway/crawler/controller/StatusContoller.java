package com.myway.crawler.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.myway.usf.mvc.base.BaseController;
import com.myway.usf.mvc.common.ResponseResultEntity;

/**
 * 
 * 
 * @author zhangy
 * @version 2018年10月12日
 */
@RestController
public class StatusContoller extends BaseController {

    @RequestMapping("/status.do")
    public ResponseResultEntity<?> status() {
        return buildSuccessResponse();
    }
}
