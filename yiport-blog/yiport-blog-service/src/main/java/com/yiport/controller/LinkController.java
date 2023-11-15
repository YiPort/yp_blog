package com.yiport.controller;

import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
import com.yiport.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/link")
public class LinkController {

    @Autowired
    private LinkService linkService;

    /**
     * 友联查询
     * @return
     */
    @GetMapping("/getAllLink")
    @SystemLog(businessName = "查询友链列表")
    public ResponseResult getAllLink(){
        return linkService.getAllLink();
    }
}